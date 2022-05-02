package com.quesito.springreactor.controller;

import com.quesito.springreactor.model.Client;
import com.quesito.springreactor.model.Plate;
import com.quesito.springreactor.service.IPlateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/plates")
public class PlateController {

    @Autowired
    private IPlateService iPlateService;

    @GetMapping
    public Mono<ResponseEntity<Flux<Plate>>> findAll() {
        Flux<Plate> plateFlux = iPlateService.findAll();
        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(plateFlux));
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<Plate>> findById(@PathVariable("id") String id) {
        return iPlateService.findById(id).map(plate -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(plate));
    }

    @PostMapping
    public Mono<ResponseEntity<Plate>> save(@Valid @RequestBody Plate plate) {

        return iPlateService.save(plate).map(p -> ResponseEntity
//                .created(URI.create(request.getURI().toString().concat("/").concat(plate.getId())))
                .created(URI.create(plate.getId()))
                .body(p));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Plate>> update(@PathVariable("id") String id, @RequestBody Plate plate) {
        Mono<Plate> plateMono = Mono.just(plate);
        Mono<Plate> monoDb = iPlateService.findById(id);
        return monoDb
                .zipWith(plateMono, (dbPlate, bodyPlate) -> {
                    dbPlate.setName(bodyPlate.getName());
                    dbPlate.setPrice(bodyPlate.getPrice());
                    dbPlate.setStatus(bodyPlate.getStatus());
                    return dbPlate;
                })
                .flatMap(iPlateService::update)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return iPlateService.findById(id)
                .flatMap(plate -> iPlateService.deleteById(plate.getId())
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Plate>> getHateoasById(@PathVariable("id") String id) {
        Mono<Link> link1 = Mono.just(
                linkTo(methodOn(PlateController.class).getHateoasById(id)).withSelfRel());
        Mono<Link> link2 = Mono.just(
                linkTo(methodOn(PlateController.class).getHateoasById(id)).withRel("plate"));
        Mono<Link> link3 = Mono.just(
                linkTo(methodOn(PlateController.class).getHateoasById(id)).withRel("order"));
//        return iPlateService
//                .findById(id)
//                .zipWith(link1,EntityModel::of);
        return link1
                .zipWith(link2)
                .map( tuple -> Links.of(tuple.getT1(), tuple.getT2()))
                .zipWith(iPlateService.findById(id),(links, plate) ->EntityModel.of(plate,links));
    }

}
