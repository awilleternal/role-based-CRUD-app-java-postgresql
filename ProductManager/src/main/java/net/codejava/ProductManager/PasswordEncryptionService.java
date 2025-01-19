package net.codejava.ProductManager;


import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
public class PasswordEncryptionService {
    private static final int SALT_LENGTH = 16; // Length of the salt in bytes
    private static final int ITERATIONS = 10000; // Number of iterations for the PBKDF2 algorithm
    private static final int KEY_LENGTH = 256; // Length of the hash (in bits)

    // Method to hash the password
    public String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt); // Generate random salt

        byte[] hash = generateHash(password.toCharArray(), salt); // Generate the hash

        // Combine salt and hash into a single array
        byte[] combined = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, combined, 0, salt.length); // Copy salt
        System.arraycopy(hash, 0, combined, salt.length, hash.length); // Copy hash

        return Base64.getEncoder().encodeToString(combined); // Encode combined result as a Base64 string
    }

    // Method to verify the password
    public boolean verifyPassword(String password, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Decode the stored hash
        byte[] combined = Base64.getDecoder().decode(storedHash);

        // Extract the salt from the stored hash (first SALT_LENGTH bytes)
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);

        // Extract the hash part from the stored hash (the rest after the salt)
        byte[] storedPasswordHash = new byte[combined.length - SALT_LENGTH];
        System.arraycopy(combined, SALT_LENGTH, storedPasswordHash, 0, storedPasswordHash.length);

        // Generate the hash for the provided password using the extracted salt
        byte[] hash = generateHash(password.toCharArray(), salt);

        // Compare the generated hash with the stored hash byte-by-byte
        for (int i = 0; i < storedPasswordHash.length; i++) {
            if (storedPasswordHash[i] != hash[i]) {
                return false; // If any byte doesn't match, return false
            }
        }

        return true; // Return true if the hashes match
    }

    // Helper method to generate the hash using PBKDF2 with HmacSHA256
    private byte[] generateHash(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded(); // Generate and return the hash
    }
}
