package com.jswm.config;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.exception.BusinessException;
import com.jswm.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Pattern MERCHANT_DETAIL_PATTERN = Pattern.compile("^/v1/merchants/\\d+$");
    private static final Pattern DISH_DETAIL_PATTERN = Pattern.compile("^/v1/dishes/\\d+$");
    private static final Pattern REVIEW_MERCHANT_PATTERN = Pattern.compile("^/v1/reviews/merchant/\\d+(/stats)?$");
    private static final Pattern REVIEW_ORDER_PATTERN = Pattern.compile("^/v1/reviews/order/\\d+$");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AuthContext.clear();
        String path = normalizePath(request);
        if (isPublicEndpoint(request.getMethod(), path)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(401, "请先登录");
        }

        Claims claims;
        try {
            claims = JwtUtils.parseToken(token);
        } catch (Exception e) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }

        Long userId = Long.valueOf(claims.get("userId").toString());
        String username = claims.getSubject();
        Integer role = Integer.valueOf(claims.get("role").toString());

        if (path.startsWith("/v1/admin/") && !Constants.USER_ROLE_ADMIN.equals(role)) {
            throw new BusinessException(403, "无权访问该资源");
        }
        if (path.startsWith("/v1/merchant/") && !Constants.USER_ROLE_MERCHANT.equals(role)) {
            throw new BusinessException(403, "无权访问该资源");
        }

        AuthContext.set(userId, username, role);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private boolean isPublicEndpoint(String method, String path) {
        if (path.startsWith("/v1/auth/")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method)) {
            return "/v1/merchants".equals(path)
                    || "/v1/dishes".equals(path)
                    || MERCHANT_DETAIL_PATTERN.matcher(path).matches()
                    || DISH_DETAIL_PATTERN.matcher(path).matches()
                    || REVIEW_MERCHANT_PATTERN.matcher(path).matches()
                    || REVIEW_ORDER_PATTERN.matcher(path).matches();
        }
        return false;
    }

    private String normalizePath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            return path.substring(contextPath.length());
        }
        return path;
    }
}
