package com.quesito.springreactor.repo;

import com.quesito.springreactor.model.Client;
import com.quesito.springreactor.model.Plate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IClientRepo extends IGenericRepo<Client,String> {
}
