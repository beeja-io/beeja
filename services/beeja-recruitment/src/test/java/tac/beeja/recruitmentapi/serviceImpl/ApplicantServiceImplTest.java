package tac.beeja.recruitmentapi.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.response.PaginatedApplicantResponse;

class ApplicantServiceImplTest {

  @InjectMocks private ApplicantServiceImpl applicantServiceImpl;

  @Mock private MongoTemplate mongoTemplate;

  private Applicant applicant;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    applicant = new Applicant();
    applicant.setId("1");
    applicant.setFirstName("John");
    applicant.setLastName("Doe");
    applicant.setEmail("john@example.com");
    applicant.setPhoneNumber("1234567890");
    applicant.setPositionAppliedFor("Software Engineer");
    applicant.setStatus(ApplicantStatus.APPLIED);
    applicant.setExperience("5 years");
    applicant.setApplicantId("Employee1");
    applicant.setCreatedAt(new Date());
  }

  @Test
  public void testGetPaginatedApplicants_ValidPagination() {
    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.singletonList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(1L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, null, null, null, null, null, "createdAt", "asc");

    assertEquals(1, response.getApplicants().size());
    assertEquals(1, response.getCurrentPage());
    assertEquals(10, response.getPageSize());
    assertEquals(1, response.getTotalRecords());
    assertEquals(1, response.getTotalPages());
  }

  @Test
  public void testGetPaginatedApplicants_NoResults() {
    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.emptyList());
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(0L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, null, null, null, null, null, "createdAt", "asc");

    assertTrue(response.getApplicants().isEmpty());
    assertEquals(1, response.getCurrentPage());
    assertEquals(10, response.getPageSize());
    assertEquals(0, response.getTotalRecords());
    assertEquals(0, response.getTotalPages());
  }

  @Test
  public void testGetPaginatedApplicants_WithFilters() {
    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.singletonList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(1L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, "John", null, null, null, null, null, "createdAt", "asc");

    assertEquals(1, response.getApplicants().size());
    assertEquals("John", response.getApplicants().get(0).getFirstName());
  }

  @Test
  public void testGetPaginatedApplicants_WithApplicantIdFilter() {
    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.singletonList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(1L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, "Employee1", null, null, null, null, null, null, "createdAt", "asc");

    assertEquals(1, response.getApplicants().size());
    assertEquals("Employee1", response.getApplicants().get(0).getApplicantId());
  }

  @Test
  public void testGetPaginatedApplicants_WithPositionAppliedForFilter() {
    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.singletonList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(1L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, "Software Engineer", null, null, null, null, "createdAt", "asc");

    assertEquals(1, response.getApplicants().size());
    assertEquals("Software Engineer", response.getApplicants().get(0).getPositionAppliedFor());
  }

  @Test
  public void testGetPaginatedApplicants_WithStatusFilter() {
    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Arrays.asList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class)))
        .thenReturn((long) Arrays.asList(applicant).size());

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, null, ApplicantStatus.APPLIED, null, null, null, "createdAt", "asc");

    assertEquals(1, response.getApplicants().size());
    assertEquals(ApplicantStatus.APPLIED, response.getApplicants().get(0).getStatus());
  }

  @Test
  public void testGetPaginatedApplicants_WithExperianceFilter() {
    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Arrays.asList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class)))
        .thenReturn((long) Arrays.asList(applicant).size());

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, null, null, "5 years", null, null, "createdAt", "asc");

    assertEquals(1, response.getApplicants().size());
    assertEquals("5 years", response.getApplicants().get(0).getExperience());
  }

  @Test
  public void testGetPaginatedApplicants_WithDateFilters() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.JANUARY, 1);
    Date fromDate = cal.getTime();

    cal.set(2024, Calendar.DECEMBER, 31);
    Date toDate = cal.getTime();

    cal.set(2024, Calendar.JUNE, 15);
    Date createdAt = cal.getTime();
    applicant.setCreatedAt(createdAt);

    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.singletonList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(1L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, null, null, null, fromDate, toDate, "createdAt", "asc");

    assertFalse(response.getApplicants().isEmpty());
    Date retrievedCreatedAt = response.getApplicants().get(0).getCreatedAt();

    assertTrue(!retrievedCreatedAt.before(fromDate) && !retrievedCreatedAt.after(toDate));
  }

  @Test
  public void testGetPaginatedApplicants_WithFromDateOnly() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.JANUARY, 1);
    Date fromDate = cal.getTime();

    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.singletonList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(1L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, null, null, null, fromDate, null, "createdAt", "asc");

    assertFalse(response.getApplicants().isEmpty());
    Date createdAt = response.getApplicants().get(0).getCreatedAt();
    assertTrue(
        createdAt.after(fromDate) || createdAt.equals(fromDate),
        "Applicant createdAt should be after or equal to fromDate");
  }

  @Test
  public void testGetPaginatedApplicants_WithToDateOnly() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.DECEMBER, 31);
    Date toDate = cal.getTime();

    cal.set(2024, Calendar.DECEMBER, 30);
    Date createdAt = cal.getTime();
    applicant.setCreatedAt(createdAt);

    when(mongoTemplate.find(any(Query.class), eq(Applicant.class)))
        .thenReturn(Collections.singletonList(applicant));
    when(mongoTemplate.count(any(Query.class), eq(Applicant.class))).thenReturn(1L);

    PaginatedApplicantResponse response =
        applicantServiceImpl.getPaginatedApplicants(
            1, 10, null, null, null, null, null, null, toDate, "createdAt", "asc");

    assertFalse(response.getApplicants().isEmpty());
    Date retrievedCreatedAt = response.getApplicants().get(0).getCreatedAt();
    assertTrue(
        retrievedCreatedAt.before(toDate) || retrievedCreatedAt.equals(toDate),
        "Applicant createdAt should be before or equal to toDate");
  }
}
