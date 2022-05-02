package com.quesito.springreactor.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class ResourceWebPropertiesConfig {
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }


}
