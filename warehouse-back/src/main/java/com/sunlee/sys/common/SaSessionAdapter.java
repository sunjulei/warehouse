package com.sunlee.sys.common;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Enumeration;

/**
 * SA-Token SaSession 适配为 HttpSession 接口，
 * 使现有 WebUtils.getSession().getAttribute("user") 等代码无需修改。
 * @Author: sunlee
 * @Date: 2026/05/25
 */
public class SaSessionAdapter implements HttpSession {

    @Override
    public Object getAttribute(String name) {
        return StpUtil.getSession().get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        StpUtil.getSession().set(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        StpUtil.getSession().delete(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(StpUtil.getSession().getDataMap().keySet());
    }

    @Override public long getCreationTime() { return 0; }
    @Override public String getId() { return StpUtil.getTokenValue(); }
    @Override public long getLastAccessedTime() { return 0; }
    @Override public ServletContext getServletContext() { return null; }
    @Override public void setMaxInactiveInterval(int interval) {}
    @Override public int getMaxInactiveInterval() { return 0; }
    @Override public boolean isNew() { return false; }
    @Override public void invalidate() {}
}
