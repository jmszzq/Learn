package com.two.jdbc;

import lombok.Data;

@Data
public class BasicDataSource {

    private String driver;

    private String url;

    private String name;

    private String password;
}
