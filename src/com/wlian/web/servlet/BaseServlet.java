package com.wlian.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("all")
public class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String method = req.getParameter("method");
            Class cla = this.getClass();
            //获得当前字节码中指定的方法
            Method method1 = cla.getMethod(method,HttpServletRequest.class,HttpServletResponse.class);
            //执行相应的方法
            method1.invoke(this,req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
