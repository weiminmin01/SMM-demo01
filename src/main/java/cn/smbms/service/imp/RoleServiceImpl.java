package cn.smbms.service.imp;

import cn.smbms.beans.Role;
import cn.smbms.dao.RoleDao;
import cn.smbms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    @Qualifier("roleDao")
    private RoleDao roleDao;

    @Override
    public List<Role> getRoleList() {
        return roleDao.getRoleList();
    }
}
