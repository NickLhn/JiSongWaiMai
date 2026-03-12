-- =============================================
-- 校园点餐系统数据库设计
-- 数据库: jswm
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_general_ci
-- =============================================

DROP DATABASE IF EXISTS jswm;
CREATE DATABASE jswm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE jswm;

-- =============================================
-- 1. 用户表 (sys_user)
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色(0学生/1商家/2管理员)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0禁用/1启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone),
    KEY idx_role (role),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 商家表 (biz_merchant)
-- =============================================
DROP TABLE IF EXISTS biz_merchant;
CREATE TABLE biz_merchant (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    shop_name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    shop_logo VARCHAR(255) DEFAULT NULL COMMENT '店铺Logo',
    shop_address VARCHAR(255) DEFAULT NULL COMMENT '店铺地址',
    description VARCHAR(500) DEFAULT NULL COMMENT '店铺描述',
    category VARCHAR(50) DEFAULT NULL COMMENT '经营类别',
    business_hours VARCHAR(50) DEFAULT NULL COMMENT '营业时间',
    rating DECIMAL(2,1) NOT NULL DEFAULT 5.0 COMMENT '店铺评分',
    sales INT NOT NULL DEFAULT 0 COMMENT '总销量',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0待审核/1营业中/2已关闭)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_shop_name (shop_name),
    KEY idx_category (category),
    KEY idx_status (status),
    KEY idx_rating (rating),
    CONSTRAINT fk_merchant_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- =============================================
-- 3. 菜品分类表 (biz_category)
-- =============================================
DROP TABLE IF EXISTS biz_category;
CREATE TABLE biz_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序(越小越靠前)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0禁用/1启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    KEY idx_sort (sort),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- =============================================
-- 4. 菜品表 (biz_dish)
-- =============================================
DROP TABLE IF EXISTS biz_dish;
CREATE TABLE biz_dish (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    merchant_id BIGINT NOT NULL COMMENT '所属商家ID',
    category_id BIGINT DEFAULT NULL COMMENT '菜品分类ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    image VARCHAR(255) DEFAULT NULL COMMENT '菜品图片',
    description VARCHAR(500) DEFAULT NULL COMMENT '菜品描述',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    sales INT NOT NULL DEFAULT 0 COMMENT '销量',
    rating DECIMAL(2,1) NOT NULL DEFAULT 5.0 COMMENT '评分',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0下架/1上架/2审核中)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    KEY idx_merchant_id (merchant_id),
    KEY idx_category_id (category_id),
    KEY idx_name (name),
    KEY idx_status (status),
    KEY idx_sales (sales),
    KEY idx_rating (rating),
    CONSTRAINT fk_dish_merchant FOREIGN KEY (merchant_id) REFERENCES biz_merchant(id),
    CONSTRAINT fk_dish_category FOREIGN KEY (category_id) REFERENCES biz_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- =============================================
