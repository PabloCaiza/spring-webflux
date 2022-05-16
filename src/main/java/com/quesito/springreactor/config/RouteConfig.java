package com.quesito.springreactor.config;

import com.quesito.springreactor.handler.ClientHandler;
import com.quesito.springreactor.handler.InvoiceHandler;
import com.quesito.springreactor.handler.PlateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import static  org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouteConfig {
    //Functional Endpoints
    @Bean
    public RouterFunction<ServerResponse> routesPlate(PlateHandler plateHandler) {
        return route(RequestPredicates.GET("/v2/plates"), plateHandler::findAll)
                .andRoute(RequestPredicates.GET("/v2/plates/{id}"), plateHandler::findById)
                .andRoute(RequestPredicates.POST("/v2/plates"), plateHandler::create)
                .andRoute(RequestPredicates.PUT("/v2/plates/{id}"), plateHandler::update)
                .andRoute(RequestPredicates.DELETE("/v2/plates/{id}"), plateHandler::delete);
    }
    @Bean
    public RouterFunction<ServerResponse> routesClient(ClientHandler clientHandler) {
        return route(RequestPredicates.GET("/v2/clients"), clientHandler::findAll)
                .andRoute(RequestPredicates.GET("/v2/clients/{id}"), clientHandler::findById)
                .andRoute(RequestPredicates.POST("/v2/clients"), clientHandler::create)
                .andRoute(RequestPredicates.PUT("/v2/clients/{id}"), clientHandler::update)
                .andRoute(RequestPredicates.DELETE("/v2/clients/{id}"), clientHandler::delete);
    }
    @Bean
    public RouterFunction<ServerResponse> routesInvoice(InvoiceHandler invoiceHandler) {
        return route(RequestPredicates.GET("/v2/invoices"), invoiceHandler::findAll)
                .andRoute(RequestPredicates.GET("/v2/invoices/{id}"), invoiceHandler::findById)
                .andRoute(RequestPredicates.POST("/v2/invoices"), invoiceHandler::create)
                .andRoute(RequestPredicates.PUT("/v2/invoices/{id}"), invoiceHandler::update)
                .andRoute(RequestPredicates.DELETE("/v2/invoices/{id}"), invoiceHandler::delete);
    }

}
