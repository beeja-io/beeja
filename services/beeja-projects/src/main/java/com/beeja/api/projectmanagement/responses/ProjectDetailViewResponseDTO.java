package com.beeja.api.projectmanagement.responses;

import com.beeja.api.projectmanagement.enums.ContractBillingCurrency;
import com.beeja.api.projectmanagement.enums.Industry;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectDetailViewResponseDTO {
        private String projectId;
        private String name;
        private String description;
        private ProjectStatus status;
        private String clientId;
        private String clientName;
        private String clientContact;
        private String clientEmail;
        private Industry ClientIndustries;
        private String clientLogId;
        private List<String> projectManagerIds;
        private List<String> projectManagerNames;
        private List<String> projectResourceIds;
        private List<String> projectResourceNames;
        private Date startDate;
        private Date endDate;
        private ContractBillingCurrency billingCurrency;

        private List<ProjectManagerView> projectManagers;
        private List<ContractView> contracts;
        private List<ResourceView> resources;
}
