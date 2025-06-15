package com.bot.config;

import com.bot.handler.WhatsappHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> employeeRoutes(WhatsappHandler whatsappHandler) {
        return RouterFunctions.route(
                POST("/v1/webhook"), whatsappHandler::incomingMessage).andRoute(
                GET("/v1/webhook"), whatsappHandler::healthCheck);

    }

}
