package be.svend.goodviews.controller;


import be.svend.goodviews.models.Login;
import be.svend.goodviews.models.User;
import be.svend.goodviews.services.LoginService;
import be.svend.goodviews.services.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {
    LoginService loginService;
    UserService userService;

    public LoginController(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    // FIND METHODS

    @CrossOrigin
    @GetMapping("/findIpsLogin")
    public ResponseEntity findLoginByIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        System.out.println("FIND LOGIN BY IP CALLED FOR: " + ip);

        Optional<Login> ipLogin = loginService.findByIp(ip);
        if (ipLogin.isEmpty()) return ResponseEntity.notFound().build();
        
        Login scrubbedLogin = ipLogin.get().createScrubbedLogin();
        System.out.println("FOUND LOGIN FOR " + scrubbedLogin);

        return ResponseEntity.ok(scrubbedLogin);
    }

    // CREATE METHODS

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity createLogin(@RequestBody User user, HttpServletRequest request) {
        System.out.println("LOGIN CALLED FOR " + user.toString());
        String ip = request.getRemoteAddr();
        String password = user.getPasswordHash();

        Optional<User> foundUser = userService.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) return ResponseEntity.status(404).body("No such user");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        if (!encoder.matches(password,foundUser.get().getPasswordHash()))
        if (!foundUser.get().getPasswordHash().equals(password)) return ResponseEntity.status(401).body("Wrong password");

        loginService.createLogin(user,ip);
        System.out.println("SENDING BACK LOGIN: " + user.getUsername() + " from " + ip);
        return ResponseEntity.ok().body("Logged in");
    }
}
