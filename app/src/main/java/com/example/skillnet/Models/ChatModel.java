package com.example.skillnet.Models;

public class ChatModel {
    String code;
    String massage;

    public ChatModel(String code, String massage) {
        this.code = code;
        this.massage = massage;
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
