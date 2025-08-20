package dev.lvstrng.argon.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HWID {

    public static String getHWID() {
        try {
            String toEncrypt = System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("os.version") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(toEncrypt.getBytes());
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
