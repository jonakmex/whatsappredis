package com.bot.whatsapp.flow;

import com.bot.whatsapp.outgoing.Messenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class ApplyRedemptionFlow implements Flow {
    private final Messenger textMessenger;

    @Override
    public String type() {
        return "apply_redemption"; // This should match the button ID in the WhatsApp interactive message
    }

    @Override
    public void execute(String phoneNumber, String body) {
        textMessenger.deliver(Map.of(
                "to", phoneNumber,
                "body", "Hello, " + phoneNumber + "! You said: ApplyRedemptionFlow"
        )).subscribe();
    }
}
