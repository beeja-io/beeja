package com.beeja.api.performance_management.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "questions")
public class Questions {

    @Id
    private String id;

    private String question;
    private String questionType;
    private boolean isRequired;
}
