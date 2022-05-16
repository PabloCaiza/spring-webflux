package com.quesito.springreactor.handler;

import com.quesito.springreactor.dto.ValidationDTO;
import com.quesito.springreactor.model.Plate;
import com.quesito.springreactor.service.IPlateService;
import com.quesito.springreactor.validators.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class ClientHandler {
    @Autowired
    private IPlateService iPlateService;
    @Autowired
    private Validator validator;
    @Autowired
    private RequestValidator requestValidator;

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(iPlateService.findAll(), Plate.class);

    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(iPlateService.findById(id), Plate.class)
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Plate.class)
                .flatMap(plate -> {
                    Errors errors = new BeanPropertyBindingResult(plate, Plate.class.getName());
                    validator.validate(plate, errors);
                    if (errors.hasErrors()) {
                        return Flux.fromIterable(errors.getFieldErrors())
                                .map(error -> new ValidationDTO(error.getCode(), error.getDefaultMessage()))
                                .collectList()
                                .flatMap(validationDTOS -> ServerResponse
                                        .badRequest()
                                        .body(BodyInserters.fromValue(validationDTOS), ValidationDTO.class));

                    } else {
                        return iPlateService.save(plate).flatMap(p -> ServerResponse
                                .created(URI.create(serverRequest.uri().toString().concat("/").concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(plate), Plate.class));
                    }


                });
//        return serverRequest.bodyToMono(Plate.class)
//                .flatMap(requestValidator::validate)
//                .flatMap(iPlateService::save)
//                .flatMap(p -> ServerResponse
//                        .created(URI.create(serverRequest.uri().toString().concat("/").concat(p.getId())))
//                        .body(Mono.just(p), Plate.class));

    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Plate> plateMono = serverRequest.bodyToMono(Plate.class);
        Mono<Plate> plateDb = iPlateService.findById(id);
        return plateMono.zipWith(plateDb, (plate, db) -> {
                    db.setName(plate.getName());
                    db.setPrice(plate.getPrice());
                    db.setStatus(plate.getStatus());
                    db.setPlateNumber(plate.getPlateNumber());
                    return db;
                })
                .flatMap(iPlateService::update)
                .flatMap(plate -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(plate)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return iPlateService.findById(id)
                .flatMap(plate -> iPlateService.deleteById(id).then(ServerResponse.ok().build())
                        .switchIfEmpty(ServerResponse.notFound().build()));

    }
}
