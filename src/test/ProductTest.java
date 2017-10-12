package test;

import com.wlian.domain.PageBean;
import com.wlian.domain.Product;
import com.wlian.service.ProductService;

public class ProductTest {
    public static void main(String[] args){
        ProductService productService = new ProductService();
        PageBean<Product> pageBean = productService.findProductListByCid("1",2,6);
        System.out.println(pageBean);
    }


}
