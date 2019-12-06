package com.denspark.db.service;

import com.denspark.db.cinematrix_dao.UserDao;
import com.denspark.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("cinematrixService")
public class CinematrixServiceImpl implements CinematrixService {
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override public User create(User user) {
        return userDao.create(user);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override public List<User> getAll(String namedQuery) {
        return userDao.getAll(namedQuery);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override public void remove(User user) {
        userDao.remove(user);
    }
}
