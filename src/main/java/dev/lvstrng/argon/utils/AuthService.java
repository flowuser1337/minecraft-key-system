package dev.lvstrng.argon.utils; // Asegúrate de que el paquete sea correcto

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthService {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String ACTIVATE_URL = "https://flowclient.shop/api_activate_key.php";
    private static final String VALIDATE_URL = "https://flowclient.shop/validate_hwid.php";

    public static String activateKey(String licenseKey, String hwid) throws Exception {
        Map<Object, Object> data = Map.of("license_key", licenseKey, "hwid", hwid);
        return sendPostRequest(ACTIVATE_URL, data);
    }

    public static String validateHwid(String username, String hwid) throws Exception {
        Map<Object, Object> data = Map.of("username", username, "hwid", hwid);
        return sendPostRequest(VALIDATE_URL, data);
    }

    public static String getHwid() throws Exception {
        String toEncrypt = System.getProperty("os.name") + System.getProperty("user.name") + System.getProperty("os.arch");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(toEncrypt.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String sendPostRequest(String url, Map<Object, Object> data) throws Exception {
        String form = data.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey().toString(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue().toString(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "FlowClientMod")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}