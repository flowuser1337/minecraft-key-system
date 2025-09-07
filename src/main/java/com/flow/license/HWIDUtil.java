package com.flow.license;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class HWIDUtil {
    
    public static String getHWID() {
        try {
            StringBuilder sb = new StringBuilder();
            
            // Obtener MAC addresses
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface ni = networkInterfaces.nextElement();
                    byte[] hardwareAddress = ni.getHardwareAddress();
                    if (hardwareAddress != null) {
                        for (byte b : hardwareAddress) {
                            sb.append(String.format("%02X", b));
                        }
                    }
                }
            } catch (Exception e) {
                // Fallback si no podemos obtener las interfaces de red
            }
            
            // Incluir información del sistema
            sb.append(System.getProperty("os.name"));
            sb.append(System.getProperty("user.name"));
            sb.append(System.getProperty("os.arch"));
            sb.append(System.getenv("PROCESSOR_IDENTIFIER"));
            sb.append(System.getenv("PROCESSOR_ARCHITECTURE"));
            
            // Crear un hash SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(sb.toString().getBytes());
            
            // Convertir a formato hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}