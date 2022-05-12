package com.quesito.springreactor.service;

import com.quesito.springreactor.model.Invoice;
import com.quesito.springreactor.pagination.PageSupport;
import com.quesito.springreactor.repo.IClientRepo;
import com.quesito.springreactor.repo.IGenericRepo;
import com.quesito.springreactor.repo.IInvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InvoiceServiceImpl extends CrudImpl<Invoice,String> implements IInvoiceService {

    @Autowired
    private IInvoiceRepo invoiceRepo;
    @Autowired
    IClientRepo clientRepo;
    @Override
    IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return null;
    }
}
