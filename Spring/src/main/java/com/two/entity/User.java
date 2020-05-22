package com.two.entity;

import lombok.Data;

@Data
public class User {

    private int id;

    private String name;

    private String phone;

    public User(){
    }

    public User(String name,String phone){
        this.name = name;
        this.phone = phone;
    }
}
