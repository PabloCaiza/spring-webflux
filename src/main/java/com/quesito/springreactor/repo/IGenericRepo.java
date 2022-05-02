package com.quesito.springreactor.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean //this is for not create an implementation of this interface
public interface IGenericRepo<T,ID> extends ReactiveMongoRepository<T,ID> {
}
