package com.wlian.service;

import com.wlian.dao.UserDao;
import com.wlian.domain.User;
import com.wlian.web.servlet.BaseServlet;

import java.sql.SQLException;

public class UserService extends BaseServlet {
    public boolean regist(User user) {
        UserDao userDao = new UserDao();
        int row = 0;
        try {
            row = userDao.registe(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0 ? true : false;
    }

    public void active(String activeCode) {
        UserDao userDao = new UserDao();
        userDao.active(activeCode);
    }

    public boolean checkName(String username) throws SQLException {
        UserDao userDao = new UserDao();
        long isExis = 0L;
        isExis = userDao.checkName(username);
        return isExis > 0 ? true : false;
    }

    public User selectName(String username) throws SQLException {
        UserDao userDao = new UserDao();
        User user = userDao.selectUser(username);
        return user;
    }
}