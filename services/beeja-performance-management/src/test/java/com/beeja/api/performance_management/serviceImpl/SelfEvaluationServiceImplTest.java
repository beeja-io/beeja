package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.model.QuestionAnswer;
import com.beeja.api.performance_management.model.SelfEvaluation;
import com.beeja.api.performance_management.repository.SelfEvaluationRepository;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.MockedStatic;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelfEvaluationServiceImplTest {

    @Mock
    private SelfEvaluationRepository selfRepo;

    @InjectMocks
    private SelfEvaluationServiceImpl service;

    private MockedStatic<UserContext> userContextMock;

    private static final String ORG_ID = "ORG1";
    private static final String EMP_ID = "EMP1";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(() -> UserContext.getLoggedInUserOrganization())
                .thenReturn(Map.of("id", ORG_ID));
    }

    @AfterEach
    void tearDown() {
        userContextMock.close();
    }

    @Test
    void testSubmitSelfEvaluation_Success() {
        QuestionAnswer qa = new QuestionAnswer();
        qa.setQuestionId("Q1");
        qa.setAnswer("Test");

        SelfEvaluation self = new SelfEvaluation();
        self.setEmployeeId(EMP_ID);
        self.setResponses(List.of(qa));

        when(selfRepo.existsByEmployeeIdAndOrganizationIdAndSubmittedTrue(EMP_ID, ORG_ID))
                .thenReturn(false);
        when(selfRepo.save(any(SelfEvaluation.class))).thenAnswer(inv -> inv.getArgument(0));

        SelfEvaluation saved = service.submitSelfEvaluation(self);

        assertEquals(EMP_ID, saved.getEmployeeId());
        assertEquals(ORG_ID, saved.getOrganizationId());
        assertTrue(saved.isSubmitted());
        assertNotNull(saved.getSubmittedAt());
        verify(selfRepo, times(1)).save(any(SelfEvaluation.class));
    }

    @Test
    void testSubmitSelfEvaluation_MissingEmployeeId_ShouldThrow() {
        SelfEvaluation self = new SelfEvaluation();
        self.setEmployeeId(" ");
        self.setResponses(List.of(new QuestionAnswer()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.submitSelfEvaluation(self)
        );
        assertTrue(ex.getMessage().contains("Employee ID is required"));
    }

    @Test
    void testSubmitSelfEvaluation_EmptyResponses_ShouldThrow() {
        SelfEvaluation self = new SelfEvaluation();
        self.setEmployeeId(EMP_ID);
        self.setResponses(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.submitSelfEvaluation(self)
        );
        assertTrue(ex.getMessage().contains("responses cannot be empty"));
    }

    @Test
    void testSubmitSelfEvaluation_AlreadySubmitted_ShouldThrow() {
        SelfEvaluation self = new SelfEvaluation();
        self.setEmployeeId(EMP_ID);
        self.setResponses(List.of(new QuestionAnswer()));

        when(selfRepo.existsByEmployeeIdAndOrganizationIdAndSubmittedTrue(EMP_ID, ORG_ID))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.submitSelfEvaluation(self)
        );
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("already submitted"));
    }

    @Test
    void testGetByEmployee_Success() {
        SelfEvaluation self = new SelfEvaluation();
        self.setEmployeeId(EMP_ID);
        self.setOrganizationId(ORG_ID);

        when(selfRepo.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(List.of(self));

        List<SelfEvaluation> result = service.getByEmployee(EMP_ID);

        assertEquals(1, result.size());
        assertEquals(EMP_ID, result.get(0).getEmployeeId());
    }

    @Test
    void testGetByEmployee_NullOrBlank_ShouldThrow() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> service.getByEmployee(null)
        );
        assertTrue(ex1.getMessage().contains("Employee ID is required"));

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> service.getByEmployee(" ")
        );
        assertTrue(ex2.getMessage().contains("Employee ID is required"));
    }

    @Test
    void testGetOrgId_MissingOrg_ShouldThrow() {
        userContextMock.when(() -> UserContext.getLoggedInUserOrganization()).thenReturn(null);

        SelfEvaluation self = new SelfEvaluation();
        self.setEmployeeId("EMP1");
        self.setResponses(List.of(new com.beeja.api.performance_management.model.QuestionAnswer()));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.submitSelfEvaluation(self)
        );
        assertTrue(ex.getMessage().contains("Organization ID not found"));
    }

}
