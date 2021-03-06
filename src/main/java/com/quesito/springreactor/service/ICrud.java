package com.quesito.springreactor.service;

import com.quesito.springreactor.model.Client;
import com.quesito.springreactor.pagination.PageSupport;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface ICrud<T,ID> {
    Mono<T> save(T t);
    Mono<T> update(T t);
    Flux<T> findAll();
    Mono<T> findById(ID id);
    Mono<Void> deleteById(ID id);
    Mono<PageSupport<T>> getPage(Pageable page);
}
