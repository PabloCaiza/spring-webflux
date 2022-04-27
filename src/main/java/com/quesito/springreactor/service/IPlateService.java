package com.quesito.springreactor.service;

import com.quesito.springreactor.model.Plate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlateService {

   Mono<Plate> save(Plate plate);
   Mono<Plate> update(Plate plate);
   Flux<Plate> findAll();
   Mono<Plate> findById(String id);
   Mono<Void> deleteById(String id);
}
