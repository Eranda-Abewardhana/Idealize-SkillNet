package com.example.skillnet.Models;

public class ChatModel {
    String code;
    String message;
    PersonData user;
    String dateTime;
    PersonData otherUser;

    public ChatModel(String code, String message, PersonData user, PersonData otherUser, String dateTime) {
        this.code = code;
        this.message = message;
        this.user = user;
        this.otherUser = otherUser;
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String massage) {
        this.message = message;
    }
}
