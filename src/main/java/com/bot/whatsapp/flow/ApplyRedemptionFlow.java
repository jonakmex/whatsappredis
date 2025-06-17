package com.bot.whatsapp.flow;

import com.bot.whatsapp.outgoing.Messenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class ApplyRedemptionFlow implements Flow {
    private final Messenger textMessenger;
    private final ConcurrentHashMap<String,String> flowStates;
    final int STEP_CODE = 1;

    @Override
    public String type() {
        return "apply_redemption"; // This should match the button ID in the WhatsApp interactive message
    }

    @Override
    public void execute(String phoneNumber, String body) {
        String flow = flowStates.get(phoneNumber);
        if (flow == null) {
            textMessenger.deliver(Map.of(
                    "to", phoneNumber,
                    "body", "Code:"
            )).subscribe();
            flowStates.put(phoneNumber, type()+":"); // Initialize the flow state
        }
        else {
            String[] flowId = flow.split(":");
            if(flowId.length == STEP_CODE) {
                // first step, ask for client name
                flowStates.put(phoneNumber, type()+":" + body);
                textMessenger.deliver(Map.of(
                        "to", phoneNumber,
                        "body", "Canje Completado "
                )).subscribe();
                flowStates.remove(phoneNumber);
            }
        }
    }
}
