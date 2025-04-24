package tac.beeja.recruitmentapi.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedApplicantResponse {
    private List<ApplicantDTO> applicants;
    private int currentPage;
    private int pageSize;
    private long totalRecords;
    private int totalPages;
}

