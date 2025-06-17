package com.bot.whatsapp.flow;

import com.bot.whatsapp.outgoing.Messenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class AddBalanceFlow implements Flow {
    private final Messenger textMessenger;
    private final ConcurrentHashMap<String,String> flowStates;


    @Override
    public String type() {
        return "add_balance"; // This should match the button ID in the WhatsApp interactive message
    }

    @Override
    public void execute(String phoneNumber, String body) {
        final int STEP_CUSTOMER = 1;
        final int STEP_AMOUNT = 2;

        String flow = flowStates.get(phoneNumber);
        if (flow == null) {
            textMessenger.deliver(Map.of(
                    "to", phoneNumber,
                    "body", "Cliente:"
            )).subscribe();
            flowStates.put(phoneNumber, type()+":"); // Initialize the flow state
        }
        else {
            String[] flowId = flow.split(":");
            if(flowId.length == STEP_CUSTOMER) {
                // first step, ask for client name
                flowStates.put(phoneNumber, type()+":" + body);
                textMessenger.deliver(Map.of(
                        "to", phoneNumber,
                        "body", "Monto:"
                )).subscribe();
            }
            else if(flowId.length == STEP_AMOUNT) {
                flowStates.put(phoneNumber, type()+":" + ":" + flowId[1] + ":" + body);
                textMessenger.deliver(Map.of(
                        "to", phoneNumber,
                        "body", String.format("Abonando $%s a %s.", body, flowId[1])
                )).subscribe();
                flowStates.remove(phoneNumber);
            }
        }
    }
}
