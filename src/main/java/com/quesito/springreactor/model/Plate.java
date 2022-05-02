package com.quesito.springreactor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "plates")
public class Plate {
    @Id
    private String id;

    private String plateNumber;
    @Size(min = 1, max = 100)
    private String name;
    private Double price;
    private Boolean status;
}
