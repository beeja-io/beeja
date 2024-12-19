package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.OrganizationPattern;
import com.beeja.api.accounts.repository.OrganizationPatternsRepository;
import com.beeja.api.accounts.requests.OrganizationPatternRequest;
import com.beeja.api.accounts.service.OrganizationPatternService;
import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrganizationPatternServiceImpl implements OrganizationPatternService {
  @Autowired OrganizationPatternsRepository organizationPatternsRepository;

  @Override
  public OrganizationPattern updatePatternStatusByPatternIdAndPatternType(
      String patternId, String patternType) {
    List<OrganizationPattern> organizationPatterns =
        organizationPatternsRepository.findByOrganizationIdAndPatternType(
            UserContext.getLoggedInUserOrganization().getId(), patternType);
    OrganizationPattern patternToBeUpdated =
        organizationPatterns.stream()
            .filter(pattern -> pattern.getId().equals(patternId))
            .findFirst()
            .orElse(null);

    if (patternToBeUpdated == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.NO_PATTERN_FOUND_WITH_PROVIDED_ID));
    }
    OrganizationPattern updatedPattern = null;
    for (OrganizationPattern pattern : organizationPatterns) {
      if (pattern.getId().equals(patternId)) {
        pattern.setActive(true);
        updatedPattern = pattern;
      } else {
        pattern.setActive(false);
      }
    }
    organizationPatternsRepository.saveAll(organizationPatterns);
    return updatedPattern;
  }

  @Override
  public OrganizationPattern addPatternByPatternIdAndPatternType(
      OrganizationPatternRequest organizationPatternRequest) {

    String organizationId = UserContext.getLoggedInUserOrganization().getId();

    boolean patternExists =
        organizationPatternsRepository
            .existsByOrganizationIdAndPatternTypeAndPatternLengthAndPrefix(
                organizationId,
                String.valueOf(organizationPatternRequest.getPatternType()),
                organizationPatternRequest.getPatternLength(),
                organizationPatternRequest.getPrefix());

    if (patternExists) {
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.CONFLICT_ERROR,
              ErrorCode.RESOURCE_IN_USE,
              Constants.SAME_ID_PATTERN_ALREADY_REGISTERED));
    }

    if (organizationPatternRequest.isActive()) {
      List<OrganizationPattern> existingPatterns =
          organizationPatternsRepository.findByOrganizationIdAndPatternType(
              organizationId, String.valueOf(organizationPatternRequest.getPatternType()));
      for (OrganizationPattern existingPattern : existingPatterns) {
        existingPattern.setActive(false);
      }
      organizationPatternsRepository.saveAll(existingPatterns);
    }

    OrganizationPattern organizationPattern =
        createOrganizationPattern(organizationPatternRequest, organizationId);
    return organizationPatternsRepository.save(organizationPattern);
  }

  private static OrganizationPattern createOrganizationPattern(
      OrganizationPatternRequest organizationPatternRequest, String organizationId) {
    OrganizationPattern organizationPattern = new OrganizationPattern();
    organizationPattern.setOrganizationId(organizationId);
    organizationPattern.setPatternType(organizationPatternRequest.getPatternType());
    organizationPattern.setPatternLength(organizationPatternRequest.getPatternLength());
    organizationPattern.setActive(organizationPatternRequest.isActive());
    organizationPattern.setPrefix(organizationPatternRequest.getPrefix());
    organizationPattern.setInitialSequence(organizationPatternRequest.getInitialSequence());
    String prefix = organizationPatternRequest.getPrefix();
    String zeros =
        String.format(
            "%0" + (organizationPatternRequest.getPatternLength() - prefix.length()) + "d",
            organizationPattern.getInitialSequence());
    organizationPattern.setExamplePattern(prefix + zeros);
    return organizationPattern;
  }

  @Override
  public void deletePatternByPatternIdAndPatternType(String patternId, String patternType) {
    organizationPatternsRepository.deleteByOrganizationIdAndPatternTypeAndId(
        UserContext.getLoggedInUserOrganization().getId(), patternId, patternType);
  }

  @Override
  public List<OrganizationPattern> getPatternsByPatternType(String patternType) throws Exception {
    try {
      return organizationPatternsRepository.findByOrganizationIdAndPatternType(
          UserContext.getLoggedInUserOrganization().getId(), patternType);
    } catch (Exception e) {
      log.info(e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.UNABLE_TO_FETCH_DETAILS_FROM_DATABASE));
    }
  }
}