-- 5. 购物车表 (biz_cart)
-- =============================================
DROP TABLE IF EXISTS biz_cart;
CREATE TABLE biz_cart (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    checked TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中(0否/1是)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_dish (user_id, dish_id),
    KEY idx_user_id (user_id),
    KEY idx_merchant_id (merchant_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_cart_dish FOREIGN KEY (dish_id) REFERENCES biz_dish(id),
    CONSTRAINT fk_cart_merchant FOREIGN KEY (merchant_id) REFERENCES biz_merchant(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- =============================================
-- 6. 收货地址表 (biz_address)
-- =============================================
DROP TABLE IF EXISTS biz_address;
CREATE TABLE biz_address (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) DEFAULT NULL COMMENT '省份',
    city VARCHAR(50) DEFAULT NULL COMMENT '城市',
    district VARCHAR(50) DEFAULT NULL COMMENT '区/县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    label VARCHAR(20) DEFAULT NULL COMMENT '地址标签(学校/宿舍/家等)',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认(0否/1是)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_is_default (is_default),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- =============================================
-- 7. 订单表 (biz_order)
-- =============================================
DROP TABLE IF EXISTS biz_order;
CREATE TABLE biz_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '下单用户ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    delivery_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    delivery_address VARCHAR(255) NOT NULL COMMENT '配送地址',
    remark VARCHAR(200) DEFAULT NULL COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态(0待支付/1待接单/2待配送/3配送中/4已完成/5已取消)',
    pay_type TINYINT DEFAULT NULL COMMENT '支付方式(1微信/2支付宝/3模拟支付)',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    deliver_time DATETIME DEFAULT NULL COMMENT '配送时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    cancel_reason VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_merchant_id (merchant_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_order_merchant FOREIGN KEY (merchant_id) REFERENCES biz_merchant(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =============================================
-- 8. 订单详情表 (biz_order_item)
-- =============================================
DROP TABLE IF EXISTS biz_order_item;
CREATE TABLE biz_order_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    dish_image VARCHAR(255) DEFAULT NULL COMMENT '菜品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_dish_id (dish_id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES biz_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_dish FOREIGN KEY (dish_id) REFERENCES biz_dish(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- =============================================
-- 9. 评价表 (biz_review)
-- =============================================
DROP TABLE IF EXISTS biz_review;
CREATE TABLE biz_review (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    dish_id BIGINT DEFAULT NULL COMMENT '菜品ID(可为空，表示对整体订单评价)',
    rating TINYINT NOT NULL COMMENT '评分(1-5)',
    content VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    images VARCHAR(1000) DEFAULT NULL COMMENT '评价图片(JSON数组)',
    is_anonymous TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名(0否/1是)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_id (order_id),
    KEY idx_user_id (user_id),
    KEY idx_merchant_id (merchant_id),
    KEY idx_dish_id (dish_id),
    KEY idx_rating (rating),
    CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES biz_order(id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_review_merchant FOREIGN KEY (merchant_id) REFERENCES biz_merchant(id),
    CONSTRAINT fk_review_dish FOREIGN KEY (dish_id) REFERENCES biz_dish(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- =============================================
-- 10. 用户收藏表 (biz_favorite)
-- =============================================
DROP TABLE IF EXISTS biz_favorite;
CREATE TABLE biz_favorite (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_merchant (user_id, merchant_id),
    KEY idx_user_id (user_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_favorite_merchant FOREIGN KEY (merchant_id) REFERENCES biz_merchant(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- =============================================
-- 11. 系统公告表 (sys_notice)
-- =============================================
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型(1通知/2公告)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0关闭/1开启)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- =============================================
-- 12. 商家资质表 (biz_merchant_qualification)
-- =============================================
DROP TABLE IF EXISTS biz_merchant_qualification;
CREATE TABLE biz_merchant_qualification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    qualification_type VARCHAR(50) NOT NULL COMMENT '资质类型(营业执照/食品经营许可证等)',
    qualification_no VARCHAR(100) DEFAULT NULL COMMENT '资质编号',
    qualification_image VARCHAR(255) NOT NULL COMMENT '资质图片',
    expire_date DATE DEFAULT NULL COMMENT '有效期',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态(0待审核/1已通过/2已拒绝)',
    audit_remark VARCHAR(200) DEFAULT NULL COMMENT '审核备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除/1已删除)',
    PRIMARY KEY (id),
    KEY idx_merchant_id (merchant_id),
    CONSTRAINT fk_qualification_merchant FOREIGN KEY (merchant_id) REFERENCES biz_merchant(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家资质表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO sys_user (username, password, real_name, phone, email, avatar, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800000000', 'admin@jswm.com', NULL, 2, 1);

-- 插入测试学生账号 (密码: 123456)
INSERT INTO sys_user (username, password, real_name, phone, email, avatar, role, status) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '13800000001', 'student1@jswm.com', NULL, 0, 1),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', '13800000002', 'student2@jswm.com', NULL, 0, 1),
('student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', '13800000003', 'student3@jswm.com', NULL, 0, 1);

-- 插入测试商家账号 (密码: 123456)
INSERT INTO sys_user (username, password, real_name, phone, email, avatar, role, status) VALUES
('merchant1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '美味餐厅', '13800000010', 'merchant1@jswm.com', NULL, 1, 1),
('merchant2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '香喷喷快餐', '13800000011', 'merchant2@jswm.com', NULL, 1, 1),
('merchant3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '校园奶茶店', '13800000012', 'merchant3@jswm.com', NULL, 1, 1);

-- 插入商家信息
INSERT INTO biz_merchant (user_id, shop_name, shop_logo, shop_address, description, category, business_hours, rating, sales, status) VALUES
(5, '美味餐厅', '/images/shop/shop1.jpg', '食堂一楼A区', '正宗家常菜，美味又实惠', '快餐简餐', '07:00-21:00', 4.8, 1256, 1),
(6, '香喷喷快餐', '/images/shop/shop2.jpg', '食堂二楼B区', '精选食材，健康美味', '快餐简餐', '07:00-21:00', 4.6, 892, 1),
(7, '校园奶茶店', '/images/shop/shop3.jpg', '食堂一楼C区', '新鲜现做，各种口味奶茶', '饮品甜点', '08:00-22:00', 4.9, 2341, 1);

-- 插入菜品分类
INSERT INTO biz_category (name, sort, status) VALUES
('快餐简餐', 1, 1),
('面食粉类', 2, 1),
('饮品甜点', 3, 1),
('特色小吃', 4, 1),
('水果生鲜', 5, 1);

-- 插入菜品信息
INSERT INTO biz_dish (merchant_id, category_id, name, price, original_price, image, description, stock, sales, rating, status) VALUES
(1, 1, '红烧肉盖饭', 15.00, 18.00, '/images/dish/dish1.jpg', '精选五花肉，肥而不腻', 100, 356, 4.8, 1),
(1, 1, '宫保鸡丁饭', 13.00, 15.00, '/images/dish/dish2.jpg', '经典川菜，香辣可口', 100, 289, 4.7, 1),
(1, 2, '番茄鸡蛋面', 10.00, NULL, '/images/dish/dish3.jpg', '家常味道，营养丰富', 50, 198, 4.6, 1),
(1, 4, '炸鸡腿', 8.00, 10.00, '/images/dish/dish4.jpg', '外酥里嫩，香脆可口', 30, 421, 4.9, 1),
(2, 1, '黄焖鸡米饭', 16.00, 18.00, '/images/dish/dish5.jpg', '鲜嫩鸡肉，浓郁汤汁', 80, 512, 4.8, 1),
(2, 1, '鱼香肉丝饭', 12.00, 14.00, '/images/dish/dish6.jpg', '酸甜可口，下饭神器', 80, 378, 4.7, 1),
(2, 2, '牛肉拉面', 14.00, NULL, '/images/dish/dish7.jpg', '手工拉面，牛肉大块', 60, 267, 4.6, 1),
(3, 3, '珍珠奶茶', 8.00, 10.00, '/images/dish/dish8.jpg', '经典口味，Q弹珍珠', 200, 856, 4.9, 1),
(3, 3, '芒果冰沙', 12.00, 15.00, '/images/dish/dish9.jpg', '新鲜芒果，清凉解暑', 100, 423, 4.8, 1),
(3, 3, '草莓奶昔', 10.00, 12.00, '/images/dish/dish10.jpg', '香甜可口，奶香浓郁', 100, 312, 4.7, 1);

-- 插入收货地址
INSERT INTO biz_address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, label, is_default) VALUES
(2, '张三', '13800000001', '北京市', '海淀区', '学院路街道', 'XX大学学生宿舍1号楼301室', '宿舍', 1),
(2, '张三', '13800000001', '北京市', '海淀区', '学院路街道', 'XX大学图书馆一楼', '图书馆', 0),
(3, '李四', '13800000002', '北京市', '海淀区', '学院路街道', 'XX大学学生宿舍2号楼205室', '宿舍', 1),
(4, '王五', '13800000003', '北京市', '海淀区', '学院路街道', 'XX大学教学楼A座301', '教学楼', 1);

-- 插入系统公告
INSERT INTO sys_notice (title, content, type, status) VALUES
('欢迎使用校园点餐系统', '欢迎使用校园点餐系统，祝您用餐愉快！如有问题请联系管理员。', 1, 1),
('新商家入驻通知', '校园奶茶店已入驻平台，欢迎同学们品尝！', 2, 1);

-- =============================================
-- 创建视图
-- =============================================

-- 订单详情视图
CREATE OR REPLACE VIEW v_order_detail AS
SELECT 
    o.id AS order_id,
    o.order_no,
    o.user_id,
    u.username AS user_name,
    u.real_name AS user_real_name,
    o.merchant_id,
    m.shop_name,
    o.total_amount,
    o.pay_amount,
    o.status,
    o.receiver_name,
    o.receiver_phone,
    o.delivery_address,
    o.create_time AS order_time
FROM biz_order o
LEFT JOIN sys_user u ON o.user_id = u.id
LEFT JOIN biz_merchant m ON o.merchant_id = m.id
WHERE o.is_deleted = 0;

-- 菜品详情视图
CREATE OR REPLACE VIEW v_dish_detail AS
SELECT 
    d.id,
    d.name AS dish_name,
    d.price,
    d.original_price,
    d.image,
    d.description,
    d.stock,
    d.sales,
    d.rating,
    d.status,
    m.id AS merchant_id,
    m.shop_name,
    c.id AS category_id,
    c.name AS category_name
FROM biz_dish d
LEFT JOIN biz_merchant m ON d.merchant_id = m.id
LEFT JOIN biz_category c ON d.category_id = c.id
WHERE d.is_deleted = 0;

-- =============================================
-- 创建存储过程
-- =============================================

DELIMITER //

-- 更新商家评分存储过程
CREATE PROCEDURE sp_update_merchant_rating(IN p_merchant_id BIGINT)
BEGIN
    DECLARE v_avg_rating DECIMAL(2,1);
    
    SELECT IFNULL(AVG(rating), 5.0) INTO v_avg_rating
    FROM biz_review
    WHERE merchant_id = p_merchant_id AND is_deleted = 0;
    
    UPDATE biz_merchant 
    SET rating = v_avg_rating, update_time = NOW()
    WHERE id = p_merchant_id;
END //

-- 更新菜品评分存储过程
CREATE PROCEDURE sp_update_dish_rating(IN p_dish_id BIGINT)
BEGIN
    DECLARE v_avg_rating DECIMAL(2,1);
    
    SELECT IFNULL(AVG(rating), 5.0) INTO v_avg_rating
    FROM biz_review
    WHERE dish_id = p_dish_id AND is_deleted = 0;
    
    UPDATE biz_dish 
    SET rating = v_avg_rating, update_time = NOW()
    WHERE id = p_dish_id;
END //

DELIMITER ;

-- =============================================
-- 创建触发器
-- =============================================

DELIMITER //

-- 订单完成后更新商家销量
CREATE TRIGGER tr_update_merchant_sales
AFTER UPDATE ON biz_order
FOR EACH ROW
BEGIN
    IF NEW.status = 4 AND OLD.status != 4 THEN
        UPDATE biz_merchant 
        SET sales = sales + (
            SELECT SUM(quantity) 
            FROM biz_order_item 
            WHERE order_id = NEW.id
        ),
        update_time = NOW()
        WHERE id = NEW.merchant_id;
    END IF;
END //

-- 订单完成后更新菜品销量
CREATE TRIGGER tr_update_dish_sales
AFTER UPDATE ON biz_order
FOR EACH ROW
BEGIN
    IF NEW.status = 4 AND OLD.status != 4 THEN
        UPDATE biz_dish d
        SET sales = sales + (
            SELECT quantity 
            FROM biz_order_item oi 
            WHERE oi.order_id = NEW.id AND oi.dish_id = d.id
        ),
        update_time = NOW()
        WHERE id IN (
            SELECT dish_id 
            FROM biz_order_item 
            WHERE order_id = NEW.id
        );
    END IF;
END //

DELIMITER ;

-- =============================================
-- 索引优化建议
-- =============================================
-- 1. 订单表按创建时间分区(数据量大时考虑)
-- 2. 评价表按商家ID分区(数据量大时考虑)
-- 3. 定期分析表优化索引: ANALYZE TABLE biz_order, biz_dish, biz_merchant;
