package commons;

import org.apache.commons.codec.binary.Base64;
import play.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordService {
    private static PasswordService instance = null;
    private static final String SALT = "Bizwe62DefO0er";

    public String encrypt(String plaintext){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
            md.update((plaintext + SALT).getBytes("UTF-8"));
        } catch (Exception e) {
            Logger.error("Exception while encrypting password: " + e.getMessage());
        }

        byte[] raw = md.digest();
        String hash = new String(Base64.encodeBase64(raw));
        return hash;
    }

    public static PasswordService getInstance() {
        if (instance == null) {
            instance = new PasswordService();
        }
        return instance;
    }
}