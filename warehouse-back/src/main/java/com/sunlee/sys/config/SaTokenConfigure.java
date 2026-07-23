package com.sunlee.sys.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.PermissionDeniedException;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SA-Token 配置 — 注册拦截器（含登录校验 + 接口级权限校验）
 *
 * 权限模型：
 * 1. URL_PERM_MAP 将接口路径映射到所需权限码（与 sys_permission 种子数据的操作级权限码一致，
 *    如 user:view / user:create / user:update / user:delete）。
 * 2. LOGIN_ONLY_PATHS 为"登录即可用"的接口（个人中心、首页看板等），不做权限码校验。
 * 3. 未细分权限码的业务模块（进货、销售、序列号等）按模块级 module:* 校验。
 * 4. 超级管理员（type=0）直接放行。
 * @Author: sunlee
 * @Date: 2026/01/15 21:01
 */
@Slf4j
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * URL 前缀 → 所需权限码 映射表（匹配时取最长前缀）
     */
    private static final Map<String, String> URL_PERM_MAP = new HashMap<>();

    /**
     * 登录即可使用的接口（不校验权限码）
     */
    private static final Set<String> LOGIN_ONLY_PATHS = new HashSet<>();

    static {
        // ===== 登录即可用 =====
        // 个人中心
        LOGIN_ONLY_PATHS.add("/user/changePassword");
        LOGIN_ONLY_PATHS.add("/user/getNowUser");
        LOGIN_ONLY_PATHS.add("/user/updateUserInfo");
        // 首页菜单与看板（所有登录用户可见）
        LOGIN_ONLY_PATHS.add("/menu/loadIndexLeftMenuJson");
        LOGIN_ONLY_PATHS.add("/goods/loadDashboardStats");
        LOGIN_ONLY_PATHS.add("/goods/loadRecentOperations");
        // 公告查看（公告面向全体用户）
        LOGIN_ONLY_PATHS.add("/notice/loadAllNotice");
        LOGIN_ONLY_PATHS.add("/notice/loadNoticeById");
        // 我的提成（仅返回本人数据）
        LOGIN_ONLY_PATHS.add("/commission/loadMyCommission");
        // 图片上传（扩展名/MIME/大小已校验，业务开单普遍需要）
        LOGIN_ONLY_PATHS.add("/file/uploadFile");

        // ===== sys 模块（操作级权限码）=====
        // 用户
        URL_PERM_MAP.put("/user/loadAllUser", "user:view");
        URL_PERM_MAP.put("/user/loadUserMaxOrderNum", "user:view");
        URL_PERM_MAP.put("/user/loadUsersByDeptId", "user:view");
        URL_PERM_MAP.put("/user/loadUserById", "user:view");
        URL_PERM_MAP.put("/user/changeChineseToPinyin", "user:view");
        URL_PERM_MAP.put("/user/queryMgrByUserId", "user:view");
        URL_PERM_MAP.put("/user/initRoleByUserId", "user:view");
        URL_PERM_MAP.put("/user/addUser", "user:create");
        URL_PERM_MAP.put("/user/updateUser", "user:update");
        URL_PERM_MAP.put("/user/deleteUser", "user:delete");
        URL_PERM_MAP.put("/user/saveUserRole", "user:selectRole");
        // 角色
        URL_PERM_MAP.put("/role/loadAllRole", "role:view");
        URL_PERM_MAP.put("/role/initPermissionByRoleId", "role:view");
        URL_PERM_MAP.put("/role/addRole", "role:create");
        URL_PERM_MAP.put("/role/updateRole", "role:update");
        URL_PERM_MAP.put("/role/deleteRole", "role:delete");
        URL_PERM_MAP.put("/role/saveRolePermission", "role:selectPermission");
        // 菜单
        URL_PERM_MAP.put("/menu/loadMenuManagerLeftTreeJson", "menu:view");
        URL_PERM_MAP.put("/menu/loadAllMenu", "menu:view");
        URL_PERM_MAP.put("/menu/loadMenuMaxOrderNum", "menu:view");
        URL_PERM_MAP.put("/menu/checkMenuHasChildrenNode", "menu:view");
        URL_PERM_MAP.put("/menu/addMenu", "menu:create");
        URL_PERM_MAP.put("/menu/updateMenu", "menu:update");
        URL_PERM_MAP.put("/menu/deleteMenu", "menu:delete");
        // 部门
        URL_PERM_MAP.put("/dept/loadDeptManagerLeftTreeJson", "dept:view");
        URL_PERM_MAP.put("/dept/loadAllDept", "dept:view");
        URL_PERM_MAP.put("/dept/loadDeptMaxOrderNum", "dept:view");
        URL_PERM_MAP.put("/dept/checkDeptHasChildrenNode", "dept:view");
        URL_PERM_MAP.put("/dept/addDept", "dept:create");
        URL_PERM_MAP.put("/dept/updateDept", "dept:update");
        URL_PERM_MAP.put("/dept/deleteDept", "dept:delete");
        // 权限
        URL_PERM_MAP.put("/permission/loadPermissionManagerLeftTreeJson", "permission:view");
        URL_PERM_MAP.put("/permission/loadAllPermission", "permission:view");
        URL_PERM_MAP.put("/permission/loadPermissionMaxOrderNum", "permission:view");
        URL_PERM_MAP.put("/permission/checkPermissionHasChildrenNode", "permission:view");
        URL_PERM_MAP.put("/permission/addPermission", "permission:create");
        URL_PERM_MAP.put("/permission/updatePermission", "permission:update");
        URL_PERM_MAP.put("/permission/deletePermission", "permission:delete");
        // 公告（查看登录即可，管理操作需权限）
        URL_PERM_MAP.put("/notice/addNotice", "notice:create");
        URL_PERM_MAP.put("/notice/updateNotice", "notice:update");
        URL_PERM_MAP.put("/notice/deleteNotice", "notice:delete");
        URL_PERM_MAP.put("/notice/batchDeleteNotice", "notice:batchdelete");
        // 登录日志（权限码种子为 info:*）
        URL_PERM_MAP.put("/loginfo/loadAllLoginfo", "info:view");
        URL_PERM_MAP.put("/loginfo/deleteLoginfo", "info:delete");
        URL_PERM_MAP.put("/loginfo/batchDeleteLoginfo", "info:batchdelete");

        // ===== bus 模块（操作级权限码，与种子数据一致）=====
        // 客户
        URL_PERM_MAP.put("/customer/loadAllCustomer", "customer:view");
        URL_PERM_MAP.put("/customer/loadAllCustomerForSelect", "customer:view");
        URL_PERM_MAP.put("/customer/addCustomer", "customer:create");
        URL_PERM_MAP.put("/customer/updateCustomer", "customer:update");
        URL_PERM_MAP.put("/customer/deleteCustomer", "customer:delete");
        // 供应商
        URL_PERM_MAP.put("/provider/loadAllProvider", "provider:view");
        URL_PERM_MAP.put("/provider/loadAllProviderForSelect", "provider:view");
        URL_PERM_MAP.put("/provider/addProvider", "provider:create");
        URL_PERM_MAP.put("/provider/updateProvider", "provider:update");
        URL_PERM_MAP.put("/provider/deleteProvider", "provider:delete");
        // 商品
        URL_PERM_MAP.put("/goods/loadAllGoods", "goods:view");
        URL_PERM_MAP.put("/goods/loadAllGoodsForSelect", "goods:view");
        URL_PERM_MAP.put("/goods/loadGoodsForPOS", "goods:view");
        URL_PERM_MAP.put("/goods/loadGoodsByProviderId", "goods:view");
        URL_PERM_MAP.put("/goods/loadAllWarningGoods", "goods:view");
        URL_PERM_MAP.put("/goods/addGoods", "goods:create");
        URL_PERM_MAP.put("/goods/updateGoods", "goods:update");
        URL_PERM_MAP.put("/goods/updateGoodsAvailable", "goods:update");
        URL_PERM_MAP.put("/goods/regeneratePinyin", "goods:update");
        URL_PERM_MAP.put("/goods/deleteGoods", "goods:delete");
        // 会员（等级规则权限码为 level:*）
        URL_PERM_MAP.put("/member/loadAllMember", "member:view");
        URL_PERM_MAP.put("/member/findMember", "member:view");
        URL_PERM_MAP.put("/member/loadMemberRecords", "member:view");
        URL_PERM_MAP.put("/member/addMember", "member:create");
        URL_PERM_MAP.put("/member/updateMember", "member:update");
        URL_PERM_MAP.put("/member/recharge", "member:update");
        URL_PERM_MAP.put("/member/consume", "member:update");
        URL_PERM_MAP.put("/member/deleteMember", "member:delete");
        URL_PERM_MAP.put("/member/loadLevelRules", "level:view");
        URL_PERM_MAP.put("/member/saveLevelRule", "level:update");
        // 盘点
        URL_PERM_MAP.put("/stocktake/loadAllStocktake", "stocktake:view");
        URL_PERM_MAP.put("/stocktake/loadStocktakeItems", "stocktake:view");
        URL_PERM_MAP.put("/stocktake/createStocktake", "stocktake:create");
        URL_PERM_MAP.put("/stocktake/saveStocktakeItems", "stocktake:create");
        URL_PERM_MAP.put("/stocktake/submitStocktake", "stocktake:finish");
        URL_PERM_MAP.put("/stocktake/cancelStocktake", "stocktake:finish");
        // 提成（种子仅有 view / calculate 两个权限码，写操作归入 calculate）
        URL_PERM_MAP.put("/commission/loadAllRules", "commission:view");
        URL_PERM_MAP.put("/commission/loadRecords", "commission:view");
        URL_PERM_MAP.put("/commission/calculate", "commission:calculate");
        URL_PERM_MAP.put("/commission/confirmRecord", "commission:calculate");
        URL_PERM_MAP.put("/commission/saveRule", "commission:calculate");
        URL_PERM_MAP.put("/commission/deleteRule", "commission:calculate");
        URL_PERM_MAP.put("/commission/saveTiers", "commission:calculate");
        // 业绩排名
        URL_PERM_MAP.put("/performance/salesRanking", "performance:view");
        URL_PERM_MAP.put("/performance/goodsRanking", "performance:view");

        // ===== 未细分权限码的模块，按模块级校验 =====
        URL_PERM_MAP.put("/inport/", "inport:*");
        URL_PERM_MAP.put("/outport/", "outport:*");
        URL_PERM_MAP.put("/sales/", "sales:*");
        URL_PERM_MAP.put("/retail/", "retail:*");
        URL_PERM_MAP.put("/salesback/", "salesback:*");
        URL_PERM_MAP.put("/retailback/", "retailback:*");
        URL_PERM_MAP.put("/report/", "report:*");
        URL_PERM_MAP.put("/category/", "category:*");
        URL_PERM_MAP.put("/serialNumber/", "serialNumber:*");
        URL_PERM_MAP.put("/operationLog/", "operationLog:*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter.match("/**")
                    .notMatch(
                            "/login/login",
                            "/login/getCode",
                            "/login/getCaptchaBase64",
                            "/login/logout",
                            "/index.html*",
                            "/sys/toLogin*",
                            "/resources/**",
                            "/file/showImageByPath*",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    )
                    .check(r -> {
                        // 1. 登录校验
                        StpUtil.checkLogin();
                        // 2. 接口级权限校验
                        checkPermission();
                    });
        })).addPathPatterns("/**");
    }

    /**
     * 接口级权限校验
     */
    private void checkPermission() {
        // 获取请求路径（不含 context-path）
        String path = cn.dev33.satoken.context.SaHolder.getRequest().getRequestPath();
        if (path == null || path.isEmpty()) {
            return;
        }

        // 获取当前用户
        User user = (User) StpUtil.getSession().get("user");
        if (user == null) {
            throw new PermissionDeniedException("未获取到用户信息");
        }

        // 超级管理员直接放行
        if (user.getType().equals(Constast.USER_TYPE_SUPER)) {
            return;
        }

        // 登录即可用的接口
        if (LOGIN_ONLY_PATHS.contains(path)) {
            return;
        }

        // 匹配 URL 获取所需权限码
        String requiredPerm = matchRequiredPermission(path);
        if (requiredPerm == null) {
            // 未配置映射的路径默认放行，但记录日志以便发现漏配（新增 Controller 必须补充映射）
            log.warn("路径 [{}] 未配置权限映射，默认放行，请尽快补充 URL_PERM_MAP 配置", path);
            return;
        }

        // 获取用户权限列表
        List<String> permissions = (List<String>) StpUtil.getSession().get("permissions");
        if (permissions == null || permissions.isEmpty()) {
            throw new PermissionDeniedException("用户无任何权限，访问路径: " + path);
        }

        // 校验权限
        if (!hasPermission(permissions, requiredPerm)) {
            throw new PermissionDeniedException(
                    "缺少权限 [" + requiredPerm + "]，访问路径: " + path
            );
        }
    }

    /**
     * 根据请求路径匹配所需权限码（最长前缀匹配，兼容 /user/deleteUser/{id} 等路径变量）
     * @param path 请求路径
     * @return 所需权限码，null 表示该路径未配置权限控制
     */
    private String matchRequiredPermission(String path) {
        // 精确匹配
        String exact = URL_PERM_MAP.get(path);
        if (exact != null) {
            return exact;
        }
        // 最长前缀匹配
        String best = null;
        int bestLen = -1;
        for (Map.Entry<String, String> entry : URL_PERM_MAP.entrySet()) {
            String urlPrefix = entry.getKey();
            if (path.startsWith(urlPrefix) && urlPrefix.length() > bestLen) {
                best = entry.getValue();
                bestLen = urlPrefix.length();
            }
        }
        return best;
    }

    /**
     * 判断用户权限列表是否包含所需权限
     * @param permissions 用户权限列表
     * @param required 所需权限码
     * @return true 有权限
     */
    private boolean hasPermission(List<String> permissions, String required) {
        // 拥有全局通配权限
        if (permissions.contains("*:*")) {
            return true;
        }
        // 精确匹配（操作级权限码必须精确命中，user:view 不再能放行 user:delete）
        if (permissions.contains(required)) {
            return true;
        }
        // 用户拥有模块级通配权限（如 user:* 可满足 user:delete）
        int colon = required.indexOf(':');
        if (colon > 0 && permissions.contains(required.substring(0, colon) + ":*")) {
            return true;
        }
        // 所需为模块级权限（如 inport:*）：用户拥有该模块任一权限即可
        // （兼容进货/销售等未细分权限码的模块）
        if (required.endsWith(":*")) {
            String module = required.substring(0, required.length() - 2);
            for (String perm : permissions) {
                if (perm.startsWith(module + ":")) {
                    return true;
                }
            }
        }
        return false;
    }
}
