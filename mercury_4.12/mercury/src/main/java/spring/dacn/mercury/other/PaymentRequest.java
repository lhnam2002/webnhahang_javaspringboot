package spring.dacn.mercury.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PaymentRequest {

    public PaymentRequest() {
        // Constructor mặc định
    }

    // Phương thức gửi yêu cầu thanh toán đến endpoint và nhận kết quả trả về
    public static String sendPaymentRequest(String endpoint, String postJsonString) {
        try {
            // Tạo đối tượng URL từ endpoint
            URL url = new URL(endpoint);

            // Tạo kết nối HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Thiết lập phương thức gửi yêu cầu và loại nội dung
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Bật ghi dữ liệu
            connection.setDoOutput(true);

            // Ghi dữ liệu JSON vào luồng của yêu cầu
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postJsonString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Đọc phản hồi từ máy chủ
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}