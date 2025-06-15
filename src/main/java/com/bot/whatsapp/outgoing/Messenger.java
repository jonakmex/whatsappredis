package com.bot.whatsapp.outgoing;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface Messenger {
    Mono<Void> deliver(Map<String,Object> params);
}
