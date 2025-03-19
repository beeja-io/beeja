package com.beeja.api.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "resources")
public class Resource {

    private String id;
    private String employeeId;
    private String firstName;
    private double allocation;

}




