package com.jswm.common;

public interface Constants {
    String JWT_SECRET = "jswm-secret-key-2025-campus-food-ordering-system";
    Long JWT_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    String REDIS_KEY_PREFIX = "jswm:";
    String REDIS_KEY_TOKEN = REDIS_KEY_PREFIX + "token:";
    String REDIS_KEY_CAPTCHA = REDIS_KEY_PREFIX + "captcha:";

    Integer USER_ROLE_STUDENT = 0;
    Integer USER_ROLE_MERCHANT = 1;
    Integer USER_ROLE_ADMIN = 2;

    Integer USER_STATUS_DISABLED = 0;
    Integer USER_STATUS_ENABLED = 1;

    Integer MERCHANT_STATUS_PENDING = 0;
    Integer MERCHANT_STATUS_ACTIVE = 1;
    Integer MERCHANT_STATUS_CLOSED = 2;

    Integer DISH_STATUS_OFFLINE = 0;
    Integer DISH_STATUS_ONLINE = 1;
    Integer DISH_STATUS_AUDITING = 2;

    Integer ORDER_STATUS_PENDING_PAYMENT = 0;
    Integer ORDER_STATUS_PENDING_ACCEPT = 1;
    Integer ORDER_STATUS_PENDING_DELIVERY = 2;
    Integer ORDER_STATUS_DELIVERING = 3;
    Integer ORDER_STATUS_COMPLETED = 4;
    Integer ORDER_STATUS_CANCELLED = 5;

    Integer PAY_TYPE_WECHAT = 1;
    Integer PAY_TYPE_ALIPAY = 2;
    Integer PAY_TYPE_MOCK = 3;
}
