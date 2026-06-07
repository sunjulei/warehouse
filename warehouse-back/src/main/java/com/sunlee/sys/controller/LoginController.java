package com.sunlee.sys.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sunlee.sys.common.*;
import com.sunlee.sys.entity.Loginfo;
import com.sunlee.sys.entity.Permission;
import com.sunlee.sys.entity.User;
import com.sunlee.sys.service.ILoginfoService;
import com.sunlee.sys.service.IPermissionService;
import com.sunlee.sys.service.IRoleService;
import com.sunlee.sys.service.IUserService;
import com.sunlee.sys.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * 登陆前端控制器
 * @Author: sunlee
 * @Date: 2026/01/15 21:33
 */
@Slf4j
@RestController
@RequestMapping("login")
public class LoginController {

    /**
     * 旧Thymeleaf路由 → 新Vue路由映射
     */
    private static final Map<String, String> HREF_MAP = new HashMap<>();
    static {
        HREF_MAP.put("/sys/toDeskManager", "/dashboard");
        HREF_MAP.put("/sys/toUserManager", "/system/user");
        HREF_MAP.put("/sys/toRoleManager", "/system/role");
        HREF_MAP.put("/sys/toDeptManager", "/system/dept");
        HREF_MAP.put("/sys/toMenuManager", "/system/menu");
        HREF_MAP.put("/sys/toPermissionManager", "/system/permission");
        HREF_MAP.put("/sys/toNoticeManager", "/system/notice");
        HREF_MAP.put("/sys/toLoginfoManager", "/system/loginfo");
        HREF_MAP.put("/sys/toCacheManager", "/system/cache");
        HREF_MAP.put("../resources/page/icon.html", "/system/icon");
        HREF_MAP.put("/bus/toCustomerManager", "/business/customer");
        HREF_MAP.put("/bus/toProviderManager", "/business/provider");
        HREF_MAP.put("/bus/toCategoryManager", "/business/category");
        HREF_MAP.put("/bus/toGoodsManager", "/business/goods");
        HREF_MAP.put("/bus/toInportManager", "/business/inport");
        HREF_MAP.put("/bus/toOutportManager", "/business/outport");
        HREF_MAP.put("/bus/toSalesManager", "/business/sales");
        HREF_MAP.put("/bus/toSalesPOS", "/business/sales-pos");
        HREF_MAP.put("/bus/toSalesbackManager", "/business/salesback");
        HREF_MAP.put("/bus/toReportManager", "/business/report");
        HREF_MAP.put("/bus/toInportAnalysis", "/business/inport-analysis");
        HREF_MAP.put("/bus/toGoodsAnalysis", "/business/goods-analysis");
        HREF_MAP.put("/bus/toProfitAnalysis", "/business/profit-analysis");
        HREF_MAP.put("/bus/toStocktakeManager", "/business/stocktake");
    }

    @Autowired
    private ILoginfoService loginfoService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService userService;

