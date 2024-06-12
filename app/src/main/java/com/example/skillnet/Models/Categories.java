package com.example.skillnet.Models;

import java.util.ArrayList;
import java.util.List;

public class Categories {
    String name;
    String url;
    String code;
    List<String> personCodes = new ArrayList<>();

    public Categories() {

    }

    public List<String> getPersonDataList() {
        return personCodes;
    }

    public void setPersonDataList(List<String> personCodes) {
        this.personCodes = personCodes;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    public Categories(String code, String name, String url, List<String> personCodes) {
        this.name = name;
        this.url = url;
        this.code = code;
        this.personCodes = personCodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
