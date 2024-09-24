package ro.upb.iotuserservice.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ApiKeyGenerator {

    private static final String SECRET_KEY = "eiJKB1jVUT+4k1GwAE0wUliKOhsS32hcreh/K/hD/Uk=";

    public static String generateApiKey(String userId) {
        long timestamp = System.currentTimeMillis();
        String data = userId + ":" + timestamp + ":" + SECRET_KEY;
        return hashData(data);
    }

    private static String hashData(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating API key", e);
        }
    }
}