    @RequestMapping("login")
    public ResultObj login(UserVo userVo, String code, HttpSession session, String captchaId, HttpServletResponse response, String platform) {
        boolean codeValid = false;
        // 移动端跳过验证码校验（小程序 session 不可靠）
        if ("mobile".equals(platform)) {
            codeValid = true;
        } else if (code != null) {
            String sessionCode;
            if (captchaId != null && !captchaId.isEmpty()) {
                // 移动端：通过 captchaId 从 session 获取验证码
                sessionCode = (String) session.getAttribute("captcha_" + captchaId);
            } else {
                // PC 端：直接从 session 获取验证码
                sessionCode = (String) session.getAttribute("code");
            }
            codeValid = sessionCode != null && sessionCode.equalsIgnoreCase(code);
        }
        if (codeValid) {
            // 根据登录名查询用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("loginname", userVo.getLoginname());
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                return ResultObj.LOGIN_ERROR_PASS;
            }
            // 验证密码（兼容Shiro Md5Hash: 第1次md5(salt+source)，后续md5(二进制结果)）
            String salt = user.getSalt();
            String hashedPwd = user.getPwd();
            String temp;
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                md.update(salt.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                byte[] hash = md.digest(userVo.getPwd().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                for (int i = 1; i < Constast.HASHITERATIONS; i++) {
                    hash = md.digest(hash);
                }
                StringBuilder sb = new StringBuilder();
                for (byte b : hash) {
                    sb.append(String.format("%02x", b & 0xff));
                }
                temp = sb.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (!temp.equals(hashedPwd)) {
                return ResultObj.LOGIN_ERROR_PASS;
            }
            // SA-Token 登录
            StpUtil.login(user.getId());
            // 将 token 放入响应头（供微信小程序等不支持 cookie 的环境使用）
            response.setHeader("satoken", StpUtil.getTokenValue());
            // 将用户信息存入 SA-Token 会话
            StpUtil.getSession().set("user", user);
            // 记录登陆日志
            Loginfo entity = new Loginfo();
            entity.setLoginname(user.getName() + "-" + user.getLoginname());
            entity.setLoginip(WebUtils.getRequest().getRemoteAddr());
            entity.setLogintime(new Date());
            loginfoService.save(entity);

            return ResultObj.LOGIN_SUCCESS;
        } else {
            return ResultObj.LOGIN_ERROR_CODE;
        }
    }

    /**
     * 得到登陆验证码
     */
    @RequestMapping("getCode")
    public void getCode(HttpServletResponse response, HttpSession session) throws IOException {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36, 4, 5);
        session.setAttribute("code", lineCaptcha.getCode());
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            lineCaptcha.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            log.error("验证码输出失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取验证码（base64，供移动端使用）
     */
    @RequestMapping("getCaptchaBase64")
    public Map<String, Object> getCaptchaBase64(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        try {
            LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36, 4, 5);
            String captchaId = java.util.UUID.randomUUID().toString().replace("-", "");
            session.setAttribute("captcha_" + captchaId, lineCaptcha.getCode());
            // 将验证码图片转为 base64
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            lineCaptcha.write(baos);
            String base64 = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
            map.put("code", 200);
            map.put("captchaId", captchaId);
            map.put("image", "data:image/png;base64," + base64);
        } catch (Exception e) {
            log.error("获取验证码失败: {}", e.getMessage(), e);
            map.put("code", -1);
            map.put("msg", "获取验证码失败");
        }
        return map;
    }

    /**
     * 获取当前登录用户信息、菜单树、权限列表
     */
    @RequestMapping("currentUser")
    public Map<String, Object> currentUser() {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!StpUtil.isLogin()) {
                map.put("code", -1);
                map.put("msg", "未登录");
                return map;
            }
            User user = (User) StpUtil.getSession().get("user");
            if (user == null) {
                map.put("code", -1);
                map.put("msg", "未登录");
                return map;
            }
            map.put("code", 200);
            map.put("user", user);

            // 查询菜单
            QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type", Constast.TYPE_MENU);
            queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
            queryWrapper.orderByAsc("ordernum");

            List<Permission> list;
            if (user.getType().equals(Constast.USER_TYPE_SUPER)) {
                list = permissionService.list(queryWrapper);
            } else {
                Integer userId = user.getId();
                List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(userId);
                Set<Integer> pids = new HashSet<>();
                for (Integer rid : currentUserRoleIds) {
                    List<Integer> permissionIds = roleService.queryRolePermissionIdsByRid(rid);
                    pids.addAll(permissionIds);
                }
                if (pids.size() > 0) {
                    queryWrapper.in("id", pids);
                    list = permissionService.list(queryWrapper);
                } else {
                    list = new ArrayList<>();
                }
            }

            List<TreeNode> treeNodes = new ArrayList<>();
            for (Permission p : list) {
                Integer id = p.getId();
                Integer pid = p.getPid();
                String title = p.getTitle();
                String icon = p.getIcon();
                String href = p.getHref();
                if (href != null && HREF_MAP.containsKey(href)) {
                    href = HREF_MAP.get(href);
                }
                Boolean spread = p.getOpen() != null && p.getOpen().equals(Constast.OPEN_TRUE);
                treeNodes.add(new TreeNode(id, pid, title, icon, href, spread));
            }
            List<TreeNode> menuTree = TreeNodeBuilder.build(treeNodes, 1);
            map.put("menus", menuTree);

            // 查询权限码列表
            List<String> permissions = new ArrayList<>();
            if (user.getType().equals(Constast.USER_TYPE_SUPER)) {
                permissions.add("*:*");
            } else {
                Integer userId = user.getId();
                List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(userId);
                Set<Integer> pids = new HashSet<>();
                for (Integer rid : currentUserRoleIds) {
                    List<Integer> permissionIds = roleService.queryRolePermissionIdsByRid(rid);
                    pids.addAll(permissionIds);
                }
                if (pids.size() > 0) {
                    QueryWrapper<Permission> permQuery = new QueryWrapper<>();
                    permQuery.in("id", pids);
                    permQuery.eq("type", Constast.TYPE_PERMISSION);
                    permQuery.eq("available", Constast.AVAILABLE_TRUE);
                    List<Permission> permList = permissionService.list(permQuery);
                    for (Permission p : permList) {
                        permissions.add(p.getPercode());
                    }
                }
            }
            map.put("permissions", permissions);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            map.put("code", -1);
            map.put("msg", "获取用户信息失败: " + e.getMessage());
        }
        return map;
    }

    /**
     * 退出登录
     */
    @RequestMapping("logout")
    public ResultObj logout() {
        StpUtil.logout();
        return new ResultObj(Constast.OK, "退出成功");
    }

}
