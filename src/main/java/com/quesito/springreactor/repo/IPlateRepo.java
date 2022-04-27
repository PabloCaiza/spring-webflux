package com.quesito.springreactor.repo;

import com.quesito.springreactor.model.Plate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IPlateRepo extends ReactiveMongoRepository<Plate,String> {
}
