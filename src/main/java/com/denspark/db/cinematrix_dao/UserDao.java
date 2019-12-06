package com.denspark.db.cinematrix_dao;

import com.denspark.model.user.User;

import java.util.List;

public interface UserDao {
    User create(User user);

    List<User> getAll(String namedQuery);

    void remove(User user);

}
