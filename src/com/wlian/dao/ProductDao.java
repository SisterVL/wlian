package com.wlian.dao;

import com.wlian.domain.Category;
import com.wlian.domain.Order;
import com.wlian.domain.OrderItem;
import com.wlian.domain.Product;
import com.wlian.util.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductDao {
    //获得热门商品
    public List<Product> finHotProductLIst() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where is_hot=? limit ?,?";
        return runner.query(sql,new BeanListHandler<Product>(Product.class),1,0,9);
    }
    //获得最新商品
    public List<Product> finNewProductLIst() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product order by pdate limit ?,?";
        return runner.query(sql,new BeanListHandler<Product>(Product.class),0,9);
    }

    public List<Category> finCategor() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from category";
        List<Category> categories = runner.query(sql,new BeanListHandler<Category>(Category.class));
        for(Category c : categories){
        }
        return categories;
    }

    public int getCount(String cid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from product where cid=?";
        Long count = (Long)runner.query(sql,new ScalarHandler(),cid);
        return count.intValue();
    }

    public List<Product> getCunnterPage(String cid, int index, int currentCount) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where cid=? limit ?,?";
        List<Product> list = runner.query(sql, new BeanListHandler<Product>(Product.class),cid,index,currentCount);
        return list;
    }

    public Product finProductByPid(String pid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="select * from product where pid=?";
        return runner.query(sql,new BeanHandler<Product>(Product.class),pid);
    }

    public void addOrder(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
        runner.update(sql,order.getOid(),order.getOrdertime(),order.getTotal(),order.getState(),
                order.getAddress(),order.getName(),order.getTelephone(),order.getUser().getUid());
    }

    public void addOrderItem(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into orderitem values(?,?,?,?,?)";
        List<OrderItem> orderItems = order.getOrderItemList();
        int a = 0;
        for(OrderItem orderItem : orderItems){
            runner.update(sql,orderItem.getItemid(),orderItem.getCount(),orderItem.getSubtotal(),
                    orderItem.getProduct().getPid(),orderItem.getOrder().getOid());
        }
    }

    public void updateOrder(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set address=?,name=?,telephone=? where oid=?";
        runner.update(sql,order.getAddress(),order.getName(),order.getTelephone(),order.getOid());
    }

    public List<Order> findOrdersByuid(String uid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "SELECT * FROM orders WHERE uid=?";
        return runner.query(sql,new BeanListHandler<Order>(Order.class),uid);
    }

    public List<Map<String,Object>> findOrderItem(String oid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "SELECT p.pimage,p.pname,p.shop_price,o.count,o.subtotal FROM product p,orderitem o WHERE p.pid=o.pid AND o.oid=?;";
        List<Map<String,Object>> list = runner.query(sql, new MapListHandler(),oid);
        return  list;
    }

    public List<Order> getOrderCunnterPage(String uid, int index, int currentCount) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from orders where uid=? limit ?,?";
        List<Order> list = runner.query(sql, new BeanListHandler<Order>(Order.class),uid,index,currentCount);
        return list;
    }

    public int getOrderCount(String uid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from orders where uid=?";
        Long count = (Long)runner.query(sql,new ScalarHandler(),uid);
        return count.intValue();
    }
}
