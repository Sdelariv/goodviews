package be.svend.goodviews.services;

import be.svend.goodviews.models.Login;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.LoginRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    LoginRepository loginRepo;

    public LoginService(LoginRepository loginRepo) {
        this.loginRepo = loginRepo;
    }

    // FIND METHODS

    public Optional<Login> findByIp(String ip) {
        return loginRepo.findByIp(ip);
    }

    public Optional<Login> findByUsername(String username) {
        return loginRepo.findByUser_Username(username);
    }

    // CREATE METHODS

    public Login createLogin(User user, String ip) {
        Optional<Login> currentUserLogin = findByUsername(user.getUsername());
        currentUserLogin.ifPresent(this::deleteLogin);

        Optional<Login> currentIpLogin = findByIp(ip);
        currentIpLogin.ifPresent(this::deleteLogin);

        Login newLogin = new Login(user, ip);
        return loginRepo.save(newLogin);
    }


    // DELETE METHODS

    public void deleteLogin(Login login) {
        loginRepo.delete(login);
    }
}
