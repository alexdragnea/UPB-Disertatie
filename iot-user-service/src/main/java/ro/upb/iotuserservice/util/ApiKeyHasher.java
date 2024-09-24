package ro.upb.iotuserservice.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ApiKeyHasher {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static String hashApiKey(String apiKey) {
        return ENCODER.encode(apiKey);
    }

    public static boolean verifyApiKey(String rawApiKey, String hashedApiKey) {
        return ENCODER.matches(rawApiKey, hashedApiKey);
    }
}