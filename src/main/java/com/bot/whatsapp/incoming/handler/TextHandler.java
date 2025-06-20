package com.bot.whatsapp.incoming.handler;

import com.bot.whatsapp.flow.Flow;
import com.bot.whatsapp.outgoing.Messenger;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TextHandler implements ChangeHandler {

    private final Messenger acknowledgeMessenger;
    private final Messenger textMessenger;
    private final Messenger optionsMessenger;
    private final ConcurrentHashMap<String,String> flowStates;
    private final Map<String, Flow> flows = new ConcurrentHashMap<>();

    public TextHandler(Messenger acknowledgeMessenger,
                       Messenger textMessenger,
                       Messenger optionsMessenger,
                       ConcurrentHashMap<String, String> flowStates,
                       List<Flow> flowList) {
        this.acknowledgeMessenger = acknowledgeMessenger;
        this.textMessenger = textMessenger;
        this.optionsMessenger = optionsMessenger;
        this.flowStates = flowStates;
        flowList.forEach(h -> flows.put(h.type(), h));
    }

    @Override
    public String type() {
        return "text";          // exactly the field Meta sends
    }

    @Override
    public Mono<Void> handle(JsonNode value) {
        JsonNode msgNode = value.at("/messages/0");
        String messageId = msgNode.path("id").asText(null);
        String body = msgNode.path("text").path("body").asText("").trim();
        String from = getPhoneNumber(msgNode);

        String flow = flowStates.get(from);
        if (flow != null) {
            flows.get(flow.split(":")[0]).execute(from, body);
        } else if("menu".equals(body)) {
            Map<String, String> options = new LinkedHashMap<>();
            options.put("invite", "Invitar");
            options.put("add_balance", "Abonar Saldo");
            options.put("apply_redemption", "Aplicar canje");
            optionsMessenger.deliver(Map.of(
                    "to", from,
                    "options", options
            )).subscribe();
        }
        else {
            textMessenger.deliver(Map.of(
                    "to", from,
                    "body", "Hello, " + from + "! You said: " + body
            )).subscribe();
        }

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
