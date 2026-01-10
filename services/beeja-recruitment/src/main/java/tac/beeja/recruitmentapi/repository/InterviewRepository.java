package tac.beeja.recruitmentapi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tac.beeja.recruitmentapi.model.Interview;

@Repository
public interface InterviewRepository extends MongoRepository<Interview, String> {
  
  List<Interview> findByApplicantIdAndOrganizationId(String applicantId);
  
  List<Interview> findByOrganizationId(String organizationId);
  
  Optional<Interview> findByMeetingIdAndOrganizationId(String meetingId);
  Optional<Interview> findByIdAndOrganizationId(String id, String organizationId);
}
