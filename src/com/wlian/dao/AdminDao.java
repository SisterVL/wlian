package com.wlian.dao;

import com.wlian.domain.Category;
import com.wlian.domain.Order;
import com.wlian.domain.Product;
import com.wlian.util.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminDao {
    public List<Category> findAllCategory() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from category";
        return runner.query(sql, new BeanListHandler<Category>(Category.class));
    }

    public void insertProduct(Product product) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into product values(?,?,?,?,?,?,?,?,?,?)";
        runner.update(sql,product.getPid(),product.getPname(),product.getMarket_price(),
                product.getShop_price(),product.getPimage(),product.getPdate(),product.getIs_hot(),
                product.getPdesc(),product.getPflag(),product.getCategory().getCid());
    }

    public List<Order> findAllOrder() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from orders";
        return runner.query(sql, new BeanListHandler<Order>(Order.class));
    }

    public List<Map<String, Object>> findOrderByOid(String oid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "SELECT p.pname,p.pimage,p.shop_price,o.count,o.subtotal FROM orderitem o,product p WHERE o.`pid`=p.`pid` AND o.`oid` = ?";
        return runner.query(sql,new MapListHandler(), oid);
    }

    public void addCategory(String cid, String cname) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into category values(?,?)";
        runner.update(sql,cid,cname);
    }

    public void updateCname(String cid, String cname) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update category set cname=? where cid=?";
        runner.update(sql,cname,cid);
    }

    public List<Product> findAllproduct() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "SELECT pid,pimage,pname,shop_price,is_hot FROM product;";
        return runner.query(sql, new BeanListHandler<Product>(Product.class));
    }

    public void deleteCategory(String cid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "delete from category where cid=?";
        runner.update(sql,cid);
    }

    public boolean updateProduct(Product product) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update product set pname=?,market_price=?,shop_price=?,pimage=?,pdate=?,is_hot=?,pdesc=?,pflag=?,cid=? where pid=?";
        int i = runner.update(sql,product.getPname(),product.getMarket_price(),
                product.getShop_price(),product.getPimage(),product.getPdate(),product.getIs_hot(),
                product.getPdesc(),product.getPflag(),product.getCategory().getCid(),product.getPid());
        return i==1?true:false;
    }

    public void deleteProduct(String pid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "delete from product where pid=?";
        runner.update(sql,pid);
    }
}
