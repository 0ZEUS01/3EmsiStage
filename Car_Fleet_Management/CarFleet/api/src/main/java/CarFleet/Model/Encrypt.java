package CarFleet.Model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Random;

public class Encrypt {
	private BCryptPasswordEncoder passwordEncoder;
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public Encrypt() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public String generateRandomPassword(int length) {
        StringBuilder randomPassword = new StringBuilder();
        Random random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomPassword.append(CHARACTERS.charAt(index));
        }

        return randomPassword.toString();
    }
}
