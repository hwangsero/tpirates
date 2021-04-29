package com.example.tpirates.exception.businessException;

import com.example.tpirates.exception.ErrorCode;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String target) {
        super(target + " is not found", ErrorCode.ENTITY_NOT_FOUND);
    }
}
