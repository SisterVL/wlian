package com.wlian.web.servlet;

import com.google.gson.Gson;
import com.wlian.domain.Category;
import com.wlian.domain.Order;
import com.wlian.domain.Product;
import com.wlian.service.AdminService;
import com.wlian.util.CommonsUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminServlet extends BaseServlet {
    //根据oid查询订单信息
    public void findOrderByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oid = request.getParameter("oid");

        AdminService adminService = new AdminService();
        //orderItem product等多表查询
        List<Map<String, Object>> list = adminService.findOrderByOid(oid);
        //将其转换为json字符串传到页面去
        Gson gson = new Gson();
        String json = gson.toJson(list);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(json);
    }

    //查看所有订单
    public void findAllOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService adminService = new AdminService();
        List<Order> orderList = adminService.findAllOrder();
        request.setAttribute("orderList", orderList);
        //response.sendRedirect(request.getContextPath()+"/list.jsp");
        request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
    }

    public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //用List<category> 来接收所有的分类id
        AdminService adminService = new AdminService();
        List<Category> categoryList = adminService.findAllCategory();

        Gson gson = new Gson();
        String json = gson.toJson(categoryList);

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().write(json);
    }

    //删除分类
    public void delectCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cid = request.getParameter("cid");
        AdminService adminService = new AdminService();
        if (cid == null) {
            System.out.println("删除失败");
        } else {
            adminService.deleteCategory(cid);
            findAllCategoryList(request, response);
        }
    }

    //添加分类
    public void addCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        AdminService adminService = new AdminService();
        String cname = request.getParameter("cname");
        String cid = CommonsUtils.getUUID();
        adminService.addCategory(cid, cname);
        response.sendRedirect(request.getContextPath() + "/admin/category/add.jsp");

    }

    //调用
    public void uCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String cid = request.getParameter("cid");
        String cname = request.getParameter("cname");
        session.setAttribute("cid", cid);
        session.setAttribute("cname", cname);
        response.sendRedirect(request.getContextPath() + "/admin/category/edit.jsp");

    }

    //编辑分类
    public void updateCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String cid = request.getParameter("cid");
        String cname = request.getParameter("cname");
        AdminService adminService = new AdminService();
        adminService.updateCname(cid, cname);
        session.removeAttribute("cid");
        session.removeAttribute("cname");
        findAllCategoryList(request, response);

    }

    //展示所有分类
    public void findAllCategoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //用List<category> 来接收所有的分类id
        AdminService adminService = new AdminService();
        List<Category> list = adminService.findAllCategory();
        request.setAttribute("categoryList", list);
        //response.sendRedirect(request.getContextPath()+"/admin/category/list.jsp");
        request.getRequestDispatcher("/admin/category/list.jsp").forward(request, response);
    }

    //获得所有商品
    public void findAllproduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService adminService = new AdminService();
        List<Product> productList = adminService.findAllproduct();
        request.setAttribute("productList", productList);
        //response.sendRedirect(request.getContextPath()+"/admin/product/list.jsp");
        request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);

    }

    //跳转修改商品页面
    public void updateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");
        HttpSession session = request.getSession();
        session.setAttribute("pid", pid);
        response.sendRedirect(request.getContextPath() + "/admin/product/edit.jsp");
    }

    //删除商品
    public void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");
        AdminService adminService = new AdminService();
        adminService.deleteProduct(pid);
        findAllproduct(request, response);
    }
}
