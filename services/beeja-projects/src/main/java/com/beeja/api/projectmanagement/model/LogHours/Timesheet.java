package com.beeja.api.projectmanagement.model.LogHours;

import com.beeja.api.projectmanagement.utils.UserContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Timesheets")
public class Timesheet {

    @Id
    private String id;
    @NotBlank(message = "Employee ID cannot be blank.")
    @Field
    private String employeeId;
    @Field
    private String organizationId= UserContext.getLoggedInUserOrganization().get("id").toString();

    @Valid
    private List<LogHours> logHours = new ArrayList<>();
}

