package com.bot.whatsapp.incoming.handler;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface ChangeHandler {
    String type(); // e.g. "messages", "statuses"
    Mono<Void> handle(JsonNode value);
}
