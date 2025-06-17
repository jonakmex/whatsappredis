package com.bot.whatsapp.outgoing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OptionsMessenger implements Messenger {
    private final WebClient whatsappClient;

    @Override
    public Mono<Void> deliver(Map<String, Object> params) {

        return whatsappClient.post()
                .uri("/messages")
                .bodyValue(new OptionsMessenger.OptionsRequest(
                        params.get("to").toString()
                        ,params.get("options") instanceof Map ? (Map<String, String>) params.get("options") : Map.of()))
                .retrieve()
                .bodyToMono(Void.class);
    }

    // DTO for request body
    record OptionsRequest(
            String messaging_product,
            String recipient_type,
            String to,
            String type,
            Interactive interactive
    ) {
        public OptionsRequest(String to, Map<String, String> buttons) {
            this(
                    "whatsapp",
                    "individual",
                    to,
                    "interactive",
                    new Interactive(
                            "button",
                            new Body("Elige una opcion:"), // or pass as parameter if needed
                            new Action(
                                    buttons.entrySet().stream()
                                            .map(e -> new Button(
                                                    "reply",
                                                    new Reply(e.getKey(), e.getValue())
                                            ))
                                            .toList()
                            )
                    )
            );
        }

        public record Interactive(
                String type,
                Body body,
                Action action
        ) {}

        public record Body(
                String text
        ) {}

        public record Action(
                java.util.List<Button> buttons
        ) {}

        public record Button(
                String type,
                Reply reply
        ) {}

        public record Reply(
                String id,
                String title
        ) {}
    }
}
