package com.beeja.api.filemanagement.repository;

import com.beeja.api.filemanagement.model.File;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
  List<File> findByEntityId(String entityId);

  File findByOrganizationIdAndId(String organizationId, String id);

  List<File> findByEntityIdAndOrganizationId(String entityId, String organizationId);

  File findByEntityIdAndFileTypeAndOrganizationId(
      String entityId, String fileType, String organizationId);
}
