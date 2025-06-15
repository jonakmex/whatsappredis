package com.bot.whatsapp.outgoing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class AcknowledgeMessenger implements Messenger {

    private final WebClient whatsappClient;

    @Override
    public Mono<Void> deliver(Map<String, Object> params) {
        return whatsappClient.post()
                .uri("/messages")
                .bodyValue(new MarkAsReadRequest(params.get("messageId").toString()))
                .retrieve()
                .bodyToMono(Void.class);
    }

    // DTO for request body
    record MarkAsReadRequest(String messaging_product, String status, String message_id) {
        public MarkAsReadRequest(String messageId) {
            this("whatsapp", "read", messageId);
        }
    }
}
