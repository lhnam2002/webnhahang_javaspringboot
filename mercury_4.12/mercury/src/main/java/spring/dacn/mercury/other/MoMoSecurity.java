package spring.dacn.mercury.other;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class MoMoSecurity {

    public MoMoSecurity() {
        // Constructor mặc định
    }

    // Phương thức để tạo chuỗi hash từ các thông tin thanh toán
    public String getHash(String partnerCode, String merchantRefId, String amount,
                          String paymentCode, String storeId, String storeName, String publicKeyBase64) throws Exception {

        // Tạo chuỗi JSON từ các thông tin đầu vào
        String json = "{\"partnerCode\":\"" + partnerCode + "\",\"partnerRefId\":\"" + merchantRefId + "\",\"amount\":" + amount +
                ",\"paymentCode\":\"" + paymentCode + "\",\"storeId\":\"" + storeId + "\",\"storeName\":\"" + storeName + "\"}";

        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKeyFromBase64(publicKeyBase64);

        byte[] encryptedData = encryptDataWithPublicKey(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Phương thức để xây dựng chuỗi hash từ thông tin yêu cầu truy vấn (query request)
    public String buildQueryHash(String partnerCode, String merchantRefId, String requestId, String publicKeyBase64) throws Exception {

        // Tạo chuỗi JSON từ các thông tin đầu vào
        String json = "{\"partnerCode\":\"" + partnerCode + "\",\"partnerRefId\":\"" + merchantRefId + "\",\"requestId\":\"" + requestId + "\"}";

        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKeyFromBase64(publicKeyBase64);

        byte[] encryptedData = encryptDataWithPublicKey(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Phương thức để xây dựng chuỗi hash từ thông tin yêu cầu hoàn tiền (refund request)
    public String buildRefundHash(String partnerCode, String merchantRefId, String momoTranId, long amount, String description, String publicKeyBase64) throws Exception {

        // Tạo chuỗi JSON từ các thông tin đầu vào
        String json = "{\"partnerCode\":\"" + partnerCode + "\",\"partnerRefId\":\"" + merchantRefId + "\",\"momoTransId\":\"" + momoTranId + "\",\"amount\":" + amount + ",\"description\":\"" + description + "\"}";

        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKeyFromBase64(publicKeyBase64);

        byte[] encryptedData = encryptDataWithPublicKey(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Phương thức để tạo chữ ký SHA256 từ thông điệp và khóa
    public String signSHA256(String message, String key) throws Exception {
        byte[] keyByte = key.getBytes(StandardCharsets.UTF_8);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, "HmacSHA256");
        hmacSHA256.init(secretKeySpec);

        byte[] hashMessage = hmacSHA256.doFinal(messageBytes);
        StringBuilder hexString = new StringBuilder();

        for (byte b : hashMessage) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private PublicKey getPublicKeyFromBase64(String publicKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private byte[] encryptDataWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
}