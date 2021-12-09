package cn.smbms.controller.role;

import cn.smbms.beans.Role;
import cn.smbms.service.RoleService;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("role")
public class RoleController {

    private static final Logger logger = Logger.getLogger(RoleController.class);

    @Autowired
    @Qualifier("roleService")
    private RoleService roleService;

    @RequestMapping(value = "roleList.html" , produces={"text/html;charset=UTF-8;","application/json;"})
    @ResponseBody
    public String getRoleList() {
        List<Role> roleList = roleService.getRoleList();
        logger.info("Java列表方式："+roleList);
        String s = JSONArray.toJSONString(roleList);
        logger.info("json格式的数据："+s);
        return s ;
    }



}
