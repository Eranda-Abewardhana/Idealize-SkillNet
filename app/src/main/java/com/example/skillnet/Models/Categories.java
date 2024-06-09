package com.example.skillnet.Models;

import java.util.ArrayList;
import java.util.List;

public class Categories {
    String name;
    String url;
    String code;
    List<PersonData> personDataList = new ArrayList<>();

    public List<PersonData> getPersonDataList() {
        return personDataList;
    }

    public void setPersonDataList(List<PersonData> personDataList) {
        this.personDataList = personDataList;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    public Categories(String code, String name, String url, List<PersonData> personDataList) {
        this.name = name;
        this.url = url;
        this.code = code;
        this.personDataList = personDataList;
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
