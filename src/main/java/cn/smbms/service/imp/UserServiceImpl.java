package cn.smbms.service.imp;

import cn.smbms.beans.User;
import cn.smbms.dao.UserDao;
import cn.smbms.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao ;



    @Override
    public User login(String userName, String password) {
        User user = userDao.login(userName , password);
        return user;
    }

    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole ,int currentPageNo , int pageSize ) {
        // 在业务层计算出 要分页查询的数据起始位置
        int fromIndex = (currentPageNo-1) * pageSize ;
        List<User> userList = userDao.getUserList(queryUserName , queryUserRole  , fromIndex ,pageSize );
        return userList;
    }

    @Override
    public int getUserCount(String queryUserName, int queryUserRole) {
        int count  = userDao.getUserCount(queryUserName , queryUserRole) ;
        return count ;
    }

    @Override
    public int addUser(User user) {
        return userDao.addUser(user);
    }

    @Override
    public User ucExist(String userCode) {
        return userDao.ucExist(userCode);
    }

}
