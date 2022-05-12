package com.quesito.springreactor.service;

import com.quesito.springreactor.model.Invoice;
import reactor.core.publisher.Mono;


public interface IInvoiceService extends ICrud<Invoice,String> {

    Mono<byte[]> generateReport(String idInvoice);
}
