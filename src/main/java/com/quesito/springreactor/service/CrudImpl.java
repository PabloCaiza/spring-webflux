package com.quesito.springreactor.service;

import com.quesito.springreactor.repo.IGenericRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class CrudImpl<T,ID> implements ICrud<T,ID>{

    abstract IGenericRepo<T,ID> getRepo();
    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(T t) {
        return getRepo().save(t);
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Void> deleteById(ID id) {
        return getRepo().deleteById(id);
    }
}
