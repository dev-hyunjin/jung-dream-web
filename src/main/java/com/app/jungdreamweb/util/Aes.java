package com.app.jungdreamweb.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
@Slf4j
public class Aes {

	private final byte[] key = hexStringToByteArray("2BDF247745B276BB847D93F950672642EF943B2AD14D2A57BCA743E048CE516F");
    private Cipher cipher;

    private Aes() {
		try {
			this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			log.error("[ERROR_99] UnExpected Exception - " + e.getMessage());
		}
    }

    private static class AESSingleton {
        private static final Aes instance = new Aes();
    }

    public static Aes getInstance() {
        return AESSingleton.instance;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String encryptString(String string) {
        String retValue = "";

        try {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] encData = cipher.doFinal(string.getBytes());
            retValue = Base64.getEncoder().encodeToString(encData);

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("[ERROR_99] UnExpected Exception - " + e.getMessage());
        }

        return retValue;
    }

    public String decryptString(String base64Str) {
        String retValue = "";

        try {
        	// 고정 샘플 데이터
//            byte[] encData = Base64.getDecoder().decode("4LngSJ+iMdV1SJLClczhng==");
            byte[] encData = Base64.getDecoder().decode(base64Str);
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] decData = cipher.doFinal(encData);

            retValue = new String(decData);

        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            log.error("[ERROR_99] UnExpected Exception - " + e.getMessage());
        }

        return retValue;
    }
}
