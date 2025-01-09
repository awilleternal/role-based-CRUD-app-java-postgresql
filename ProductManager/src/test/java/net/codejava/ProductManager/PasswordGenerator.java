package net.codejava.ProductManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String rp = "pass3";

        // Using SHA-256 for hashing
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(rp.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        System.out.println(hexString.toString());  // Printing the hashed password
    }
}
