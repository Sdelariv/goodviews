package be.svend.goodviews.models;

import be.svend.goodviews.services.users.UserScrubber;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Login {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    private String ip;

    // CONSTRUCTOR

    public Login() {
    }

    public Login(User user, String ip) {
        this.user = user;
        this.ip = ip;
    }


    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    // OTHER METHODS

    public Login createScrubbedLogin() {
        Login scrubbedUser = new Login();

        scrubbedUser.setIp(this.ip);
        scrubbedUser.setUser(UserScrubber.scrubAllExceptUsername(this.user));

        return scrubbedUser;
    }

    // TO STRING

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", user=" + user +
                ", ip='" + ip + '\'' +
                '}';
    }
}
