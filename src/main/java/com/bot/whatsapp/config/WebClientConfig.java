package com.bot.whatsapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${whatsapp.api.version}")
    private  String version;
    @Value("${whatsapp.api.token}")
    private  String token;
    @Value("${whatsapp.api.url}")
    private  String url;
    @Value("${whatsapp.api.businessPhoneNumberId}")
    private String businessPhoneNumberId;

    @Bean
    public WebClient whatsappClient() {
        return WebClient.builder()
                .baseUrl(url + "/" + version+ "/"+businessPhoneNumberId)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

}
