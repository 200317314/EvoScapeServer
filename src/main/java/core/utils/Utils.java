package core.utils;

import com.google.gson.Gson;
import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;

public class Utils {
    private static final Gson gson = new Gson();
    private static final SecureRandom random = new SecureRandom();

    public static SecureRandom getRandom() {
        return random;
    }

    public static Gson getGson() {
        return gson;
    }

    public static String decryptRSA(byte[] data, PrivateKey privateKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decryptAES(byte[] data, SecretKey secretKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] encryptAES(String data, SecretKey secretKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }
}
