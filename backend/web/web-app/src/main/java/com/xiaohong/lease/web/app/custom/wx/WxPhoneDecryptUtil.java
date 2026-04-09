package com.xiaohong.lease.web.app.custom.wx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class WxPhoneDecryptUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String decryptPhoneNumber(String sessionKey, String encryptedData, String iv) {
        try {
            byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encryptedDataBytes);
            String json = new String(decrypted, StandardCharsets.UTF_8);

            JsonNode root = OBJECT_MAPPER.readTree(json);
            JsonNode phoneNumberNode = root.get("phoneNumber");
            if (phoneNumberNode == null || phoneNumberNode.isNull()) {
                throw new RuntimeException("手机号解密失败");
            }
            return phoneNumberNode.asText();
        } catch (Exception e) {
            throw new RuntimeException("手机号解密失败", e);
        }
    }
}
