package com.beeja.api.projectmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class orgAddress {
    private String addressOne;
    private String addressTwo;
    private String city;
    private String state;
    private Integer pinCode;
    private String country;
}
