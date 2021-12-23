package be.svend.goodviews.models;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static be.svend.goodviews.services.users.PasswordHasher.hashPassword;

@Entity
public class User {

    @Id
    private String username;

    private String profileUrl;

    private String firstName;

    private String lastName;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private TypeOfUser typeOfUser;

    // CONSTRUCTORS

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    // GETTERS & SETTERS

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = hashPassword(password);
    }

    public boolean hasPasswordHash(String passwordHash) {
        if (passwordHash.equals(this.passwordHash)) return true;
        return false;
    }

    public TypeOfUser getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(TypeOfUser typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    /*
    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    public boolean addFriend(User user) {
        if (this.friendList == null) friendList = new ArrayList<>();
        if (user.equals(this)) return false;

        if (this.friendList.contains(user)) return false;

        this.friendList.add(user);
        return true;
    }

    public boolean removeFriend(User user) {
        if (this.friendList == null) return false;
        if (user.equals(this)) return false;

        if (!this.friendList.contains(user)) return false;

        this.friendList.remove(user);
        return true;
    }

     */

    // OTHER

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", typeOfUser=" + typeOfUser +
               // ", friendList=" + friendList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.username.equals(user.getUsername());
    }

}
