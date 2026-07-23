package com.sunlee.sys.controller;


import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.sys.common.*;
import com.sunlee.sys.entity.Dept;
import com.sunlee.sys.entity.Role;
import com.sunlee.sys.entity.User;
import com.sunlee.sys.service.IDeptService;
import com.sunlee.sys.service.IRoleService;
import com.sunlee.sys.service.IUserService;
import com.sunlee.sys.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * InnoDB free: 9216 kB; (`deptid`) REFER `warehouse/sys_dept`(`id`) ON UPDATE CASC 前端控制器
 * </p>
 *
 * @author sunlee
 * @since 2026-01-15
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IDeptService deptService;

    @Autowired
    private IRoleService roleService;

    /**
     * 查询所有用户
     * @param userVo
     * @return
     */
    @RequestMapping("loadAllUser")
    public DataGridView loadAllUser(UserVo userVo){
        IPage<User> page = new Page<User>(userVo.getPage(),userVo.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //根据用户登录名称以及用户名称模糊查询用户
        queryWrapper.like(StringUtils.isNotBlank(userVo.getName()),"loginname",userVo.getName()).or().eq(StringUtils.isNotBlank(userVo.getName()),"name",userVo.getName());
        queryWrapper.like(StringUtils.isNotBlank(userVo.getAddress()),"address",userVo.getAddress());
        //查询系统用户（包含超级管理员和普通用户）
        queryWrapper.in("type", Constast.USER_TYPE_SUPER, Constast.USER_TYPE_NORMAL);
        queryWrapper.eq(userVo.getDeptid()!=null,"deptid",userVo.getDeptid());
        queryWrapper.orderByDesc("id");
        userService.page(page,queryWrapper);

        //将所有用户数据放入list中
        List<User> list = page.getRecords();
        for (User user : list) {
            Integer deptId = user.getDeptid();
            if (deptId!=null){
                //先从缓存中去取，如果缓存中没有就去数据库中取
                Dept one = deptService.getById(deptId);
                if (one != null) {
                    //设置user的部门名称
                    user.setDeptname(one.getName());
                }
            }
            Integer mgr = user.getMgr();
            if (mgr!=null&&mgr!=0){
                User one = userService.getById(mgr);
                if (one != null) {
                    //设置user的领导名称
                    user.setLeadername(one.getName());
                }
            }
        }
        return new DataGridView(page.getTotal(),list);
    }

    /**
     * 加载排序码
     * @return
     */
    @RequestMapping("loadUserMaxOrderNum")
    public Map<String,Object> loadUserMaxOrderNum(){
        Map<String,Object> map = new HashMap<String,Object>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.orderByDesc("ordernum");
        IPage<User> page = new Page<>(1,1);
        List<User> list = userService.page(page,queryWrapper).getRecords();
        if (list.size()>0){
            map.put("value",list.get(0).getOrdernum()+1);
        }else {
            map.put("value",1);
        }
        return map;
    }

    /**
     * 根据部门ID查询用户
     * @param deptId
     * @return
     */
    @RequestMapping("loadUsersByDeptId")
    public DataGridView loadUsersByDeptIp(Integer deptId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq(deptId!=null,"deptid",deptId);
        queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        queryWrapper.eq("type",Constast.USER_TYPE_NORMAL);
        List<User> list = userService.list(queryWrapper);
        return new DataGridView(list);
    }

    /**
     * 把用户名转成拼音
     * @param username
     * @return
     */
    @RequestMapping("changeChineseToPinyin")
    public Map<String,Object> changeChineseToPinyin(String username){
        Map<String,Object> map = new HashMap<String, Object>(16);
        if (null!=username){
            map.put("value", PinyinUtils.getPingYin(username));
        }else {
            map.put("value","");
        }
        return map;
    }

    /**
     * 添加用户
     * @param userVo
     * @return
     */
    @RequestMapping("addUser")
    public ResultObj addUser(UserVo userVo){
        try {
            // 手动数据校验
            if (userVo.getName() == null || userVo.getName().trim().isEmpty()) {
                return ResultObj.error("姓名不能为空");
            }
            if (userVo.getName().length() > 50) {
                return ResultObj.error("姓名长度不能超过50个字符");
            }
            if (userVo.getLoginname() == null || userVo.getLoginname().trim().isEmpty()) {
                return ResultObj.error("登录名不能为空");
            }
            if (userVo.getLoginname().length() > 50) {
                return ResultObj.error("登录名长度不能超过50个字符");
            }
            if (userVo.getDeptid() == null) {
                return ResultObj.error("部门不能为空");
            }
            // 校验用户名是否已存在
            QueryWrapper<User> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("loginname", userVo.getLoginname());
            long count = userService.count(checkWrapper);
            if (count > 0) {
                return ResultObj.error("登录名已存在，请更换");
            }
            //设置类型
            userVo.setType(Constast.USER_TYPE_NORMAL);
            //设置盐
            String salt = IdUtil.simpleUUID().toUpperCase();
            userVo.setSalt(salt);
            // 生成随机初始密码（8位字母数字组合）
            String initialPwd = generateRandomPassword(8);
            userVo.setPwd(md5Hash(initialPwd, salt, Constast.HASHITERATIONS));
            //设置用户默认头像
            userVo.setImgpath(Constast.DEFAULT_IMG_USER);
            userService.save(userVo);
            return new ResultObj(Constast.OK, "添加成功，初始密码为: " + initialPwd);
        } catch (Exception e) {
            log.error("添加用户失败: {}", e.getMessage(), e);
            return ResultObj.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 根据id查询一个用户
     * @param id  领导的id
     * @return
     */
    @RequestMapping("loadUserById")
    public DataGridView loadUserById(Integer id){
        User user = userService.getById(id);
        return new DataGridView(user);
    }

    /**
     * 修改用户
     * @param userVo
     * @return
     */
    @RequestMapping("updateUser")
    public ResultObj updateUser(UserVo userVo){
        try {
            // 安全：禁止通过 updateUser 接口修改密码、盐值和用户类型（防提权为超管）
            userVo.setPwd(null);
            userVo.setSalt(null);
            userVo.setType(null);
            userService.updateById(userVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("操作失败: {}", e.getMessage(), e);
            return ResultObj.error("修改失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @RequestMapping("deleteUser/{id}")
    public ResultObj deleteUser(@PathVariable("id") Integer id){
        try {
            userService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("操作失败: {}", e.getMessage(), e);
            return ResultObj.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询当前用户是否是其他用户的直属领导
     * @param userId
     * @return
     */
    @RequestMapping("queryMgrByUserId")
    public ResultObj queryMgrByUserId(Integer userId){
        Boolean isMgr = userService.queryMgrByUserId(userId);
        if (isMgr){
            return ResultObj.DELETE_ERROR_NEWS;
        }else {
            return ResultObj.DELETE_QUERY;
        }
    }

    /**
     * 根据用户id查询角色并选中已拥有的角色
     * @param id 用户id
     * @return
     */
    @RequestMapping("initRoleByUserId")
    public DataGridView initRoleByUserId(Integer id){
        //1.查询所有可用的角色
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        List<Map<String, Object>> listMaps = roleService.listMaps(queryWrapper);
        //2.查询当前用户拥有的角色ID集合
        List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(id);
        for (Map<String, Object> map : listMaps) {
            Boolean LAY_CHECKED=false;
            Integer roleId = (Integer) map.get("id");
            for (Integer rid : currentUserRoleIds) {
                //如果当前用户已有该角色，则让LAY_CHECKED为true。LAY_CHECKED为true时，复选框选中
                if (rid.equals(roleId)){
                    LAY_CHECKED=true;
                    break;
                }
            }
            map.put("LAY_CHECKED",LAY_CHECKED);
        }
        return new DataGridView(Long.valueOf(listMaps.size()),listMaps);
    }

    /**
     * 保存用户和角色的关系
     * @param uid 用户的ID
     * @param ids 用户拥有的角色的ID的数组
     * @return
     */
    @RequestMapping("saveUserRole")
    public ResultObj saveUserRole(Integer uid,Integer[] ids){
        try {
            userService.saveUserRole(uid,ids);
            return ResultObj.DISPATCH_SUCCESS;
        } catch (Exception e) {
            log.error("操作失败: {}", e.getMessage(), e);
            return ResultObj.error("分配失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户的密码
     * @param oldPassword  用户的原密码
     * @param newPwdOne     用户第一次输入的新密码
     * @param newPwdTwo     用户第二次输入的新密码
     * @return
     */
    @RequestMapping("changePassword")
    public ResultObj changePassword(String oldPassword,String newPwdOne,String newPwdTwo){
        //1.先通过session获得当前用户的ID
        User user =(User) WebUtils.getSession().getAttribute("user");
        //2.将oldPassword加盐并散列两次在和数据库中的密码进行对比
        Integer userId = user.getId();
        User user1 = userService.getById(userId);
        if (user1 == null) {
            return ResultObj.UPDATE_ERROR;
        }
        //2.1获得该用户的盐
        String salt = user1.getSalt();
        //2.2通过用户输入的原密码，从数据库中查出的盐，散列次数生成新的旧密码
        String oldPassword2 = md5Hash(oldPassword, salt, Constast.HASHITERATIONS);
        if (oldPassword2.equals(user1.getPwd())){
            if (newPwdOne.equals(newPwdTwo)){
                //3.生成新的密码
                String newPassword = md5Hash(newPwdOne, salt, Constast.HASHITERATIONS);
                user1.setPwd(newPassword);
                userService.updateById(user1);
                return ResultObj.UPDATE_SUCCESS;
            }else {
                return ResultObj.UPDATE_ERROR;
            }
        }else {
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 返回当前已登录的user
     * @return
     */
    @RequestMapping("getNowUser")
    public User getNowUser(){
        //1.获取当前session中的user
        User user = (User) WebUtils.getSession().getAttribute("user");
        return user;
    }


    /**
     * 生成随机密码
     * @param length 密码长度
     * @return 随机密码字符串
     */
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * MD5加盐散列（兼容Shiro Md5Hash格式）
     * 第1次: md5(salt_bytes + source_bytes)，后续: md5(上一次二进制结果)
     */
    private String md5Hash(String source, String salt, int iterations) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] saltBytes = salt.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] sourceBytes = source.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            md.update(saltBytes);
            byte[] hash = md.digest(sourceBytes);
            for (int i = 1; i < iterations; i++) {
                hash = md.digest(hash);
            }
            // 转为小写hex
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改用户
     * @param userVo
     * @return
     */
    @RequestMapping("updateUserInfo")
    public ResultObj updateUserInfo(UserVo userVo){
        try {
            // 安全：个人资料接口只允许修改当前登录用户本人，且禁止修改敏感字段（防越权改密/提权）
            User currentUser = (User) WebUtils.getSession().getAttribute("user");
            if (currentUser == null) {
                return ResultObj.error("未登录或会话已过期");
            }
            userVo.setId(currentUser.getId());
            userVo.setPwd(null);
            userVo.setSalt(null);
            userVo.setType(null);
            userVo.setLoginname(null);
            //用户头像有更新（新上传的图片带 _temp 后缀）
            if (userVo.getImgpath() != null && userVo.getImgpath().endsWith("_temp")) {
                String newName = AppFileUtils.renameFile(userVo.getImgpath());
                userVo.setImgpath(newName);
                //删除原先的图片
                User oldUser = userService.getById(userVo.getId());
                if (oldUser != null) {
                    String oldPath = oldUser.getImgpath();
                    AppFileUtils.removeFileByPath(oldPath);
                }
                //获取存储在session中的user并重新设置user中的图片地址
                currentUser.setImgpath(newName);
                //重新设置user
                WebUtils.getSession().setAttribute("user",currentUser);
            }
            userService.updateById(userVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("操作失败: {}", e.getMessage(), e);
            return ResultObj.error("修改失败: " + e.getMessage());
        }
    }

}

