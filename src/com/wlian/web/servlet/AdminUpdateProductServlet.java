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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUpdateProductServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //创建一个Product来进行封装
        Product product = new Product();
        //map集合来存储一般表单项，然后将其封装入product
        Map<String,Object> map = new HashMap<String,Object>();
        HttpSession session = request.getSession();
        try {
            //创建一个文件项工厂
            //创建工厂类的核心对象
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解析request获得对象集合
            List<FileItem> fileItemList = upload.parseRequest(request);

            for(FileItem fileItem : fileItemList){
                //判断是否是普通文件项
                boolean isFileItem = fileItem.isFormField();
                if(isFileItem){
                    //简单文件项，将内容存入map集合
                    String fileValue = fileItem.getString("UTF-8");
                    String fileName = fileItem.getFieldName();
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
            BeanUtils.populate(product,map);
            String pid = request.getParameter("pid");
            product.setPid(pid);
            product.setPdate(new Date());
            product.setPflag(0);

            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);

            AdminService service = new AdminService();
            boolean is = service.updateProduct(product);
            System.out.println(product+"--"+product.getPname()+"--"+product.getMarket_price()+"--"+
                    product.getShop_price()+"--"+product.getPimage()+"--"+product.getPdate()+"--"+product.getIs_hot()+"--"+
                    product.getPdesc()+"--"+product.getPflag()+"--"+product.getCategory().getCid()+"--"+product.getPid());
            session.removeAttribute("pid");
            response.sendRedirect(request.getContextPath()+"/admin/product/edit.jsp");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
