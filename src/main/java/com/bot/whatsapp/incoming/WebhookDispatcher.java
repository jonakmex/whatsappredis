package com.bot.whatsapp.incoming;

import com.bot.whatsapp.incoming.handler.ChangeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


@Component
@Slf4j
public class WebhookDispatcher {
    private final Map<String, Function<JsonNode, Mono<Void>>> handlers = new ConcurrentHashMap<>();

    public WebhookDispatcher(List<ChangeHandler> handlerList) {
        handlerList.forEach(h -> handlers.put(h.type(), h::handle));
    }

    public Mono<Void> dispatch(JsonNode root) {
        return Flux.fromIterable(root.at("/entry"))
                .flatMap(entry -> Flux.fromIterable(entry.get("changes")))
                .filter(change -> change.get("value").has("messages"))
                .flatMap(change -> {
                    JsonNode messages = change.get("value").get("messages");
                    return Flux.fromIterable(messages)
                            .flatMap(message -> {
                                String type = message.get("type").asText();
                                return handlers.getOrDefault(type, this::unknown).apply(change.get("value"));
                            });
                })
                .then();
    }

    private Mono<Void> unknown(JsonNode ignored) {
        // loggear y seguir sin fallar
        return Mono.empty();
    }
}
