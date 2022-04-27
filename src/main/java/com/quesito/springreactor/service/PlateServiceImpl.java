package com.quesito.springreactor.service;

import com.quesito.springreactor.model.Plate;
import com.quesito.springreactor.repo.IPlateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlateServiceImpl implements IPlateService {
    @Autowired
    private IPlateRepo iPlateRepo;

    @Override
    public Mono<Plate> save(Plate plate) {
        return iPlateRepo.save(plate);
    }

    @Override
    public Mono<Plate> update(Plate plate) {
        return iPlateRepo.save(plate);
    }

    @Override
    public Flux<Plate> findAll() {
        return iPlateRepo.findAll();
    }

    @Override
    public Mono<Plate> findById(String id) {
        return iPlateRepo.findById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return iPlateRepo.deleteById(id);
    }
}
