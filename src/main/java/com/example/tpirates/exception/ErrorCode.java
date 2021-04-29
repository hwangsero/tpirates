package com.example.tpirates.exception;

import com.example.tpirates.exception.businessException.BusinessException;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "Invalid Input Value"),
    METHOD_NOT_ALLOWED(400," Invalid Input Value"),
    INTERNAL_SERVER_ERROR(500, "Server Error"),
    INVALID_TYPE_VALUE(400, " Invalid Type Value"),
    HANDLE_ACCESS_DENIED(403, "Access is Denied"),
    ENTITY_NOT_FOUND(400, " Entity Not Found"),

    // Store
    BUSINESS_VALIDATION_ERROR(400, "Open and Close are can't be the same");


//    // Member
//    EMAIL_DUPLICATION(400,"Email is Duplication"),
//    LOGIN_INPUT_INVALID(400,"Login input is invalid"),
//
//    // Coupon
//    COUPON_ALREADY_USE(400,"Coupon was already used"),
//    COUPON_EXPIRE(400,"Coupon was already expired");

    private final int status;
    private String code;

    ErrorCode(final int status, final String code) {
        this.status = status;
        this.code = code;
    }


}
