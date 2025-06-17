package com.bot.whatsapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class FlowConfig {
    @Bean
    public ConcurrentHashMap<String,String> flowStates() {
        return new ConcurrentHashMap<>();
    }
}
