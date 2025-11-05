package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.FinalRating;
import java.util.List;

/**
 * Service interface for computing, retrieving, and publishing employee ratings.
 */
public interface RatingService {

    /** Computes the final rating for an employee in a given cycle. */
    FinalRating computeRating(String employeeId, String cycleId, String computedBy);

    /** Retrieves all ratings for an employee in a given cycle. */
    List<FinalRating> getRatings(String employeeId, String cycleId);

    /** Publishes a specific final rating by its ID. */
    FinalRating publishRating(String id);
}
