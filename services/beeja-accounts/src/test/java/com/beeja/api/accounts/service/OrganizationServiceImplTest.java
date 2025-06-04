package com.beeja.api.accounts.service;

import static org.bouncycastle.asn1.x509.X509ObjectIdentifiers.organization;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.beeja.api.accounts.clients.FileClient;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.*;
import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.*;
import com.beeja.api.accounts.response.FileDownloadResultMetaData;
import com.beeja.api.accounts.response.OrganizationResponse;
import com.beeja.api.accounts.serviceImpl.OrganizationServiceImpl;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import java.lang.reflect.Method;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceImplTest {

  @Mock
  private OrganizationRepository organizationRepository;

  @Mock
  private UserRepository userRepository;

  @Spy
  @InjectMocks
  private OrganizationServiceImpl organizationService;

  @Mock
  private UserContext userContext;

  @Mock
  private OrgDefaultsRepository orgDefaultsRepository;

  @Mock
  private FileClient fileClient;


  private MockedStatic<UserContext> mockUserContext;

  private static final String ORGANIZATION_ID = "testOrgId";

  @BeforeEach
  public void init() {

    MockitoAnnotations.initMocks(this);
    mockUserContext = mockStatic(UserContext.class);
  }

  @AfterEach
  void tearDown() {
    if (mockUserContext != null) {
      mockUserContext.close();
    }
  }


  @Test
  void testGetAllUsersByOrganizationId_Success() throws Exception {

    Organization organization = new Organization();
    organization.setId(ORGANIZATION_ID);
    Optional<Organization> orgOptional = Optional.of(organization);
    User user = new User();
    when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(orgOptional);
    when(userRepository.findByOrganizationsId(ORGANIZATION_ID)).thenReturn(List.of(user));
    List<User> users = organizationService.getAllUsersByOrganizationId(ORGANIZATION_ID);

    assertNotNull(users);
    assertEquals(1, users.size());
    verify(organizationRepository).findById(ORGANIZATION_ID);
    verify(userRepository).findByOrganizationsId(ORGANIZATION_ID);
  }

  @Test
  void testGetAllUsersByOrganizationId_OrganizationFetchError() {
    when(organizationRepository.findById(ORGANIZATION_ID)).thenThrow(new RuntimeException("DB error"));
    Exception exception = assertThrows(Exception.class, () -> {
      organizationService.getAllUsersByOrganizationId(ORGANIZATION_ID);
    });

    assertFalse(exception.getMessage().contains("Unable to fetch details"));
    verify(organizationRepository).findById(ORGANIZATION_ID);
    verify(userRepository, never()).findByOrganizationsId(ORGANIZATION_ID);
  }

  @Test
  void testGetAllUsersByOrganizationId_UserFetchError() throws Exception {
    Organization organization = new Organization();
    organization.setId(ORGANIZATION_ID);
    Optional<Organization> orgOptional = Optional.of(organization);
    when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(orgOptional);
    when(userRepository.findByOrganizationsId(ORGANIZATION_ID)).thenThrow(new RuntimeException("DB error"));
    Exception exception = assertThrows(Exception.class, () -> {
      organizationService.getAllUsersByOrganizationId(ORGANIZATION_ID);
    });

    assertFalse(exception.getMessage().contains("Unable to fetch details"));
    verify(organizationRepository).findById(ORGANIZATION_ID);
    verify(userRepository).findByOrganizationsId(ORGANIZATION_ID);
  }

  @Test
  public void testGetOrganizationById_SuccessWithPermission() throws Exception {
    String loggedInUserOrgId = "5678";
    OrganizationResponse organizationResponse = new OrganizationResponse();
    organizationResponse.setId(ORGANIZATION_ID);
    when(userContext.getLoggedInUserPermissions()).thenReturn(Collections.singleton(PermissionConstants.READ_ALL_ORGANIZATIONS));
    when(organizationRepository.findByOrganizationId(ORGANIZATION_ID)).thenReturn(organizationResponse);
    OrganizationResponse result = organizationService.getOrganizationById(ORGANIZATION_ID);
    assertEquals(ORGANIZATION_ID, result.getId());
    verify(organizationRepository, times(1)).findByOrganizationId(ORGANIZATION_ID);
  }

  @Test
  public void testGetOrganizationById_DatabaseError() throws Exception {
    String loggedInUserOrgId = "5678";
    when(userContext.getLoggedInUserPermissions()).thenReturn(Collections.singleton(PermissionConstants.READ_ALL_ORGANIZATIONS));
    when(organizationRepository.findByOrganizationId(ORGANIZATION_ID)).thenThrow(new RuntimeException("DB error"));
    Exception exception = assertThrows(Exception.class, () -> {
      organizationService.getOrganizationById(ORGANIZATION_ID);
    });
    assertFalse(exception.getMessage().contains("Unable to fetch details"));
    verify(organizationRepository, times(1)).findByOrganizationId(ORGANIZATION_ID);
  }

  @Test
  void testUpdateOrganization_SuccessWithPermission() throws Exception {
    String organizationId = "org1";
    String fields = "{\"preferences\": {\"theme\": \"DARK\"}}"; // Example JSON
    MultipartFile file = null;

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);

    when(userContext.getLoggedInUserPermissions())
            .thenReturn(Collections.singleton(PermissionConstants.UPDATE_ALL_ORGANIZATIONS));
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
    when(organizationRepository.save(any(Organization.class))).thenReturn(mockOrganization);

    Organization result = organizationService.updateOrganization(organizationId, fields, file);

    assertNotNull(result);
    verify(organizationRepository, times(1)).save(mockOrganization);
  }

  @Test
  void testUpdateOrganization_SuccessWithoutPermission() throws Exception {
    String organizationId = "org1";
    String fields = "{\"preferences\": {\"theme\": \"DARK\"}}";
    MultipartFile file = null;

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);

    when(userContext.getLoggedInUserPermissions()).thenReturn(Collections.emptySet());
    when(userContext.getLoggedInUserOrganization()).thenReturn(mockOrganization);
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
    when(organizationRepository.save(any(Organization.class))).thenReturn(mockOrganization);

    Organization result = organizationService.updateOrganization(organizationId, fields, file);

    assertNotNull(result);
    verify(organizationRepository, times(1)).save(mockOrganization);
  }

  @Test
  public void testGetMetaData_AllHeadersPresent() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.png\"");
    headers.add("createdby", "admin");
    headers.add("organizationid", "org123");
    headers.add("entityId", "entity456");

    ResponseEntity<byte[]> fileResponse = ResponseEntity.ok().headers(headers).body(new byte[]{});
    Method getMetaDataMethod =
            OrganizationServiceImpl.class.getDeclaredMethod("getMetaData", ResponseEntity.class);
    getMetaDataMethod.setAccessible(true);
    FileDownloadResultMetaData metadata =
            (FileDownloadResultMetaData) getMetaDataMethod.invoke(null, fileResponse);
    assertEquals("test.png", metadata.getFileName());
    assertEquals("admin", metadata.getCreatedBy());
    assertEquals("entity456", metadata.getEntityId());
    assertEquals("org123", metadata.getOrganizationId());
  }



  @Test
  void testGetOrganizationValuesByKey_Success() throws Exception {
    String key = "employeeSettings";
    String organizationId = "org123";

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);
    mockUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
    OrgValues orgValue1 = new OrgValues("value1", "description1");
    OrgValues orgValue2 = new OrgValues("value2", "description2");
    Set<OrgValues> orgValuesSet = new HashSet<>(Arrays.asList(orgValue1, orgValue2));

    OrgDefaults mockOrgDefaults = new OrgDefaults();
    mockOrgDefaults.setId("default1");
    mockOrgDefaults.setOrganizationId(organizationId);
    mockOrgDefaults.setKey(key);
    mockOrgDefaults.setValues(orgValuesSet);
    when(orgDefaultsRepository.findByOrganizationIdAndKey(organizationId, key))
            .thenReturn(mockOrgDefaults);
    OrgDefaults result = organizationService.getOrganizationValuesByKey(key);
    assertNotNull(result);
    assertEquals(key, result.getKey());
    assertEquals(2, result.getValues().size());
    assertTrue(result.getValues().contains(orgValue1));
    assertTrue(result.getValues().contains(orgValue2));
    verify(orgDefaultsRepository, times(1)).findByOrganizationIdAndKey(organizationId, key);
  }

  @Test
  void testGetOrganizationValuesByKey_Exception() {
    String key = "employeeSettings";
    String organizationId = "org123";

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);
    mockUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
    when(orgDefaultsRepository.findByOrganizationIdAndKey(organizationId, key))
            .thenThrow(new RuntimeException("Database error"));
    Exception exception = assertThrows(Exception.class, () -> {
      organizationService.getOrganizationValuesByKey(key);
    });
    assertTrue(exception.getMessage().contains(Constants.ERROR_IN_UPDATING_ORGANIZATION));
    verify(orgDefaultsRepository, times(1)).findByOrganizationIdAndKey(organizationId, key);
  }

  @Test
  public void updateOrganizationValues_Success() throws Exception {

    Organization mockOrganization = new Organization();
    mockOrganization.setId("org1");

    OrgValues orgValue = new OrgValues("valueData", "Value description");
    Set<OrgValues> values = new HashSet<>();
    values.add(orgValue);

    OrgDefaults orgDefaults = new OrgDefaults();
    orgDefaults.setKey("key1");
    orgDefaults.setValues(values);

    OrgDefaults existingOrgDefaults = new OrgDefaults();
    existingOrgDefaults.setKey("key1");
    existingOrgDefaults.setValues(values);

    mockUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

    when(orgDefaultsRepository.findByOrganizationIdAndKey("org1", "key1"))
            .thenReturn(existingOrgDefaults);
    when(orgDefaultsRepository.save(any(OrgDefaults.class))).thenReturn(existingOrgDefaults);
    OrgDefaults result = organizationService.updateOrganizationValues(orgDefaults);
    assertNotNull(result);
    assertEquals("key1", result.getKey());
    assertEquals(1, result.getValues().size());
    assertEquals("valueData", result.getValues().iterator().next().getValue());
    assertEquals("Value description", result.getValues().iterator().next().getDescription());
    verify(orgDefaultsRepository, times(1)).save(existingOrgDefaults);
  }

  @Test
  public void updateOrganizationValues_CreateNewOrgDefaults() throws Exception {
    Organization mockOrganization = new Organization();
    mockOrganization.setId("org1");
    OrgValues orgValue = new OrgValues("valueData", "Value description");
    Set<OrgValues> values = new HashSet<>();
    values.add(orgValue);

    OrgDefaults orgDefaults = new OrgDefaults();
    orgDefaults.setKey("key1");
    orgDefaults.setValues(values);
    mockUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
    when(orgDefaultsRepository.findByOrganizationIdAndKey("org1", "key1")).thenReturn(null);
    when(orgDefaultsRepository.save(any(OrgDefaults.class))).thenReturn(orgDefaults);
    OrgDefaults result = organizationService.updateOrganizationValues(orgDefaults);
    assertNotNull(result);
    assertEquals("key1", result.getKey());
    assertEquals(1, result.getValues().size());
    assertEquals("valueData", result.getValues().iterator().next().getValue());
    assertEquals("Value description", result.getValues().iterator().next().getDescription());
    verify(orgDefaultsRepository, times(1)).save(any(OrgDefaults.class));
  }


  @Test
  public void updateOrganizationValues_ExceptionWhileUpdating() {
    Organization mockOrganization = new Organization();
    mockOrganization.setId("org1");
    OrgValues orgValue = new OrgValues("valueData", "Value description");
    Set<OrgValues> values = new HashSet<>();
    values.add(orgValue);

    OrgDefaults orgDefaults = new OrgDefaults();
    orgDefaults.setKey("key1");
    orgDefaults.setValues(values);

    OrgDefaults existingOrgDefaults = new OrgDefaults();
    existingOrgDefaults.setKey("key1");
    existingOrgDefaults.setValues(new HashSet<>());
    mockUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
    when(orgDefaultsRepository.findByOrganizationIdAndKey("org1", "key1")).thenReturn(existingOrgDefaults);
    when(orgDefaultsRepository.save(any(OrgDefaults.class))).thenThrow(new RuntimeException("Database error"));
    Exception exception = assertThrows(Exception.class, () -> {
      organizationService.updateOrganizationValues(orgDefaults);
    });

    assertTrue(exception.getMessage().contains(Constants.ERROR_IN_UPDATING_ORGANIZATION));
    verify(orgDefaultsRepository, times(1)).save(existingOrgDefaults);
  }


  @Test
  void testDownloadOrganizationFile_Success() throws Exception {
    String logoFileId = "someFileId";
    Organization mockOrganization = new Organization();
    mockOrganization.setLogoFileId(logoFileId);
    mockUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

    byte[] fileData = "sample file content".getBytes();
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"logo.png\"");
    ResponseEntity<byte[]> fileResponse = new ResponseEntity<>(fileData, headers, HttpStatus.OK);

    when(fileClient.downloadFile(logoFileId)).thenReturn(fileResponse);

    ByteArrayResource result = organizationService.downloadOrganizationFile();
    assertNotNull(result);
    assertEquals("logo.png", result.getFilename());
    assertArrayEquals(fileData, result.getByteArray());
  }

  @Test
  void testDownloadOrganizationFile_LogoFileIdNull() throws Exception {
    Organization mockOrganization = new Organization();
    mockOrganization.setLogoFileId(null);
    mockUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

    Exception exception = assertThrows(Exception.class, () -> {
      organizationService.downloadOrganizationFile();
    });
    assertFalse(exception.getMessage().contains(Constants.ORGANIZATION_LOGO_NOT_FOUND));
  }


  @Test
  void testGetMetaData_MissingHeaders() throws Exception {
    HttpHeaders headers = new HttpHeaders(); // No headers set

    ResponseEntity<byte[]> fileResponse = ResponseEntity.ok().headers(headers).body(new byte[]{});
    Method getMetaDataMethod = OrganizationServiceImpl.class.getDeclaredMethod("getMetaData", ResponseEntity.class);
    getMetaDataMethod.setAccessible(true);

    FileDownloadResultMetaData metadata = (FileDownloadResultMetaData) getMetaDataMethod.invoke(null, fileResponse);

    assertNull(metadata.getFileName());
    assertNull(metadata.getCreatedBy());
    assertNull(metadata.getEntityId());
    assertNull(metadata.getOrganizationId());
  }

  @Test
  public void testUpdateOrganization_OrganizationNotFound() throws Exception {
    String organizationId = "nonexistentOrg";
    String fields = "{\"preferences\": {\"theme\": \"DARK\"}}";
    MultipartFile file = null;

    when(UserContext.getLoggedInUserPermissions())
            .thenReturn(Collections.singleton(PermissionConstants.UPDATE_ALL_ORGANIZATIONS));
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> organizationService.updateOrganization(organizationId, fields, file));
    verify(organizationRepository, never()).save(any(Organization.class));
  }
  @Test
  public void testUpdateOrganization_InvalidFieldInJson() throws Exception {
    String organizationId = "org1";
    String fields = "{\"invalidField\": \"value\"}";
    MultipartFile file = null;

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);

    when(UserContext.getLoggedInUserPermissions())
            .thenReturn(Collections.singleton(PermissionConstants.UPDATE_ALL_ORGANIZATIONS));
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));

    assertThrows(BadRequestException.class, () -> organizationService.updateOrganization(organizationId, fields, file));
    verify(organizationRepository, never()).save(any(Organization.class));
  }

  @Test
  public void testUpdateOrganization_JsonParsingError() throws Exception {
    String organizationId = "org1";
    String fields = "invalid json";
    MultipartFile file = null;

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);

    when(UserContext.getLoggedInUserPermissions())
            .thenReturn(Collections.singleton(PermissionConstants.UPDATE_ALL_ORGANIZATIONS));
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));

    assertThrows(Exception.class, () -> organizationService.updateOrganization(organizationId, fields, file));
    verify(organizationRepository, never()).save(any(Organization.class));
  }

  @Test
  public void testUpdateOrganization_FileClientUploadError() throws Exception {
    String organizationId = "org1";
    String fields = null;
    MultipartFile file = mock(MultipartFile.class);

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);

    when(UserContext.getLoggedInUserPermissions())
            .thenReturn(Collections.singleton(PermissionConstants.UPDATE_ALL_ORGANIZATIONS));
    when(UserContext.getLoggedInUserOrganization()).thenReturn(mockOrganization);
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
    when(fileClient.uploadFile(any())).thenThrow(new RuntimeException("File upload failed"));

    assertThrows(Exception.class, () -> organizationService.updateOrganization(organizationId, fields, file));
    verify(organizationRepository, never()).save(any(Organization.class));
  }

  @Test
  public void testUpdateOrganization_FileClientUpdateError() throws Exception {
    String organizationId = "org1";
    String fields = null;
    MultipartFile file = mock(MultipartFile.class);

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);
    mockOrganization.setLogoFileId("file123");

    when(UserContext.getLoggedInUserPermissions())
            .thenReturn(Collections.singleton(PermissionConstants.UPDATE_ALL_ORGANIZATIONS));
    when(UserContext.getLoggedInUserOrganization()).thenReturn(mockOrganization);
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
    when(fileClient.updateFile(anyString(), any())).thenThrow(new RuntimeException("File update failed"));

    assertThrows(Exception.class, () -> organizationService.updateOrganization(organizationId, fields, file));
    verify(organizationRepository, never()).save(any(Organization.class));
  }

  @Test
  public void testUpdateOrganization_SaveOrganizationError() throws Exception {
    String organizationId = "org1";
    String fields = "{\"preferences\": {\"theme\": \"DARK\"}}";
    MultipartFile file = null;

    Organization mockOrganization = new Organization();
    mockOrganization.setId(organizationId);

    when(UserContext.getLoggedInUserPermissions())
            .thenReturn(Collections.singleton(PermissionConstants.UPDATE_ALL_ORGANIZATIONS));
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
    when(organizationRepository.save(any(Organization.class))).thenThrow(new RuntimeException("Save failed"));

    assertThrows(Exception.class, () -> organizationService.updateOrganization(organizationId, fields, file));
    verify(organizationRepository, times(1)).save(any(Organization.class));
  }





}