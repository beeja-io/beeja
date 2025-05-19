package com.beeja.api.projectmanagement.model;

import com.beeja.api.projectmanagement.enums.TaxCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaxDetails {
    @NotNull(message = "Tax Category cannot be null")
    private TaxCategory taxCategory;

    @NotNull(message = "Tax Number cannot be null")
    private String taxNumber;
}
