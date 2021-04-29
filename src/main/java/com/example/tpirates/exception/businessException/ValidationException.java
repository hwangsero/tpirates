package com.example.tpirates.exception.businessException;

import com.example.tpirates.exception.ErrorCode;

public class ValidationException extends BusinessException{

    public ValidationException(String target) {
        super(target + " is validation violation", ErrorCode.BUSINESS_VALIDATION_ERROR);
    }
}
