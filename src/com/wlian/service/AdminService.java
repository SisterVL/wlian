package com.wlian.service;

import com.wlian.dao.AdminDao;
import com.wlian.domain.Category;
import com.wlian.domain.Order;
import com.wlian.domain.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminService {
    public List<Category> findAllCategory() {
        AdminDao adminDao = new AdminDao();
        List<Category> list = null;
        try {
            list = adminDao.findAllCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertProduct(Product product) {
        AdminDao adminDao = new AdminDao();
        try {
            adminDao.insertProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> findAllOrder() {
        AdminDao adminDao = new AdminDao();
        List<Order> list =null;
        try {
            list = adminDao.findAllOrder();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> findOrderByOid(String oid) {
        AdminDao adminDao = new AdminDao();
        List<Map<String, Object>> list = null;
        try {
            list = adminDao.findOrderByOid(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addCategory(String cid, String cname) {
        AdminDao adminDao = new AdminDao();
        try {
            adminDao.addCategory(cid,cname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateCname(String cid, String cname) {
        AdminDao adminDao = new AdminDao();
        try {
            adminDao.updateCname(cid,cname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Product> findAllproduct() {
        AdminDao adminDao = new AdminDao();
        List<Product> list =null;
        try {
            list = adminDao.findAllproduct();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteCategory(String cid) {
        AdminDao adminDao = new AdminDao();
        try {
            adminDao.deleteCategory(cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateProduct(Product product) {
        AdminDao adminDao = new AdminDao();
        boolean is = true;
        try {
            is = adminDao.updateProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return is;
    }

    public void deleteProduct(String pid) {
        AdminDao adminDao = new AdminDao();
        try {
            adminDao.deleteProduct(pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
