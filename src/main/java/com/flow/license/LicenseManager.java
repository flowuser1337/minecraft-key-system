package com.flow.license;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LicenseManager {
    
    private static final String API_URL = "https://tudominio.com/api/verify.php"; // Cambiar a tu URL real
    
    private String hwid;
    private boolean isActivated = false;
    private String productType;
    private String licenseKey;
    private int userId;
    
    public LicenseManager() {
        this.hwid = HWIDUtil.getHWID();
    }
    
    /**
     * Verifica si existe una licencia válida para el HWID actual
     * @return true si existe una licencia válida, false en caso contrario
     */
    public boolean checkLicense() {
        LicenseResponse response = verifyHWID(this.hwid);
        
        if (response != null && response.isValid) {
            this.isActivated = true;
            this.productType = response.productType;
            this.licenseKey = response.licenseKey;
            this.userId = response.userId;
            return true;
        }
        
        return false;
    }
    
    /**
     * Activa una nueva licencia para el HWID actual
     * @param key La clave de licencia a activar
     * @return La respuesta del servidor
     */
    public LicenseResponse activateLicense(String key) {
        LicenseResponse response = verifyLicense(key, this.hwid);
        
        if (response != null && response.isValid) {
            this.isActivated = true;
            this.productType = response.productType;
            this.licenseKey = response.licenseKey;
            this.userId = response.userId;
        }
        
        return response;
    }
    
    /**
     * Verifica una clave de licencia específica
     */
    private LicenseResponse verifyLicense(String key, String hwid) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            // Datos para enviar
            String data = "action=verify_key" + 
                         "&key=" + URLEncoder.encode(key, "UTF-8") + 
                         "&hwid=" + URLEncoder.encode(hwid, "UTF-8");
            
            // Enviar datos
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes("UTF-8"));
            }
            
            return readResponse(conn);
            
        } catch (Exception e) {
            System.err.println("Error al verificar la licencia: " + e.getMessage());
            return createErrorResponse("Error de conexión: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si existe una licencia para un HWID específico
     */
    private LicenseResponse verifyHWID(String hwid) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            // Datos para enviar
            String data = "action=verify_hwid" + 
                         "&hwid=" + URLEncoder.encode(hwid, "UTF-8");
            
            // Enviar datos
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes("UTF-8"));
            }
            
            return readResponse(conn);
            
        } catch (Exception e) {
            System.err.println("Error al verificar HWID: " + e.getMessage());
            return createErrorResponse("Error de conexión: " + e.getMessage());
        }
    }
    
    /**
     * Lee la respuesta HTTP y la convierte en un objeto LicenseResponse
     */
    private LicenseResponse readResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                
                // Parsear respuesta JSON
                JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
                
                LicenseResponse licenseResponse = new LicenseResponse();
                licenseResponse.isValid = jsonObject.get("valid").getAsBoolean();
                licenseResponse.message = jsonObject.get("message").getAsString();
                
                if (licenseResponse.isValid) {
                    if (jsonObject.has("product_type")) {
                        licenseResponse.productType = jsonObject.get("product_type").getAsString();
                    }
                    if (jsonObject.has("user_id")) {
                        licenseResponse.userId = jsonObject.get("user_id").getAsInt();
                    }
                    if (jsonObject.has("license_key")) {
                        licenseResponse.licenseKey = jsonObject.get("license_key").getAsString();
                    }
                }
                
                return licenseResponse;
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.err.println("Error HTTP " + responseCode + ": " + response);
            }
            return createErrorResponse("Error HTTP " + responseCode);
        }
    }
    
    private LicenseResponse createErrorResponse(String message) {
        LicenseResponse response = new LicenseResponse();
        response.isValid = false;
        response.message = message;
        return response;
    }
    
    public boolean isActivated() {
        return isActivated;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getLicenseKey() {
        return licenseKey;
    }
    
    // Clase interna para parsear respuestas de la API
    public static class LicenseResponse {
        public boolean isValid;
        public String message;
        public String productType;
        public int userId;
        public String licenseKey;
    }
}