package com.quesito.springreactor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String urlPhoto;
}
