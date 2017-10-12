package com.wlian.web.servlet;

import com.wlian.domain.Category;
import com.wlian.domain.Product;
import com.wlian.service.AdminService;
import com.wlian.util.CommonsUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddProductServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //创建一个Product来进行封装
        Product product = new Product();
        //map集合来存储一般表单项，然后将其封装入product
        Map<String,Object> map = new HashMap<String,Object>();

        try {
            //创建一个文件项工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //创建工厂类的核心对象
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解析request获得对象集合
            List<FileItem> fileItemList = upload.parseRequest(request);

            //遍历FileItem
            for(FileItem fileItem : fileItemList){
                //判断是否是普通文件项
                boolean isFileItem = fileItem.isFormField();
                if(isFileItem){
                    //如果是简单文件项，将内容存入map集合
                    String fileName = fileItem.getFieldName();
                    String fileValue = fileItem.getString("UTF-8");

                    map.put(fileName,fileValue);
                }else {
                    //不是普通项，将内容写入磁盘----文件上传项 获得文件名称 获得文件的内容
                    String fileName = fileItem.getName();

                    String path = "F:\\IDEA\\wlian\\web\\upload";
                    InputStream in = fileItem.getInputStream();

                    OutputStream out = new FileOutputStream(path+"/"+fileName);
                    IOUtils.copy(in,out);
                    in.close();
                    out.close();
                    fileItem.delete();

                    map.put("pimage","upload/"+fileName);
                }
            }
            //封装product
              /*`pid` varchar(32) NOT NULL,
              `pname` varchar(50) DEFAULT NULL,
              `market_price` double DEFAULT NULL,
              `shop_price` double DEFAULT NULL,
              `pimage` varchar(200) DEFAULT NULL,
              `pdate` date DEFAULT NULL,
              `is_hot` int(11) DEFAULT NULL,
              `pdesc` varchar(255) DEFAULT NULL,
              `pflag` int(11) DEFAULT NULL,
              `cid` varchar(32) DEFAULT NULL*/
            BeanUtils.populate(product,map);
            product.setPid(CommonsUtils.getUUID());
            product.setPdate(new Date());
            product.setPflag(0);

            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);

            AdminService service = new AdminService();
            service.insertProduct(product);
            response.sendRedirect(request.getContextPath()+"/admin/product/add.jsp");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
