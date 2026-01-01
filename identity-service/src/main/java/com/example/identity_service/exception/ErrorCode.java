package com.example.identity_service.exception;

public enum ErrorCode {
    INVALID_KEY(1004, "Uncategorized error"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception"),
    USER_EXISTED(1001, "User existed"),
    USERNAME_INVALID(1002, "Username must be at least 6 characters"),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters"),
    USER_NOT_FOUND(1005, "User not found"),
    UNAUTHENTICATED(1006, "Invalid username or password"),
    UNAUTHORIZED(1007, "You do not have permission to access this resource"),
    INVALID_TOKEN(1008, "Invalid or expired token"),
    ROLE_NOT_FOUND(1009, "Role not found"),
    ROLE_ALREADY_EXISTS(1010, "Role already exists"),
    INVALID_RESET_TOKEN(1011, "Password reset token is invalid or expired"),
    INVALID_VERIFICATION_TOKEN(1012, "Email verification token is invalid or expired"),
    EMAIL_NOT_VERIFIED(1013, "Email address is not verified"),
    INVALID_OLD_PASSWORD(1014, "Old password is incorrect"),
    PASSWORD_MISMATCH(1015, "New passwords do not match"),
    TOKEN_BLACKLISTED(1016, "Token has been invalidated"),
    EMAIL_SEND_FAILED(1017, "Failed to send email"),
    EMAIL_ALREADY_VERIFIED(1018, "Email is already verified"),
    NO_RESET_TOKEN_FOUND(1019, "No password reset request found for this user"),
    EMAIL_EXISTED(1020, "Email already exists"),
    ;

    ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private int errorCode;
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
