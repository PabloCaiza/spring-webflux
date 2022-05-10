package com.quesito.springreactor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class RequestValidator {
    @Autowired
    private Validator validator;
    public <T> Mono<T> validate(T request) {
        Set<ConstraintViolation<T>> validate = validator.validate(request);
        if(validate.isEmpty()){
            return Mono.just(request);
        }
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
}
