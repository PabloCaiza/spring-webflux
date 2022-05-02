package com.quesito.springreactor.service;

import com.quesito.springreactor.model.Client;
import com.quesito.springreactor.model.Plate;
import com.quesito.springreactor.repo.IClientRepo;
import com.quesito.springreactor.repo.IGenericRepo;
import com.quesito.springreactor.repo.IPlateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl extends CrudImpl<Client,String> implements IClientService {
    @Autowired
    private IClientRepo repo;
    @Override
    IGenericRepo<Client, String> getRepo() {
        return repo;
    }
}
