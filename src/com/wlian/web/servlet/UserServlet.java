package com.wlian.web.servlet;

import com.wlian.domain.User;
import com.wlian.service.UserService;
import com.wlian.util.CommonsUtils;
import com.wlian.util.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class UserServlet extends BaseServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        doGet(request,response);
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String methodName = request.getParameter("method");
//        if("register".equals(methodName)){
//            register(request,response);
//        }else if("checkName".equals(methodName)){
//            checkName(request,response);
//        }else if("active".equals(methodName)){
//            active(request,response);
//        }
//    }
    //用户退出
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        response.sendRedirect(request.getContextPath()+"/product?method=index");
    }

    //用户登录
    public void userlogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserService userService = new UserService();
        try {
            User user = userService.selectName(username);
            if(user==null){
                response.sendRedirect(request.getContextPath()+"/loginFail.jsp");
            }else {
                boolean isLogin = password.equals(user.getPassword());
                if(isLogin){
                    session.setAttribute("user",user);
                    response.sendRedirect(request.getContextPath()+"/index.jsp");
                }else {
                    response.sendRedirect(request.getContextPath()+"/loginFail.jsp");
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        //用户注册
    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        //获得表单数据
        Map<String, String[]> properties = request.getParameterMap();
        User user = new User();
        try {
            //自己指定一个类型转换器（将String转成Date）
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class clazz, Object value) {
                    //将string转成date
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = null;
                    try {
                        parse = format.parse(value.toString());
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    return parse;
                }
            }, Date.class);
            //映射封装
            BeanUtils.populate(user, properties);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //private String uid;
        user.setUid(CommonsUtils.getUUID());
        //private String telephone;
        user.setTelephone(null);
        //private int state;//是否激活
        user.setState(0);
        //private String code;//激活码
        String activeCode = CommonsUtils.getUUID();
        user.setCode(activeCode);


        //将user传递给service层
        UserService service = new UserService();
        boolean isRegisterSuccess = service.regist(user);

        //是否注册成功
        if (isRegisterSuccess) {
            //发送激活邮件
            String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户"
                    + "<a href='http://localhost:8080/wlian/active?activeCode=" + activeCode + "'>"
                    + "http://localhost:8080/wlian/active?activeCode=" + activeCode + "</a>";
            try {
                MailUtils.sendMail(user.getEmail(), emailMsg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }


            //跳转到注册成功页面
            response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
        } else {
            //跳转到失败的提示页面
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
        }
    }

    //检查姓名是否存在
    public void checkName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        UserService userService = new UserService();
        boolean isExist = false;
        try {
            isExist = userService.checkName(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String json = "{\"isExist\":" + isExist + "}";
        response.getWriter().write(json);
    }

    //查看是否激活
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String activeCode = request.getParameter("activeCode");
        UserService userService = new UserService();
        userService.active(activeCode);
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
