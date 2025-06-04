package com.beeja.api.performance_management.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "review_form")
public class ReviewForm {

    @Id
    private String id;

    private String reviewCycleId;
    private String description;

    private Email providerEmail;
    private String receiverName;

    @DBRef
    List<Questions> questionsList;

    @DBRef
    List<Answers> answersList;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;
}
