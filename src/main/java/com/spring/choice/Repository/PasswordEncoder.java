package com.spring.choice.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {

 private static final int SALT_LENGTH = 16;

 public static String encode(String password) {
     try {
         byte[] salt = generateSalt();
         byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
         byte[] combinedBytes = new byte[passwordBytes.length + salt.length];
         System.arraycopy(passwordBytes, 0, combinedBytes, 0, passwordBytes.length);
         System.arraycopy(salt, 0, combinedBytes, passwordBytes.length, salt.length);

         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(combinedBytes);

         String encodedSalt = Base64.getEncoder().encodeToString(salt);
         String encodedHash = Base64.getEncoder().encodeToString(hash);

         return encodedSalt + "$" + encodedHash;
     } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
         return null;
     }
 }
 
 public static boolean matches(String password, String encodedPassword) {
	    try {
	        // 암호화된 비밀번호에서 솔트와 해시를 추출합니다.
	        String[] parts = encodedPassword.split("\\$");
	        if (parts.length != 2) {
	            return false; // 암호화된 비밀번호 형식이 올바르지 않습니다.
	        }
	        String encodedSalt = parts[0];
	        String storedHash = parts[1];

	        // 입력된 비밀번호를 같은 솔트로 암호화합니다.
	        byte[] salt = Base64.getDecoder().decode(encodedSalt);
	        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
	        byte[] combinedBytes = new byte[passwordBytes.length + salt.length];
	        System.arraycopy(passwordBytes, 0, combinedBytes, 0, passwordBytes.length);
	        System.arraycopy(salt, 0, combinedBytes, passwordBytes.length, salt.length);

	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(combinedBytes);
	        String encodedInputHash = Base64.getEncoder().encodeToString(hash);

	        // 암호화된 비밀번호와 입력된 비밀번호의 해시가 일치하는지 확인합니다.
	        return storedHash.equals(encodedInputHash);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


 public static String encodeWithSalt(String password, String encodedSalt) {
     byte[] salt = Base64.getDecoder().decode(encodedSalt);
     byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
     byte[] combinedBytes = new byte[passwordBytes.length + salt.length];
     System.arraycopy(passwordBytes, 0, combinedBytes, 0, passwordBytes.length);
     System.arraycopy(salt, 0, combinedBytes, passwordBytes.length, salt.length);

     try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(combinedBytes);
         return Base64.getEncoder().encodeToString(hash);
     } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
         return null;
     }
 }

 private static byte[] generateSalt() {
     byte[] salt = new byte[SALT_LENGTH];
     SecureRandom random = new SecureRandom();
     random.nextBytes(salt);
     return salt;
 }
}
