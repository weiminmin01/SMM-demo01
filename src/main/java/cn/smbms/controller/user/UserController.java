package cn.smbms.controller.user;


import cn.smbms.beans.Role;
import cn.smbms.beans.User;
import cn.smbms.service.RoleService;
import cn.smbms.service.UserService;
import cn.smbms.tools.Consts;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    private Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    @Qualifier("userService")
    private UserService userService ;

    @Autowired
    @Qualifier("roleService")
    private RoleService roleService;

    @RequestMapping("index.html")
    public String login() {
        System.out.println("登录操作");
        return "index" ;
    }


    @RequestMapping("dologin.html")
    public String doLogin(@RequestParam("userCode") String userName , @RequestParam("userPassword") String password
            , HttpSession session ) {
        // 到数据库中查找用户是否存在
        User user = userService.login(userName, password);
        System.out.println(user);
        if ( null != user) {
            System.out.println("查找到了用户");
            session.setAttribute(Consts.USER_SESSION, user );
            //  用户输入信息正确，跳转到功能页面
            return "redirect:/user/main.html";
        }else {
            // 输入信息不正确，重新登录
            return "index" ;
        }
    }

    @RequestMapping("main.html")
    public String main() {
        System.out.println("主体功能页面");
        return "frame";
    }

    // 退出
    @RequestMapping("logout.html")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // 用户详情列表
    @RequestMapping("userList.html")
    public ModelAndView userList(HttpServletRequest request , HttpServletResponse response) throws IOException {

        String queryUserName = request.getParameter("queryname");
        String temp = request.getParameter("queryUserRole");
        String pageIndex = request.getParameter("pageIndex");



        int queryUserRole = 0; // 默认用户没有选择任何身份，表示查询所有身份的人
        int pageSize = Consts.pageSize; // 分页的每一页初始大小 5
        int currentPageNo = 1 ; // 当前页码

        if(queryUserName == null){
            queryUserName = "";
        }
        if(temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }
        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(Exception e){
                response.sendRedirect("error.jsp");
            }
        }

        //总数量（表）
        int totalCount	= userService.getUserCount(queryUserName,queryUserRole);
        System.out.println("总数量" + totalCount );

        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        pages.setTotalPageCountByRs();
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        List<User> userList = userService.getUserList(queryUserName, queryUserRole , currentPageNo , pageSize );
        List<Role> roleList = roleService.getRoleList();


        /*List<User> userList = userService.getUserList();
        System.out.println(userList);*/

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userlist");
        modelAndView.addObject("userList" , userList );
        modelAndView.addObject("roleList" , roleList );
        modelAndView.addObject("totalCount" , totalCount);
        modelAndView.addObject("currentPageNo" , currentPageNo);
        modelAndView.addObject("totalPageCount" , totalPageCount);

        return modelAndView;
    }
    // 添加用户

    @RequestMapping("addUser.html")
    public String addUser() {
        return "useradd";
    }

    @RequestMapping("doAddUser.html")
    public String doAddUser( HttpSession session, String userCode , String userName , String userPassword , String gender , String birthday , String phone , String address , String userRole) throws Exception {
        logger.info("进入添加操作：" + userCode + userName );
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)session.getAttribute(Consts.USER_SESSION)).getId());
        int i = userService.addUser(user);
        System.out.println("是否插入成功" + i );
        if(userService.addUser(user) > 0){
            return "redirect:/user/userList.html";
        }else{
            return "redirect:/user/addUser.html";
        }
    }

    // 校验用户名称是否存在
    @RequestMapping("ucexist.html")
    @ResponseBody
    public String ucExist(String userCode) {
        HashMap<String, String> map = new HashMap<>();
        User user = userService.ucExist(userCode);
        if ( user != null ) {
            map.put("userCode" , "exist");
        }else {
            map.put("userCode" , "noExist");
        }
        return JSONArray.toJSONString(map);
    }

















    //多文件上传
    @RequestMapping(value="/useraddsave.html",method= RequestMethod.POST)
    public String addUserSave(User user,HttpSession session,HttpServletRequest request,
                              @RequestParam(value ="attachs", required = false) MultipartFile[] attachs){
        String idPicPath = null;
        String workPicPath = null;
        String errorInfo = null;
        boolean flag = true;
        String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
        logger.info("uploadFile path ============== > "+path);
        for(int i = 0;i < attachs.length ;i++){
            MultipartFile attach = attachs[i];
            if(!attach.isEmpty()){
                if(i == 0){
                    errorInfo = "uploadFileError";
                }else if(i == 1){
                    errorInfo = "uploadWpError";
                }
                String oldFileName = attach.getOriginalFilename();//原文件名
                logger.info("uploadFile oldFileName ============== > "+oldFileName);
                String prefix= FilenameUtils.getExtension(oldFileName);//原文件后缀
                logger.debug("uploadFile prefix============> " + prefix);
                int filesize = 500000;
                logger.debug("uploadFile size============> " + attach.getSize());
                if(attach.getSize() >  filesize){//上传大小不得超过 500k
                    request.setAttribute(errorInfo, " * 上传大小不得超过 500k");
                    flag = false;
                }else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                        || prefix.equalsIgnoreCase("jpeg") || prefix.equalsIgnoreCase("pneg")){//上传图片格式不正确
                    String fileName = System.currentTimeMillis()+ RandomUtils.nextInt(1000000)+"_Personal.jpg";
                    logger.debug("new fileName======== " + attach.getName());
                    File targetFile = new File(path, fileName);
                    if(!targetFile.exists()){
                        targetFile.mkdirs();
                    }
                    //保存
                    try {
                        attach.transferTo(targetFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute(errorInfo, " * 上传失败！");
                        flag = false;
                    }
                    if(i == 0){
                        idPicPath = path+File.separator+fileName;
                    }else if(i == 1){
                        workPicPath = path+File.separator+fileName;
                    }
                    logger.debug("idPicPath: " + idPicPath);
                    logger.debug("workPicPath: " + workPicPath);

                }else{
                    request.setAttribute(errorInfo, " * 上传图片格式不正确");
                    flag = false;
                }
            }
        }
        /*if(flag){
            user.setCreatedBy(((User)session.getAttribute(Consts.USER_SESSION)).getId());
            user.setCreationDate(new Date());
            user.setIdPicPath(idPicPath);
            user.setWorkPicPath(workPicPath);
            if(userService.addUser(user)){
                return "redirect:/user/userlist.html";
            }
        }*/
        return "useradd";
    }












}
