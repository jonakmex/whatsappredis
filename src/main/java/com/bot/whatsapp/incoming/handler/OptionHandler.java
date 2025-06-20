package com.bot.whatsapp.incoming.handler;

import com.bot.whatsapp.flow.Flow;
import com.bot.whatsapp.outgoing.Messenger;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Service
public class OptionHandler implements ChangeHandler {

    private final Messenger acknowledgeMessenger;
    private final Map<String, Flow> flows = new ConcurrentHashMap<>();

    public OptionHandler(List<Flow> flowList,Messenger acknowledgeMessenger) {
        flowList.forEach(h -> flows.put(h.type(), h));
        this.acknowledgeMessenger = acknowledgeMessenger;
    }

    @Override
    public String type() {
        return "interactive";          // exactly the field Meta sends
    }

    @Override
    public Mono<Void> handle(JsonNode value) {
        JsonNode msgNode = value.at("/messages/0");
        String messageId = msgNode.path("id").asText(null);

        String optionId = msgNode.path("interactive").path("button_reply").path("id").asText("").trim();
        String from = getPhoneNumber(msgNode);

        flows.get(optionId).execute(from,null);

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
