package com.denspark.db.service;

import com.denspark.model.user.User;

import java.util.List;

public interface CinematrixService {
    User create(User user);

    List<User> getAll(String namedQuery);

    void remove(User user);
}
