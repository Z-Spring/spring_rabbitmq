package com.murphy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user2")
public class User {
    private String email;
    private String name;
    private String password;
    private int uid;
    private String uid2;
}
