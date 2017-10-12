package com.wlian.web.servlet;

import com.google.gson.Gson;
import com.wlian.domain.*;
import com.wlian.service.ProductService;
import com.wlian.util.CommonsUtils;
import com.wlian.util.PaymentUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ProductServlet extends BaseServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        doGet(request,response);
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String methodName = request.getParameter("method");
//        if("index".equals(methodName)){
//            index(request,response);
//        }else if("categorList".equals(methodName)){
//            categorList(request,response);
//        }else if("productInfo".equals(methodName)){
//            productInfo(request,response);
//        }else if("productList".equals(methodName)){
//            productList(request,response);
//        }
//    }

    //查看我的订单
    public void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String pageStr = request.getParameter("currentPage");
        if(pageStr==null) pageStr="1";
        int currentPage = Integer.parseInt(pageStr);
        int currentCount = 5;
        User user = (User) session.getAttribute("user");

        if(user==null){
            response.sendRedirect(request.getContextPath()+"/login.jsp");
            return;
        }

        ProductService productService = new ProductService();
        PageBean pageBean = productService.findOrdersByuid(user.getUid(),currentPage,currentCount);
        List<Order> orders = null;
        //遍历list
        if(pageBean!=null){
            String uid = user.getUid();
            orders = pageBean.getList();
            request.setAttribute("uid",uid);
            for(Order order : orders){
                String oid = order.getOid();
                List<Map<String,Object>> orderItemList = productService.findOrderItem(oid);
                //将orderItemList转换为多个orderItem
                for(Map<String,Object> map : orderItemList){
                    try {
                        OrderItem orderItem = new OrderItem();
                        //p.pimage,p.pname,p.shop_price,o.count,o.subtotal
                        BeanUtils.populate(orderItem,map);
                        Product product = new Product();
                        BeanUtils.populate(product,map);
                        orderItem.setProduct(product);
                        //将orderItem封装到order的orderList里面
                        order.getOrderItemList().add(orderItem);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        request.setAttribute("orderList",orders);
        request.setAttribute("pageBean",pageBean);
        request.getRequestDispatcher("/order_list.jsp").forward(request, response);
    }

    //支付、提交
    public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String,String[]> parametermap = request.getParameterMap();
        Order order = new Order();
        try {
            BeanUtils.populate(order,parametermap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将地址和收货信息存入数据库(将收货人信息存入数据库)
        ProductService productService = new ProductService();
        productService.updateOrder(order);


        //支付订单
        // 获得 支付必须基本数据
        String orderid = request.getParameter("oid");
        String money = "0.01";
        // 银行
        String pd_FrpId = request.getParameter("pd_FrpId");

        // 发给支付公司需要哪些数据
        String p0_Cmd = "Buy";
        String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
        String p2_Order = orderid;
        String p3_Amt = money;
        String p4_Cur = "CNY";
        String p5_Pid = "";
        String p6_Pcat = "";
        String p7_Pdesc = "";
        // 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
        // 第三方支付可以访问网址
        String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
        String p9_SAF = "";
        String pa_MP = "";
        String pr_NeedResponse = "1";
        // 加密hmac 需要密钥
        String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
                "keyValue");
        String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
                p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
                pd_FrpId, pr_NeedResponse, keyValue);


        String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
                "&p0_Cmd="+p0_Cmd+
                "&p1_MerId="+p1_MerId+
                "&p2_Order="+p2_Order+
                "&p3_Amt="+p3_Amt+
                "&p4_Cur="+p4_Cur+
                "&p5_Pid="+p5_Pid+
                "&p6_Pcat="+p6_Pcat+
                "&p7_Pdesc="+p7_Pdesc+
                "&p8_Url="+p8_Url+
                "&p9_SAF="+p9_SAF+
                "&pa_MP="+pa_MP+
                "&pr_NeedResponse="+pr_NeedResponse+
                "&hmac="+hmac;

        //重定向到第三方支付平台
        response.sendRedirect(url);

    }

    //提交订单
    public void sunmitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user==null){
            response.sendRedirect(request.getContextPath()+"/login.jsp");
            return;
        }

        //封装一个order
        Order order = new Order();
//        private String oid;//订单编号
//        private Date ordertiem;//订单时间
//        private double total;//订单总价
//        private int state;//订单支付状态1为支付，0为不支付
//        private String address;//收获地址
//        private String name;//收货姓名
//        private String telephone;//收货人电话
//        private User user;//用户

        String oid = CommonsUtils.getUUID();
        order.setOid(oid);
        order.setOrdertime(new Date());
        Cart cart = (Cart) session.getAttribute("cart");
        order.setTotal(cart.getTotal());
        order.setState(0);
        order.setAddress(null);
        order.setName(null);
        order.setTelephone(null);
        order.setUser(user);

        //封装
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        Map<String,CartItem> cartItemMap = cart.getCartItemMap();
        for(Map.Entry<String,CartItem> entry : cartItemMap.entrySet()){
            CartItem cartItem = entry.getValue();
            OrderItem orderItem = new OrderItem();
//            private String itemid;//订单id
//            private int count;//订单数量(购买的单个商品的数量)
//            private double subtotal;//订单小计
//            private Product product;//商品信息
//            private Order order;//属于的订单
            orderItem.setItemid(CommonsUtils.getUUID());
            orderItem.setCount(cartItem.getBuyNum());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setOrderItemList(orderItems);
        ProductService productService = new ProductService();
        productService.submitOrder(order);

        session.setAttribute("order",order);

        //页面跳转
        response.sendRedirect(request.getContextPath()+"/order_info.jsp");

    }
    //清空购物车
    public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");

        response.sendRedirect(request.getContextPath()+"/cart.jsp");
    }

    //删除商品
    public void deleteCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if(cart!=null){
            Map<String,CartItem> cartItemMap = cart.getCartItemMap();
            //修改总价
            cart.setTotal(cart.getTotal()-cartItemMap.get(pid).getSubtotal());
            //在cartItem中删除一项
            cartItemMap.remove(pid);
            cart.setCartItemMap(cartItemMap);

        }
        session.setAttribute("cart",cart);
        response.sendRedirect(request.getContextPath()+"/cart.jsp");
    }
        //加入购物车
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String pid = request.getParameter("pid");
        int buyNum = Integer.parseInt(request.getParameter("buyNum"));

        ProductService productService = new ProductService();

        Product product = productService.finProductByPid(pid);
        //计算小计
        double subtotal = product.getShop_price()*buyNum;

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setBuyNum(buyNum);
        item.setSubtotal(subtotal);

        Cart cart = (Cart) session.getAttribute("cart");
        if(cart==null){
            cart = new Cart();
        }

        //判断购物车中是否存在此商品，就要取出cartItem集合，查看里面是否有该商品，如果有则商品购买数量加
        Map<String,CartItem> cartItemMap = cart.getCartItemMap();
        double newsubtotal = 0.0;
        if(cartItemMap.containsKey(pid)){
            //取原有商品数
            CartItem cartItem1 = cartItemMap.get(pid);
            int oldBuyNum = cartItem1.getBuyNum();
            oldBuyNum+=buyNum;
            cartItem1.setBuyNum(oldBuyNum);
            cart.setCartItemMap(cartItemMap);
            //小计
            double oldsubtotal = cartItem1.getSubtotal();
            newsubtotal = product.getShop_price()*buyNum;
            cartItem1.setSubtotal(oldsubtotal+newsubtotal);
        }else {
            cart.getCartItemMap().put(product.getPid(),item);
            newsubtotal = product.getShop_price()*buyNum;
        }

        //计算全部商品
        double total = cart.getTotal()+newsubtotal;
        cart.setTotal(total);

        session.setAttribute("cart", cart);

        response.sendRedirect(request.getContextPath()+"/cart.jsp");

