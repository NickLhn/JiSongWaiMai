# PRD: 校园点餐系统

## Overview

基于 Spring Boot + Vue 的校园点餐系统，面向高校师生提供在线点餐服务。系统解决传统校园餐饮排队久、效率低、信息不透明等痛点，实现从点餐、支付、配餐到取餐的全流程数字化管理，提升师生就餐体验与商家运营效率。

## Problem Statement

高校师生在就餐高峰期面临排队拥挤、点餐效率低下、菜品信息不透明的问题，导致就餐体验差、时间浪费严重。

## Target User

**主要用户：** 高校在校学生及教职工

**次要用户：** 校园餐饮商家、平台管理员

**用户特征：**
- 熟悉移动互联网操作，习惯线上点餐支付
- 就餐时间集中（午/晚高峰），对效率敏感
- 注重菜品性价比与评价反馈
- 商家多为个体经营，需要简单易用的管理工具

## Goals & Success Metrics

| Goal | Metric | Target |
|------|--------|--------|
| 提升点餐效率 | 页面响应时间 | ≤ 2秒 |
| 支撑高峰期访问 | 并发用户数 | ≥ 50人 |
| 保障数据准确 | 订单处理准确率 | 100% |
| 系统稳定运行 | 系统可用性 | 7×24小时 |
| 多终端适配 | 浏览器兼容 | Chrome/Firefox/Edge |

## User Stories

### Must Have (MVP)

**学生端：**
- As a 学生, I want to 浏览菜品并搜索 so that 快速找到想吃的食物
- As a 学生, I want to 将菜品加入购物车 so that 方便批量下单
- As a 学生, I want to 在线下单并支付 so that 节省排队时间
- As a 学生, I want to 查看订单状态 so that 知道餐品何时送达
- As a 学生, I want to 对订单进行评价 so that 帮助其他同学选择

**商家端：**
- As a 商家, I want to 管理菜品上下架 so that 及时更新菜单
- As a 商家, I want to 处理订单 so that 高效完成配餐
- As a 商家, I want to 查看销售统计 so that 了解经营状况

**管理员端：**
- As a 管理员, I want to 审核商家入驻 so that 保障平台质量
- As a 管理员, I want to 管理用户信息 so that 维护平台秩序
- As a 管理员, I want to 查看平台数据 so that 掌握运营情况

### Should Have (v1.1)

- As a 学生, I want to 收藏喜欢的商家 so that 快速找到常去的店
- As a 学生, I want to 查看历史订单 so that 方便再次下单
- As a 商家, I want to 设置库存预警 so that 及时补货
- As a 管理员, I want to 配置系统参数 so that 灵活调整平台规则

### Could Have (Future)

- As a 学生, I want to 使用校园一卡通支付 so that 无需额外充值
- As a 学生, I want to 预约取餐时间 so that 错峰取餐
- As a 商家, I want to 推送优惠活动 so that 吸引更多顾客

## Features

### 菜品浏览与搜索
**Priority:** Must Have
**Description:** 学生可浏览商家列表、菜品详情，支持按名称、分类、价格区间搜索
**User value:** 快速找到目标菜品，节省选择时间
**Acceptance criteria:**
- [ ] 商家列表按评分/销量排序展示
- [ ] 菜品支持分类筛选
- [ ] 搜索结果支持按价格排序
- [ ] 菜品详情展示图片、价格、描述、评分

### 购物车
**Priority:** Must Have
**Description:** 学生可将菜品加入购物车，修改数量，计算总价
**User value:** 方便批量下单，灵活调整购买内容
**Acceptance criteria:**
- [ ] 支持添加/删除商品
- [ ] 支持修改商品数量
- [ ] 实时计算订单总价
- [ ] 购物车数据本地持久化

### 在线下单支付
**Priority:** Must Have
**Description:** 学生确认订单信息后提交订单，完成模拟支付
**User value:** 实现无接触点餐，提升效率
**Acceptance criteria:**
- [ ] 支持选择/新增配送地址
- [ ] 支持添加订单备注
- [ ] 支付成功后订单状态自动更新
- [ ] 支付失败给出明确提示

### 订单跟踪
**Priority:** Must Have
**Description:** 学生可查看订单列表和详情，实时跟踪订单状态
**User value:** 了解餐品进度，合理安排取餐时间
**Acceptance criteria:**
- [ ] 订单列表展示状态标签
- [ ] 订单详情展示完整信息
- [ ] 支持取消未接单的订单
- [ ] 状态变更实时更新

