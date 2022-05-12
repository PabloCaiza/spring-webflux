package com.quesito.springreactor.controller;

import com.quesito.springreactor.model.Invoice;
import com.quesito.springreactor.pagination.PageSupport;
import com.quesito.springreactor.service.IInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private IInvoiceService iInvoiceService;

    @GetMapping
    public Mono<ResponseEntity<Flux<Invoice>>> findAll() {
        Flux<Invoice> invoiceFlux = iInvoiceService.findAll();
        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invoiceFlux));
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> findById(@PathVariable("id") String id) {
        return iInvoiceService.findById(id).map(invoice -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invoice));
    }

    @PostMapping
    public Mono<ResponseEntity<Invoice>> save(@Valid @RequestBody Invoice invoice) {

        return iInvoiceService.save(invoice).map(p -> ResponseEntity
//                .created(URI.create(request.getURI().toString().concat("/").concat(invoice.getId())))
                .created(URI.create(invoice.getId()))
                .body(p));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> update(@PathVariable("id") String id, @RequestBody Invoice invoice) {
        Mono<Invoice> invoiceMono = Mono.just(invoice);
        Mono<Invoice> monoDb = iInvoiceService.findById(id);
        return monoDb
                .zipWith(invoiceMono, (dbInvoice, bodyInvoice) -> {
                    dbInvoice.setId(bodyInvoice.getId());
                    dbInvoice.setDescription(bodyInvoice.getDescription());
                    dbInvoice.setClient(bodyInvoice.getClient());
                    dbInvoice.setItems(bodyInvoice.getItems());
                    return dbInvoice;
                })
                .flatMap(iInvoiceService::update)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return iInvoiceService.findById(id)
                .flatMap(invoice -> iInvoiceService.deleteById(invoice.getId())
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Invoice>> getHateoasById(@PathVariable("id") String id) {
        Mono<Link> link1 = Mono.just(
                linkTo(methodOn(InvoiceController.class).getHateoasById(id)).withSelfRel());
        Mono<Link> link2 = Mono.just(
                linkTo(methodOn(InvoiceController.class).getHateoasById(id)).withRel("invoice"));
        Mono<Link> link3 = Mono.just(
                linkTo(methodOn(InvoiceController.class).getHateoasById(id)).withRel("order"));
//        return iInvoiceService
//                .findById(id)
//                .zipWith(link1,EntityModel::of);
        return link1
                .zipWith(link2)
                .map( tuple -> Links.of(tuple.getT1(), tuple.getT2()))
                .zipWith(iInvoiceService.findById(id),(links, invoice) ->EntityModel.of(invoice,links));
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Invoice>>> findAllPageable(
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue ="5") int size) {
        return iInvoiceService.getPage(PageRequest.of(page,size))
                .map(p -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.noContent().build());

    }
}
