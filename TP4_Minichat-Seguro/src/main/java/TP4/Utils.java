package TP4;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    public static final String RESET = "\u001B[0m";
    public static final String[] COLORES = {
    	    "\u001B[31m", // rojo
    	    "\u001B[32m", // verde
    	    "\u001B[33m", // amarillo
    	    "\u001B[34m", // azul
    	    "\u001B[35m", // magenta
    	    "\u001B[36m"  // cian
    	};

    public static SecureRandom sr = new SecureRandom();

    private static SecretKeySpec deriveKey(String clave) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(clave.getBytes(StandardCharsets.UTF_8));
        byte[] key16 = new byte[16];
        System.arraycopy(keyBytes, 0, key16, 0, 16);
        return new SecretKeySpec(key16, "AES");
    }

    public static byte[] encriptarBytes(String clave, byte[] iv, byte[] plain) {
        try {
            SecretKeySpec sks = deriveKey(clave);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(iv));
            return cipher.doFinal(plain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decriptarBytes(String clave, byte[] iv, byte[] cipherBytes) {
        try {
            SecretKeySpec sks = deriveKey(clave);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(iv));
            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
        }
        return null;
    }
}
