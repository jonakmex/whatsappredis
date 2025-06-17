package com.bot.whatsapp.flow;

public interface Flow {
    String type();
    void execute(String phoneNumber, String body);
}
