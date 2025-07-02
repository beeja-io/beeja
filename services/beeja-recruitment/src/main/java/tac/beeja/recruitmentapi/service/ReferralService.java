package tac.beeja.recruitmentapi.service;

import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.request.ApplicantRequest;

public interface ReferralService {
  Applicant newReferral(ApplicantRequest applicantRequest) throws Exception;

  List<Applicant> getMyReferrals() throws Exception;

  ByteArrayResource downloadFile(String fileId) throws Exception;
}
