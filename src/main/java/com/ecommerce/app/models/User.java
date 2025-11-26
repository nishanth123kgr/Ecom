package com.ecommerce.app.models;

import java.sql.Date;
import java.sql.ResultSet;

public class User {
    private int id;
    private String name;
    private transient String passwordHash;
    private String email;
    private String role;
    private String mobileNumber;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    public User(int id, String name, String passwordHash, String email, String role, String mobileNumber, boolean isActive, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.mobileNumber = mobileNumber;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