### 评价反馈
**Priority:** Must Have
**Description:** 学生对已完成订单进行评分和文字评价
**User value:** 表达用餐体验，帮助其他用户决策
**Acceptance criteria:**
- [ ] 支持1-5星评分
- [ ] 支持文字评价（限500字）
- [ ] 支持上传评价图片
- [ ] 评价后更新商家/菜品评分

### 商家菜品管理
**Priority:** Must Have
**Description:** 商家可添加、编辑、上下架菜品
**User value:** 灵活管理菜单，及时响应经营变化
**Acceptance criteria:**
- [ ] 支持添加菜品（名称、价格、图片、描述）
- [ ] 支持编辑菜品信息
- [ ] 支持一键上下架
- [ ] 支持设置库存数量

### 商家订单处理
**Priority:** Must Have
**Description:** 商家查看待处理订单，进行接单、拒单、配送状态更新
**User value:** 高效处理订单，提升服务质量
**Acceptance criteria:**
- [ ] 新订单提醒通知
- [ ] 支持接单/拒单操作
- [ ] 支持更新配送状态
- [ ] 订单按状态分类展示

### 商家营收统计
**Priority:** Should Have
**Description:** 商家查看日/周/月销售额，图表展示经营趋势
**User value:** 了解经营状况，辅助决策
**Acceptance criteria:**
- [ ] 展示今日销售额
- [ ] 展示订单数量统计
- [ ] 支持按时间段筛选
- [ ] 图表可视化展示

### 管理员用户管理
**Priority:** Must Have
**Description:** 管理员查看用户列表，管理用户状态
**User value:** 维护平台秩序，处理违规用户
**Acceptance criteria:**
- [ ] 用户列表分页展示
- [ ] 支持搜索用户
- [ ] 支持禁用/启用用户
- [ ] 查看用户详细信息

### 管理员商家审核
**Priority:** Must Have
**Description:** 管理员审核商家入驻申请，管理商家状态
**User value:** 保障平台商家质量
**Acceptance criteria:**
- [ ] 待审核商家列表
- [ ] 查看商家资质信息
- [ ] 支持通过/拒绝审核
- [ ] 审核结果通知商家

### 数据统计分析
**Priority:** Should Have
**Description:** 管理员查看平台整体数据，包括用户量、订单量、交易额等
**User value:** 掌握平台运营情况
**Acceptance criteria:**
- [ ] 展示核心数据指标
- [ ] 支持数据导出
- [ ] 图表可视化展示
- [ ] 支持按时间筛选

## Scope

### In Scope (MVP)
- 学生端：菜品浏览、搜索、购物车、下单支付、订单跟踪、评价
- 商家端：菜品管理、订单处理、营收统计
- 管理员端：用户管理、商家审核、数据统计
- 三端权限控制与身份认证
- 多终端响应式适配

### Out of Scope
- 真实支付对接（支付宝/微信）
- 校园一卡通集成
- 智能取餐柜对接
- 骑手配送系统
- AI推荐算法
- 多语言支持

### Future Considerations
- 校园一卡通支付集成
- 智能取餐柜联动
- 优惠券/满减活动系统
- 骑手端APP
- 菜品智能推荐

## Assumptions & Risks

### Assumptions
- 用户已习惯线上点餐操作流程
- 校园网络环境稳定，支持系统访问
- 商家愿意配合使用系统管理订单
- 模拟支付可满足毕业设计演示需求

### Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| 高峰期并发压力 | High | Redis缓存热点数据，数据库读写分离 |
| 订单状态不一致 | High | 事务控制，状态机管理 |
| 商家入驻审核效率 | Medium | 简化审核流程，提供明确审核标准 |
| 用户数据安全 | High | 密码加密存储，敏感信息脱敏 |

## Technical Considerations

- **Platform:** Web端（PC + 移动端响应式）
- **Frontend:** Vue.js 3.x + Element Plus + Vite
- **Backend:** Spring Boot 2.7.x + MyBatis
- **Database:** MySQL 5.7+ (主数据存储) + Redis (缓存)
- **Authentication:** JWT Token
- **API Style:** RESTful
- **Constraints:** 
  - 页面响应 ≤ 2秒
  - 支持 50+ 并发
  - 兼容 Chrome/Firefox/Edge

## Open Questions

- 是否需要支持多校区独立运营？
- 商家入驻需要哪些资质材料？
- 订单超时自动取消时间设置为多久？
- 是否需要支持商家自营配送或第三方配送？

## Appendix

- 任务书：`/docs/任务书-李佳成.docx`
- 开题报告：`/docs/2261266226 李佳成 基于Spring Boot+Vue的校园点餐系统设计与实现.docx`
- 开发进度：2025.01.25 - 2025.05.27
