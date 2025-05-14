package com.beeja.api.financemanagementservice.modals.clients.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationPattern {
    private String id;
    private String patternType;
    private String organizationId;
    private int patternLength;
    private String prefix;
    private int initialSequence;
    private String examplePattern;
    private boolean active;
}