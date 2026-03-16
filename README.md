# 极送外卖 (JiSongWaiMai)

> 🍔 校园外卖点餐系统 | Campus Food Ordering System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4FC08D.svg)](https://vuejs.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.x-409EFF.svg)](https://element-plus.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.x-DC382D.svg)](https://redis.io/)

## 📋 项目简介

极送外卖是一个专为校园场景设计的外卖点餐系统，支持学生、商家、管理员三种角色，提供完整的在线点餐、订单管理、店铺管理等功能。

### ✨ 功能特性

- 🔐 **用户认证** - JWT Token 认证，支持登录/注册/密码修改
- 🍽️ **学生端** - 浏览商家、搜索菜品、购物车、下单支付、订单追踪、评价
- 🏪 **商家端** - 店铺管理、菜品管理、订单处理、营收统计
- 👨‍💼 **管理员** - 用户管理、商家审核、订单监控、数据统计
- 📤 **文件上传** - 阿里云 OSS 图片存储
- ⚡ **性能优化** - Redis 缓存、Druid 连接池监控

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 2.7.18
- **ORM**: MyBatis 3.5.x
- **数据库**: MySQL 8.0
- **缓存**: Redis 6.x
- **连接池**: Alibaba Druid
- **工具库**: Hutool, Lombok, JWT
- **文件存储**: 阿里云 OSS

### 前端
- **框架**: Vue 3.3.x
- **构建工具**: Vite 4.x
- **UI组件库**: Element Plus 2.x
- **HTTP客户端**: Axios
- **路由**: Vue Router 4.x
- **图标**: Element Plus Icons

## 📁 项目结构

```
JiSongWaiMai/
├── jswm-backend/          # 后端项目
│   ├── src/main/java/
│   │   └── com/jswm/
│   │       ├── common/    # 常量、结果封装
│   │       ├── config/    # 配置类
│   │       ├── controller/# 控制器层
│   │       ├── dto/       # 数据传输对象
│   │       ├── entity/    # 实体类
│   │       ├── exception/ # 异常处理
│   │       ├── mapper/    # MyBatis Mapper
│   │       ├── service/   # 业务逻辑层
│   │       └── utils/     # 工具类
│   └── src/main/resources/
│       ├── mapper/        # MyBatis XML
│       └── application.yml # 配置文件
│
├── jswm-frontend/         # 前端项目
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── components/    # 公共组件
│   │   ├── layouts/       # 布局组件
│   │   ├── router/        # 路由配置
│   │   ├── utils/         # 工具函数
│   │   ├── views/         # 页面组件
│   │   │   ├── student/   # 学生端
│   │   │   ├── merchant/  # 商家端
│   │   │   └── admin/     # 管理员端
│   │   └── styles/        # 全局样式
│   └── vite.config.js     # Vite配置
│
├── sql/
│   └── init.sql           # 数据库初始化脚本
│
└── design/                # 设计文档
```

## 🚀 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+

### 1. 克隆项目
```bash
git clone https://github.com/NickLhn/JiSongWaiMai.git
cd JiSongWaiMai
```

### 2. 初始化数据库
```bash
mysql -u root -p < sql/init.sql
```

### 3. 配置后端
编辑 `jswm-backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jisong_wm?useUnicode=true&characterEncoding=utf8
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379

aliyun:
  oss:
    endpoint: your-endpoint
    access-key-id: your-access-key
    access-key-secret: your-secret
    bucket-name: your-bucket
```

### 4. 启动后端
```bash
cd jswm-backend
mvn spring-boot:run
```

后端服务将启动在 http://localhost:8080

### 5. 启动前端
```bash
cd jswm-frontend
npm install
npm run dev
```

前端服务将启动在 http://localhost:3000

### 6. 访问系统
- 学生端: http://localhost:3000
- 商家端: http://localhost:3000/login (使用商家账号登录后自动跳转)
- 管理员: http://localhost:3000/login (使用管理员账号登录后自动跳转)

## 👤 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 学生 | student1 | 123456 |
| 商家 | merchant1 | 123456 |
| 管理员 | admin | 123456 |

## 📚 API 文档

启动后端后访问：
- Druid 监控: http://localhost:8080/druid
- Swagger API: (待添加)

## 🔒 安全说明

- JWT Secret 在生产环境应修改为强随机字符串
- 数据库密码、OSS 密钥等敏感信息应使用环境变量或配置中心
- 生产环境建议启用 HTTPS

## 📝 数据库表结构

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| biz_merchant | 商家表 |
| biz_dish | 菜品表 |
| biz_category | 分类表 |
| biz_order | 订单表 |
| biz_order_item | 订单明细表 |
| biz_cart | 购物车表 |
| biz_address | 地址表 |
| biz_coupon | 优惠券表 |
| biz_review | 评价表 |
| biz_favorite | 收藏表 |

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目仅供学习和参考使用。

---

Made with ❤️ by JiSong Team
