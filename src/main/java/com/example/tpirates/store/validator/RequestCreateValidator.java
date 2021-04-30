package com.example.tpirates.store.validator;

import com.example.tpirates.exception.businessException.ValidationException;
import com.example.tpirates.store.model.RequestCreateStore;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RequestCreateValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RequestCreateStore.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RequestCreateStore request = (RequestCreateStore) target;

        request.getBusinessTimes().forEach(businessTime -> {
            if(businessTime.getOpen() == businessTime.getClose()) {
                throw new ValidationException("businessTime");
            }
        }
        );
    }
}