//        request.getRequestDispatcher("/cart.jsp").forward(request, response);

    }

    //首页商品显示
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService productService = new ProductService();
        //热门商品
        List<Product> hotproductList = productService.finHotProductList();
        //获得最新商品
        List<Product> newproductList = productService.finNewProductList();
        //分类数据
        /*List<Category> categorList;
        categorList = productService.finCategorList();*/

        request.setAttribute("hotproductList", hotproductList);
        request.setAttribute("newproductList", newproductList);
        //request.setAttribute("categorList" ,categorList);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    //显示商品类别
    public void categorList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService productService = new ProductService();
        List<Category> categoryList = productService.finCategorList();
        Gson gson = new Gson();
        String json = gson.toJson(categoryList);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(json);
    }

    //显示详细商品的详细
    public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");
        String cid = request.getParameter("cid");
        String currentPage = request.getParameter("curretnPage");
        if (currentPage == null) {
            currentPage = "1";
        }
        ProductService productService = new ProductService();
        Product product = productService.finProductByPid(pid);
        request.setAttribute("product", product);
        request.setAttribute("cid", cid);
        request.setAttribute("currentPage", currentPage);

        //获得客户端携带的cookie------在cookid里面存放pid
        String pids = pid;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    pids = cookie.getValue();
                    //首先判断访问的pid之前有没有访问过，以3-1-2的格式存放在cookies里，
                    // 然后将String拆分为String数组，将数组转换为LinkedList
                    String[] slist = pids.split("-");
                    List<String> alist = Arrays.asList(slist);
                    LinkedList<String> list = new LinkedList<String>(alist);
                    if (list.contains(pid)) list.remove(pid);
                    list.addFirst(pid);
                    //将数组转换为字符串
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < list.size() && i < 7; i++) {
                        stringBuffer.append(list.get(i));
                        stringBuffer.append("-");
                    }
                    pids = stringBuffer.substring(0, stringBuffer.length() - 1);
                }
            }
        }
        Cookie cookie_pids = new Cookie("pids", pids);
        response.addCookie(cookie_pids);

        request.getRequestDispatcher("/product_info.jsp").forward(request, response);
    }

    //显示订单
    public void orderList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = request.getParameter("uid");
        String pageStr = request.getParameter("currentPage");
        if(pageStr==null) pageStr="1";
        int currentPage = Integer.parseInt(pageStr);
        int currentCount = 5;
        ProductService productService = new ProductService();
        PageBean pageBean = productService.findProductListByCid(uid,currentPage,currentCount);
        request.setAttribute("uid",uid);
        request.setAttribute("pageBean",pageBean);


    }

        //显示该列表下的商品
    public void productList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获得cid
        String cid = request.getParameter("cid");
        String pageStr = request.getParameter("currentPage");
        if(pageStr==null) pageStr="1";
        int currentPage = Integer.parseInt(pageStr);
        int currentCount = 8;
        ProductService productService = new ProductService();
        PageBean pageBean = productService.findProductListByCid(cid,currentPage,currentCount);
        request.setAttribute("cid",cid);
        request.setAttribute("pageBean",pageBean);

        //获得历史商品信息集
        List<Product> historyProduct = new ArrayList<Product>();
        //获得存放cookie的pids
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if("pids".equals(cookie.getName())){
                    String pids = cookie.getValue();
                    String[] split = pids.split("-");
                    for(String pid :split){
                        Product pro = productService.finProductByPid(pid);
                        historyProduct.add(pro);
                    }
                }
            }
        }
        request.setAttribute("historyProduct",historyProduct);
        request.getRequestDispatcher("/product_list.jsp").forward(request,response);
    }
}
