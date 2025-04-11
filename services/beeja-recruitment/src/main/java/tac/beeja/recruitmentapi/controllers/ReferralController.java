package tac.beeja.recruitmentapi.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tac.beeja.recruitmentapi.annotations.HasPermission;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.service.ReferralService;
import tac.beeja.recruitmentapi.utils.Constants;

@RestController
@RequestMapping("/v1/referrals")
public class ReferralController {
  @Autowired ReferralService referralService;

  @PostMapping
  @HasPermission(Constants.ACCESS_REFFERRAL)
  public Applicant newReferral(ApplicantRequest applicantRequest) throws Exception {
    return referralService.newReferral(applicantRequest);
  }

  @GetMapping
  @HasPermission(Constants.ACCESS_REFFERRAL)
  public List<Applicant> getAllMyReferrals() throws Exception {
    return referralService.getMyReferrals();
  }

  @GetMapping("/{resumeId}")
  @HasPermission(Constants.ACCESS_REFFERRAL)
  public ByteArrayResource downloadResume(@PathVariable String resumeId) throws Exception {
    return referralService.downloadFile(resumeId);
  }
}
