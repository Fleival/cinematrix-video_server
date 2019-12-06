package com.denspark.db.cinematrix_dao;

import com.denspark.db.abstract_dao.CommonDao;
import com.denspark.model.user.User;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl extends CommonDao<User> implements UserDao {
}
