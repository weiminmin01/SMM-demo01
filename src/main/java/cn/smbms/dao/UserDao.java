package cn.smbms.dao;

import cn.smbms.beans.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDao")
public interface UserDao {


    User login(@Param("userName") String userName,@Param("password") String password);

    List<User> getUserList(@Param("queryUserName") String queryUserName,
                           @Param("queryUserRole") int queryUserRole ,
                           @Param("fromIndex") int fromIndex,
                           @Param("pageSize") int pageSize
    );


    int getUserCount(@Param("queryUserName") String queryUserName, @Param("queryUserRole") int queryUserRole);

    int addUser(User user);

    User ucExist(@Param("userCode") String userCode);
}
