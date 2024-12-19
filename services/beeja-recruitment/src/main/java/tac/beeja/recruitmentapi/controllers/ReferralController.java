package tac.beeja.recruitmentapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.service.ReferralService;

import java.util.List;

@RestController
@RequestMapping("/v1/referrals")
public class ReferralController {
  @Autowired ReferralService referralService;

  @PostMapping
  public Applicant newReferral(ApplicantRequest applicantRequest) throws Exception {
    return referralService.newReferral(applicantRequest);
  }

  @GetMapping
  public List<Applicant> getAllMyReferrals() throws Exception {
    return referralService.getMyReferrals();
  }

  @GetMapping("/{resumeId}")
  public ByteArrayResource downloadResume(@PathVariable String resumeId) throws Exception {
    return referralService.downloadFile(resumeId);
  }
}
