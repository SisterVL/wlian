package com.wlian.dao;


import com.wlian.domain.User;
import com.wlian.util.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class UserDao {
    //注册
    public int registe(User user) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "INSERT INTO `bookstore`.`user`VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int update = runner.update(sql,user.getUid(),user.getUsername(),user.getPassword(),
                user.getName(), user.getEmail(),user.getTelephone(),
                user.getBirthday(),user.getSex(),user.getState(),user.getCode());
        return update;
    }

    public void active(String activeCode) {
        QueryRunner runner = new QueryRunner();
        String sql = "update user set state=? where code=?";
        try {
            int update = runner.update(sql,1,activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long checkName(String username) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from user where username=?";
        //ScalarHandler: 可以返回指定列的一个值或返回一个统计函数的值.
        Long isExist = (Long)runner.query(sql,new ScalarHandler(), username);
        return isExist;
    }

    public User selectUser(String username) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from user where username=?";
        User user = runner.query(sql,new BeanHandler<User>(User.class),username);
        return user;
    }
}
