package com.jswm.common;

import com.jswm.exception.BusinessException;

import java.util.Arrays;
import java.util.Objects;

public final class AuthContext {

    private static final ThreadLocal<UserInfo> HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(Long userId, String username, Integer role) {
        HOLDER.set(new UserInfo(userId, username, role));
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static Long getUserId() {
        UserInfo userInfo = HOLDER.get();
        if (userInfo == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userInfo.userId;
    }

    public static String getUsername() {
        UserInfo userInfo = HOLDER.get();
        if (userInfo == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userInfo.username;
    }

    public static Integer getRole() {
        UserInfo userInfo = HOLDER.get();
        if (userInfo == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userInfo.role;
    }

    public static boolean hasRole(Integer... roles) {
        Integer currentRole = getRole();
        return Arrays.stream(roles).anyMatch(role -> Objects.equals(role, currentRole));
    }

    public static void requireRole(Integer... roles) {
        if (!hasRole(roles)) {
            throw new BusinessException(403, "无权访问该资源");
        }
    }

    private static final class UserInfo {
        private final Long userId;
        private final String username;
        private final Integer role;

        private UserInfo(Long userId, String username, Integer role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }
    }
}
