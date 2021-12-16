package be.svend.goodviews.test;

import be.svend.goodviews.models.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;

public class TestMain {

    public static void main(String[] args) throws IOException {

        User user = new User();
        user.setUsername("Test");

        User user2 = new User();
        user2.setUsername("Test2");

        User user3 = new User();
        user3.setUsername("Test3");

        user.addFriend(user2);
        user.addFriend(user3);
        user.removeFriend(user2);

        System.out.println(user.getFriendList());









    }
}
