package cn.smbms.service;

import cn.smbms.beans.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {

    User login(String userName, String password);

    List<User> getUserList(String queryUserName,
                           int queryUserRole ,
                           int fromIndex,
                           int pageSize);

    int getUserCount(String queryUserName, int queryUserRole);

    int addUser(User user);

    User ucExist(String userCode);
}
