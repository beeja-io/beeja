package com.beeja.api.expense.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.beeja.api.expense.enums.ErrorType;
import com.beeja.api.expense.enums.ErrorCode;

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
