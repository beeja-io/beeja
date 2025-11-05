package tac.beeja.recruitmentapi.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tac.beeja.recruitmentapi.exceptions.BadRequestException;
import tac.beeja.recruitmentapi.request.ScheduleInterviewRequest;
import tac.beeja.recruitmentapi.response.InterviewScheduleResponse;
import tac.beeja.recruitmentapi.service.InterviewScheduleService;

@RestController
@RequestMapping("/v1/interviews")
@Slf4j
public class InterviewScheduleController {

  @Autowired private InterviewScheduleService interviewScheduleService;

  @PostMapping("/schedule/event")
  public ResponseEntity<InterviewScheduleResponse> scheduleInterview(
      @Valid @RequestBody ScheduleInterviewRequest request, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      String errorMessage = bindingResult.getFieldErrors().stream()
          .map(error -> error.getField() + ": " + error.getDefaultMessage())
          .findFirst()
          .orElse("Validation failed");
      throw new BadRequestException(errorMessage);
    }

    InterviewScheduleResponse response = interviewScheduleService.scheduleInterview(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{interviewId}")
  public ResponseEntity<InterviewScheduleResponse> getInterviewById(
      @PathVariable String interviewId) throws Exception {
    InterviewScheduleResponse response = interviewScheduleService.getInterviewById(interviewId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{interviewId}")
  public ResponseEntity<String> cancelInterview(@PathVariable String interviewId)
      throws Exception {
    interviewScheduleService.cancelInterview(interviewId);
    return ResponseEntity.ok("Interview cancelled successfully");
  }
}
