package cn.smbms.test;

import cn.smbms.beans.User;
import cn.smbms.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDemo {
    @Test
    public void test01() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        UserService userService = (UserService) context.getBean("userService");
        User user = new User();
        user.setUserName("huyuansong");
        //userService.addUser(user);


    }



}
