package tac.beeja.recruitmentapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tac.beeja.recruitmentapi.model.Applicant;

import java.util.List;

public interface ApplicantRepository extends MongoRepository<Applicant, String> {
  List<Applicant> findAllByOrganizationId(String organizationId);

  Applicant findByIdAndOrganizationId(String id, String organizationId);

  List<Applicant> findByReferredByEmployeeIdAndOrganizationId(
      String referredByEmployeeId, String organizationId);
}
