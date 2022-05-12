package com.quesito.springreactor.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "invoices")
@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class Invoice {
    private String id;
    private String description;
    private Client client;
    private List<InvoiceDetail> items;

}
