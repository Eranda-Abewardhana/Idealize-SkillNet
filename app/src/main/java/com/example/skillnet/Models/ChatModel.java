package com.example.skillnet.Models;

public class ChatModel {
    String code;
    String massage;
    PersonData user;
    PersonData otherUser;

    public ChatModel(String code, String massage, PersonData user, PersonData otherUser) {
        this.code = code;
        this.massage = massage;
        this.user = user;
        this.otherUser = otherUser;
    }

    public PersonData getUser() {
        return user;
    }

    public void setUser(PersonData user) {
        this.user = user;
    }

    public PersonData getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(PersonData otherUser) {
        this.otherUser = otherUser;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
