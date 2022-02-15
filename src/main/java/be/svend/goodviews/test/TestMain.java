package be.svend.goodviews.test;

import be.svend.goodviews.models.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.io.IOException;

public class TestMain {

    public static void main(String[] args) throws IOException {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A,12);
        System.out.println(encoder.matches("password","$2a$12$g9gOIP1euowJ03tMuvzotuoUnquJY4SSIPsRyPYr8GSZburYyk7ze"));








    }
}
