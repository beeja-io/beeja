package tac.beeja.recruitmentapi.utils;

import tac.beeja.recruitmentapi.enums.ErrorCode;
import tac.beeja.recruitmentapi.enums.ErrorType;

public class BuildErrorMessage {
    public static String buildErrorMessage(ErrorType errorType, ErrorCode errorCode, String message) {
        return String.format("%s,%s,%s", errorType, errorCode, message);
    }
}
