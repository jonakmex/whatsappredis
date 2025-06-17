package com.bot.whatsapp.outgoing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
@AllArgsConstructor
@Service
@Slf4j
public class TextMessenger implements Messenger {
    private final WebClient whatsappClient;

    @Override
    public Mono<Void> deliver(Map<String, Object> params) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(new TextRequest(
                    params.get("to").toString(),
                    params.get("body").toString()
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return whatsappClient.post()
                .uri("/messages")
                .bodyValue(new TextMessenger.TextRequest(
                        params.get("to").toString()
                        ,params.get("body").toString()))
                .retrieve()
                .bodyToMono(Void.class);
    }

    // DTO for request body
    record TextRequest(
            String messaging_product,
            String recipient_type,
            String to,
            String type,
            Text text
    ) {
        public TextRequest(String to, String body) {
            this("whatsapp", "individual", to, "text", new Text(body));
        }

        public record Text(String body) {}
    }
}
