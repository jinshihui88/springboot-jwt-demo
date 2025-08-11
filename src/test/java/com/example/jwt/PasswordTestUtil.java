package com.example.jwt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTestUtil {

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        String rawPassword = "admin123";
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Existing hash: " + existingHash);
        
        boolean matches = passwordEncoder.matches(rawPassword, existingHash);
        System.out.println("Password matches: " + matches);
        
        String newHash = passwordEncoder.encode(rawPassword);
        System.out.println("New hash: " + newHash);
        
        boolean newMatches = passwordEncoder.matches(rawPassword, newHash);
        System.out.println("New hash matches: " + newMatches);
        
        String userPassword = "user123";
        String userHash = passwordEncoder.encode(userPassword);
        System.out.println("\nUser password: " + userPassword);
        System.out.println("User hash: " + userHash);
    }
}
