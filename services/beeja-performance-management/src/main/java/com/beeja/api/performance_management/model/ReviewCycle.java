package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.ReviewCycleStatus;
import com.beeja.api.performance_management.enums.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "review_cycles")
public class ReviewCycle {

    @Id
    private String id;

    private String name;
    private ReviewType reviewType;
    private Date startDate;
    private Date endDate;

    private String reviewFormId;

    private List<String> managerIds;

    private ReviewCycleStatus status;

    private String organizationId;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    private String createdBy;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    private String updatedBy;

}
