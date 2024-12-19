package tac.beeja.recruitmentapi.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tac.beeja.recruitmentapi.annotations.HasPermission;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;
import tac.beeja.recruitmentapi.request.AddCommentRequest;
import tac.beeja.recruitmentapi.request.ApplicantFeedbackRequest;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.service.ApplicantService;
import tac.beeja.recruitmentapi.utils.Constants;
import tac.beeja.recruitmentapi.utils.ValidationUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/applicants")
public class ApplicantController {

  @Autowired ApplicantService applicantService;

  @PostMapping
  @HasPermission(Constants.CREATE_APPLICANT)
  public ResponseEntity<Applicant> postApplicant(ApplicantRequest applicantRequest)
      throws Exception {
    return ResponseEntity.ok(applicantService.postApplicant(applicantRequest, false));
  }

  @GetMapping
  @HasPermission(Constants.GET_APPLICANTS)
  public ResponseEntity<List<Applicant>> getAllApplicants() throws Exception {
    return ResponseEntity.ok(applicantService.getAllApplicantsInOrganization());
  }

  @PatchMapping("/{applicantId}")
  @HasPermission(Constants.UPDATE_ENTIRE_APPLICANT)
  public ResponseEntity<Applicant> updateApplicant(
      @PathVariable String applicantId, @RequestBody Map<String, Object> fields) throws Exception {
    return ResponseEntity.ok(applicantService.updateApplicant(applicantId, fields));
  }

  @GetMapping("/resume/{fileId}")
  @HasPermission(Constants.READ_APPLICANT_RESUME)
  public ResponseEntity<?> downloadFile(@PathVariable String fileId) throws Exception {
    ByteArrayResource resource = applicantService.downloadFile(fileId);
    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .headers(headers)
        .body(resource);
  }

  @PatchMapping("/feedback/{applicantID}")
  @HasPermission(Constants.UPDATE_APPLICANT)
  public ResponseEntity<Applicant> submitFeedBack(
      @PathVariable String applicantID,
      @RequestBody ApplicantFeedbackRequest applicantFeedbackRequest)
      throws Exception {
    return ResponseEntity.ok(
        applicantService.submitFeedback(applicantID, applicantFeedbackRequest));
  }

  @PatchMapping("/{applicantID}/assign-interviewer")
  @HasPermission(Constants.UPDATE_ENTIRE_APPLICANT)
  public ResponseEntity<?> assignInterviewer(
      @PathVariable String applicantID,
      @Valid @RequestBody AssignedInterviewer assignedInterviewer,
      BindingResult bindingResult)
      throws Exception {

    if (bindingResult.hasErrors()) {
      String errorMessages =
          bindingResult.getFieldErrors().stream()
              .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
              .collect(Collectors.joining(", "));
      return ResponseEntity.badRequest().body("Validation errors: " + errorMessages);
    }

    Applicant applicant = applicantService.assignInterviewer(applicantID, assignedInterviewer);
    return ResponseEntity.ok(applicant);
  }

  @GetMapping("/{applicantID}")
  @HasPermission(Constants.GET_APPLICANTS)
  public ResponseEntity<Applicant> getApplicantById(@PathVariable String applicantID)
      throws Exception {
    return ResponseEntity.ok(applicantService.getApplicantById(applicantID));
  }

  @DeleteMapping("/{applicantID}/interview/{interviewID}")
  @HasPermission(Constants.DELETE_INTERVIEW)
  public ResponseEntity<Applicant> deleteInterviewerByInterviewID(
      @PathVariable String applicantID, @PathVariable String interviewID) throws Exception {
    return ResponseEntity.ok(
        applicantService.deleteInterviewerByInterviewID(applicantID, interviewID));
  }

  @PostMapping("/comments")
  public ResponseEntity<?> addCommentToApplicant(
      @Valid @RequestBody AddCommentRequest addCommentRequest, BindingResult bindingResult)
      throws Exception {

    Map<String, Object> errors = ValidationUtil.handleValidation(bindingResult);
    if (errors != null) {
      return ResponseEntity.badRequest().body(errors);
    }

    return ResponseEntity.ok(applicantService.addCommentToApplicant(addCommentRequest));
  }
}
