package com.bot.whatsapp.incoming.handler;

import com.bot.whatsapp.outgoing.Messenger;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandler implements ChangeHandler {

    private final Messenger acknowledgeMessenger;
    private final Messenger textMessenger;

    @Override
    public String type() {
        return "text";          // exactly the field Meta sends
    }

    @Override
    public Mono<Void> handle(JsonNode value) {
        JsonNode msgNode = value.at("/messages/0");
        JsonNode contactsNode = value.at("/contacts/0");
        String messageId = msgNode.path("id").asText(null);

        String body = msgNode.path("text").path("body").asText("").trim();
        String from = getPhoneNumber(msgNode);
        String phoneNumberId = value.path("metadata").path("phone_number_id").asText(null);

        textMessenger.deliver(Map.of(
                "to", from,
                "body", "Hello, " + from + "! You said: " + body
        )).subscribe();

        return acknowledgeMessenger.deliver(Map.of(
                "messageId",messageId) ); // nothing else to do
    }

    private String getPhoneNumber(JsonNode messageNode) {
        String from = messageNode.path("from").asText();
        if (from != null && from.length() == 13 && from.startsWith("521")) {
            return "52" + from.substring(3);
        }
        return from;
    }
}
