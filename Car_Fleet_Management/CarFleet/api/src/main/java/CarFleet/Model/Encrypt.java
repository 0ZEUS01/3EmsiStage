package CarFleet.Model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encrypt {
	private BCryptPasswordEncoder passwordEncoder;

    public Encrypt() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
