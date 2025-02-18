package com.beeja.api.projectmanagement.model.LogHours;

import com.beeja.api.projectmanagement.utils.UserContext;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Timesheets")
public class Timesheet {

    @Id
    private String id;
    @NotBlank
    private String employeeId;
    private String organizationId= UserContext.getLoggedInUserOrganization().get("id").toString();
    private List<LogHours> logHours = new ArrayList<>();
}

