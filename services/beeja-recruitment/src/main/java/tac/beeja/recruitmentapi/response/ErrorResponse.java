package tac.beeja.recruitmentapi.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tac.beeja.recruitmentapi.enums.ErrorCode;
import tac.beeja.recruitmentapi.enums.ErrorType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ErrorType type;
    private ErrorCode code;
    private String message;
    private String docUrl;
    private String path;
    private String referenceId;
    private String timestamp;
}
