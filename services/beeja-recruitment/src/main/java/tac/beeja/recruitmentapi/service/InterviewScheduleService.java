package tac.beeja.recruitmentapi.service;

import tac.beeja.recruitmentapi.request.ScheduleInterviewRequest;
import tac.beeja.recruitmentapi.response.InterviewScheduleResponse;

public interface InterviewScheduleService {
  
  InterviewScheduleResponse scheduleInterview(ScheduleInterviewRequest request) throws Exception;
  
  InterviewScheduleResponse getInterviewById(String interviewId) throws Exception;
  
  void cancelInterview(String interviewId) throws Exception;
}
