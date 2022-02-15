package be.svend.goodviews.services.users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {


    public static String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        String passwordHash = encoder.encode(password);

        return passwordHash;
    }
}
