package com.wlian.service;

import com.wlian.dao.ProductDao;
import com.wlian.domain.*;
import com.wlian.util.DataSourceUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductService {
    public List<Product> finHotProductList() {
        ProductDao hotproductDao = new ProductDao();
        List<Product> products = null;
        try {
            products = hotproductDao.finHotProductLIst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> finNewProductList() {
        ProductDao newproductDao = new ProductDao();
        List<Product> products = null;
        try {
            products = newproductDao.finNewProductLIst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Category> finCategorList(){
        ProductDao productDao = new ProductDao();
        List<Category> categor = null;
        try {
            categor = productDao.finCategor();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categor;
    }

    public PageBean findProductListByCid(String cid,int currentPage ,int currentCount) {
        ProductDao productDao = new ProductDao();
        PageBean<Product> pageBean = new PageBean<Product>();
        //设置PageBean

        pageBean.setCurrentPage(currentPage);
        pageBean.setCurrentCount(currentCount);
        int totalCount = 0;
        try {
            totalCount = productDao.getCount(cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setTotalCount(totalCount);
        //封装的总页数
        int totalPage = (int) (1.0*totalCount/currentCount);
        pageBean.setTotalPage(totalPage);
        //显示当页应该显示的数据
        int index = (currentPage-1)*currentCount;
        List<Product> list = null;
        try {
            list = productDao.getCunnterPage(cid,index,currentCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setList(list);
        return pageBean;
    }

    public Product finProductByPid(String pid) {
        ProductDao productDao = new ProductDao();
        Product p = null;
        try {
            p = productDao.finProductByPid(pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    public void submitOrder(Order order) {
        ProductDao productDao = new ProductDao();

        //开启事务
        try {
            DataSourceUtils.startTransaction();
            productDao.addOrder(order);
            productDao.addOrderItem(order);
        } catch (SQLException e) {
            try {
                DataSourceUtils.commitAndRelease();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                DataSourceUtils.commitAndRelease();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateOrder(Order order) {
        ProductDao productDao = new ProductDao();
        try {
            productDao.updateOrder(order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PageBean findOrdersByuid(String uid, int currentPage, int currentCount) {

        PageBean<Order> pageBean = new PageBean<Order>();
        ProductDao productDao = new ProductDao();

        //设置PageBean

        pageBean.setCurrentPage(currentPage);
        pageBean.setCurrentCount(currentCount);
        int totalCount = 0;
        try {
            totalCount = productDao.getOrderCount(uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setTotalCount(totalCount);
        //封装的总页数
        int totalPage = (int) (1.0*totalCount/currentCount);
        pageBean.setTotalPage(totalPage);
        //显示当页应该显示的数据
        int index = (currentPage-1)*currentCount;
        List<Order> list = null;
        try {
            list = productDao.getOrderCunnterPage(uid,index,currentCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setList(list);
        return pageBean;
//        ProductDao productDao = new ProductDao();
//        List<Order> orderList = null;
//        try {
//            orderList = productDao.findOrdersByuid(uid);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return orderList;
    }

    public List<Map<String,Object>> findOrderItem(String oid) {
        ProductDao productDao = new ProductDao();
        List<Map<String,Object>> orderItems = null;
        try {
            orderItems = productDao.findOrderItem(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }
}
