package com.bot.handler;

import com.bot.whatsapp.SignatureVerifier;
import com.bot.whatsapp.incoming.WebhookDispatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class WhatsappHandler {
    private final WebhookDispatcher dispatcher;
    private final SignatureVerifier signatureVerifier;
    private final ObjectMapper mapper;

    public Mono<ServerResponse> incomingMessage(ServerRequest request) {
        log.info("Incoming message from WhatsApp");
        String signature = request.headers()
                .firstHeader("X-Hub-Signature-256");

        return request.bodyToMono(String.class)
                // 1) verifica firma – si falla, 403
                .filter(json -> signatureVerifier.ok(signature, json))
                .switchIfEmpty(ServerResponse.status(HttpStatus.FORBIDDEN).build().then(Mono.empty()))
                // 2) despacha a los change-handlers
                .flatMap(json ->
                        Mono.fromCallable(() -> mapper.readTree(json))
                                .flatMap(dispatcher::dispatch)
                )
                // 3) responde 200 OK (WhatsApp exige ACK rápido)
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> healthCheck(ServerRequest request){
        String mode = request.queryParam("hub.mode").orElse("");
        String token = request.queryParam("hub.verify_token").orElse("");
        String challenge = request.queryParam("hub.challenge").orElse("");
        if ("subscribe".equals(mode) && "cashback".equals(token)) {
            return ServerResponse.ok().bodyValue(challenge);
        } else {
            return ServerResponse.status(403).bodyValue("Forbidden");
        }
    }
}
