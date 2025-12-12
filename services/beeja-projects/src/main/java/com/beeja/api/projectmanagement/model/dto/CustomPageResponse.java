package com.beeja.api.projectmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Custom page response used by controller responses.
 *
 * @param <T> the content type
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
