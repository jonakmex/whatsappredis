package com.bot.whatsapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class SignatureVerifier {
    private static final String HMAC_ALGO = "HmacSHA256";
    private final byte[] secret;

    public SignatureVerifier(@Value("${whatsapp.api.secret}") String appSecret) {
        // 🔐 your WhatsApp App Secret (should come from application.properties)
        if (appSecret == null) throw new IllegalStateException("Missing app secret");
        this.secret = appSecret.getBytes();
    }

    public boolean ok(String signatureHeader, String rawBody) {
        if (signatureHeader == null || rawBody == null) return false;

        String expected = "sha256=" + hmacSha256(rawBody);
        return expected.equals(signatureHeader);
    }

    private String hmacSha256(String body) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            byte[] digest = mac.doFinal(body.getBytes());
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("HMAC verification failed", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) result.append(String.format("%02x", b));
        return result.toString();
    }
}
