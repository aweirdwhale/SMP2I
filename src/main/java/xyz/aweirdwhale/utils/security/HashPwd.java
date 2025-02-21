package xyz.aweirdwhale.utils.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class HashPwd {



    public static String hash(String password)  throws xyz.aweirdwhale.utils.exceptions.SecurityException {
        // Hash the password using SHA-256
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new SecurityException("Erreur lors du téléchargement de : " + e.getMessage());
        }
    }
}
