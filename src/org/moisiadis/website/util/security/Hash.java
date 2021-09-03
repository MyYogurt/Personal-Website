package org.moisiadis.website.util.security;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Creates hexadecimal representation SHA-256 hash of a String
 */
public class Hash {
    private final String input;

    /**
     * Create Hash object
     * @param input Data to be hashed
     */
    public Hash(String input) {
        this.input = input;
    }

    /**
     * Returns hash string
     * @return hexadecimal representation
     */
    public String toSrting() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
