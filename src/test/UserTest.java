package test;

import com.wlian.domain.User;
import com.wlian.service.UserService;

import java.util.Date;

public class UserTest {
    public static void main(String[] args){
        User user = new User("05", "zhangsan", "123", "sss", "sss@qq.com", "123",
                null, "c", '0', "1231");
        UserService userService = new UserService();
        boolean b = userService.regist(user);
        System.out.println(b);
    }
}
