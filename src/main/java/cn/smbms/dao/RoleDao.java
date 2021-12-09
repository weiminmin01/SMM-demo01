package cn.smbms.dao;

import cn.smbms.beans.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("roleDao")
public interface RoleDao {

    public List<Role> getRoleList();

}
