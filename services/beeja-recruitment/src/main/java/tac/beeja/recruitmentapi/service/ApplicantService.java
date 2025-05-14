package tac.beeja.recruitmentapi.service;

import org.springframework.core.io.ByteArrayResource;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;
import tac.beeja.recruitmentapi.request.AddCommentRequest;
import tac.beeja.recruitmentapi.request.ApplicantFeedbackRequest;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.response.PaginatedApplicantResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ApplicantService {
  Applicant postApplicant(ApplicantRequest applicant, boolean isReferral) throws Exception;

  List<Applicant> getAllApplicantsInOrganization() throws Exception;

  Applicant updateApplicant(String applicantId, Map<String, Object> fields) throws Exception;

  ByteArrayResource downloadFile(String fileId) throws Exception;

  Applicant submitFeedback(String applicantId, ApplicantFeedbackRequest applicantFeedbackRequest);

  Applicant assignInterviewer(String applicantId, AssignedInterviewer assignedInterviewer)
      throws Exception;

  Applicant getApplicantById(String applicantId) throws Exception;

  Applicant deleteInterviewerByInterviewID(String applicantId, String interviewId) throws Exception;

  Applicant addCommentToApplicant(AddCommentRequest addCommentRequest) throws Exception;

  Applicant changeStatusOfApplicant(String applicantId, String status) throws Exception;

  PaginatedApplicantResponse getPaginatedApplicants(Integer page, Integer limit, String applicantId, String firstName, String positionAppliedFor, ApplicantStatus status, String experience, Date fromDate,Date toDate, String sortBy, String sortDirection);

}
