package com.quesito.springreactor.handler;

import com.quesito.springreactor.model.Invoice;
import com.quesito.springreactor.service.IInvoiceService;
import com.quesito.springreactor.validators.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class InvoiceHandler {
    @Autowired
    private IInvoiceService iInvoiceService;
    @Autowired
    private Validator validator;
    @Autowired
    private RequestValidator requestValidator;

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(iInvoiceService.findAll(), Invoice.class);

    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(iInvoiceService.findById(id), Invoice.class)
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
//        return serverRequest.bodyToMono(Invoice.class)
//                .flatMap(invoice -> {
//                    Errors errors = new BeanPropertyBindingResult(invoice, Invoice.class.getName());
//                    validator.validate(invoice, errors);
//                    if(errors.hasErrors()) {
//                        return Flux.fromIterable(errors.getAllErrors())
//                                .map(error -> new ValidationDTO(error.getCode(), error.getDefaultMessage()))
//                                .collectList()
//                                .flatMap(validationDTOS -> ServerResponse.badRequest().body(Mono.just(validationDTOS), ValidationDTO.class));
//
//                    }else {
//                        return iInvoiceService.save(invoice).flatMap(p -> ServerResponse
//                                .created(URI.create(serverRequest.uri().toString().concat("/").concat(p.getId())))
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .body(Mono.just(invoice), Invoice.class));
//                    }
//
//
//                });
        return serverRequest.bodyToMono(Invoice.class)
                .flatMap(requestValidator::validate)
                .flatMap(iInvoiceService::save)
                .flatMap(p -> ServerResponse
                        .created(URI.create(serverRequest.uri().toString().concat("/").concat(p.getId())))
                        .body(BodyInserters.fromValue(p)));

    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Invoice> invoiceMono = serverRequest.bodyToMono(Invoice.class);
        Mono<Invoice> invoiceDb = iInvoiceService.findById(id);
        return invoiceMono.zipWith(invoiceDb, (invoice, db) -> {
                    db.setId(invoice.getId());
                    db.setDescription(invoice.getDescription());
                    db.setClient(invoice.getClient());
                    db.setItems(invoice.getItems());

                    return db;
                })
                .flatMap(iInvoiceService::update)
                .flatMap(invoice -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(invoice), Invoice.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return iInvoiceService.findById(id)
                .flatMap(invoice -> iInvoiceService.deleteById(id).then(ServerResponse.ok().build())
                        .switchIfEmpty(ServerResponse.notFound().build()));

    }
}
