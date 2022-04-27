package com.quesito.springreactor.controller;

import com.quesito.springreactor.model.Plate;
import com.quesito.springreactor.service.IPlateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

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
    public Mono<ResponseEntity<Plate>> save(@RequestBody Plate plate, ServerHttpRequest request) {

        return iPlateService.save(plate).map(p -> ResponseEntity
                .created(URI.create(request.getURI().toString().concat("/").concat(plate.getId())))
                .body(p));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Plate>> update(@PathVariable("id") String id,@RequestBody Plate plate) {
        Mono<Plate> plateMono=Mono.just(plate);
        Mono<Plate> monoDb = iPlateService.findById(id);
        return monoDb
                .zipWith(plateMono,(dbPlate, bodyPlate) -> {
                    dbPlate.setName(bodyPlate.getName());
                    dbPlate.setPrice(bodyPlate.getPrice());
                    dbPlate.setStatus(bodyPlate.getStatus());
                    return dbPlate;
                })
                .flatMap(iPlateService::update)
                .map(p->ResponseEntity.ok()
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

}
