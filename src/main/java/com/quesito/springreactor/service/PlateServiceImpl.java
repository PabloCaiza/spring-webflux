package com.quesito.springreactor.service;

import com.quesito.springreactor.model.Plate;
import com.quesito.springreactor.repo.IGenericRepo;
import com.quesito.springreactor.repo.IPlateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlateServiceImpl extends CrudImpl<Plate,String> implements IPlateService {
    @Autowired
    private IPlateRepo repo;
    @Override
    IGenericRepo<Plate, String> getRepo() {
        return repo;
    }
}
