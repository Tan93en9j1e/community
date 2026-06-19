# 🏘️ Community — 社区论坛系统

一个基于 **Spring Boot 4 + Java 21** 构建的现代化社区论坛系统，集成了全站搜索、实时消息通知、权限管理、数据统计等完整功能。

## 📋 目录

- [技术栈](#-技术栈)
- [功能特性](#-功能特性)
- [系统架构](#-系统架构)
- [快速开始](#-快速开始)
- [配置文件说明](#-配置文件说明)
- [项目结构](#-项目结构)
- [API 概览](#-api-概览)

## 🛠 技术栈

| 类别           | 技术                                                                         |
| -------------- | ---------------------------------------------------------------------------- |
| **核心框架**   | Spring Boot 4.0.6 · Spring MVC · Thymeleaf                                   |
| **语言**       | Java 21                                                                      |
| **ORM / 数据库** | MyBatis + MySQL · HikariCP 连接池                                           |
| **缓存**       | Redis (数据缓存/点赞/关注) · Caffeine (本地热帖缓存)                         |
| **搜索**       | Elasticsearch (全文检索)                                                     |
| **消息队列**   | Apache Kafka (异步事件驱动)                                                  |
| **安全**       | Spring Security · 验证码 (Kaptcha) · 敏感词过滤                              |
| **定时任务**   | Quartz (帖子热分刷新)                                                        |
| **邮件**       | Spring Mail (QQ 邮箱 — 注册激活/密码找回)                                    |
| **文件存储**   | 七牛云 OSS (头像/分享图)                                                     |
| **AOP**        | Spring AOP (日志记录/权限检查)                                               |
| **监控**       | Spring Boot Actuator                                                         |
| **运维**       | Logback (分级日志) · DevTools (热重载)                                       |

## ✨ 功能特性

### 🧑‍💻 用户系统
- **注册与登录** — 邮箱注册 + 邮件激活，支持验证码 (Kaptcha)
- **登录凭证** — 支持「记住我」功能，登录态自动续期
- **密码找回** — 通过邮件发送验证码重置密码
- **个人主页** — 头像上传（七牛云）、个人资料编辑
- **权限模型** — 三级角色：`user` / `moderator` / `admin`

### 📝 帖子与评论
- **帖子管理** — 发布、查看讨论帖，分页浏览（首页 + 最新/热门排序）
- **评论系统** — 支持对帖子及评论的嵌套评论
- **置顶/加精/删除** — 管理员与版主特权操作

### ❤️ 互动
- **点赞** — Redis 实时点赞，异步通知作者
- **关注** — 用户互关，查看关注者/被关注列表
- **私信** — 站内信 + 系统通知（评论/点赞/关注消息）

### 🔎 全文搜索
- 基于 Elasticsearch 的帖子与评论全文检索
- 搜索结果高亮显示

### ⚙️ 后台与运维
- **数据统计** — UV / DAU 独立访客与日活统计
- **热帖排行** — Quartz 定时任务刷新帖子热度分（基于点赞/评论/时间衰减）
- **运营监控** — Spring Boot Actuator 暴露运行时指标

### 🔒 安全与风控
- **敏感词过滤** — 基于前缀树的 DFA 算法，毫秒级过滤
- **接口鉴权** — `@LoginRequired` 自定义注解 + 拦截器
- **CSRF / XSS** — Spring Security 默认防护

### 🚀 性能优化
- **多级缓存** — Caffeine 本地缓存高频热帖 + Redis 分布式缓存
- **异步解耦** — Kafka 处理评论/点赞/关注等事件推送
- **连接池** — HikariCP 高性能连接池

### 🖼 扩展功能
- **分享图生成** — wkhtmltoimage 将帖子渲染为分享图片
- **长连接管理** — WebSocket 就绪的模块结构

## 🏗 系统架构

```
┌─────────────┐    ┌──────────────┐    ┌──────────────┐
│   Browser   │    │  Thymeleaf   │    │   REST API   │
└──────┬──────┘    └──────┬───────┘    └──────┬───────┘
       │                  │                    │
┌──────▼──────────────────▼────────────────────▼──────────┐
│                   Spring MVC Controller                  │
├──────────┬──────────┬──────────┬──────────┬─────────────┤
│  AOP切面  │ 拦截器链  │ Security │ 自定义注解 │  事件发布    │
└──────────┴────┬─────┴──────────┴──────────┴──────┬──────┘
                │                                   │
       ┌────────▼────────┐              ┌──────────▼────────┐
       │   Service 层    │────Kafka────▶│   EventConsumer   │
       └────┬───────┬────┘              │(评论/点赞/关注通知)│
            │       │                   └───────────────────┘
     ┌──────▼───────▼──────┐
     │      DAO 层         │
     │ (MyBatis + ES)      │
     └──┬───────┬──────┬───┘
        │       │      │
   ┌────▼───┐ ┌─▼────┐ ┌▼──────────┐
   │ MySQL  │ │Redis │ │Elasticsearch│
   └────────┘ └──────┘ └────────────┘
```

## 🚀 快速开始

### 前置条件

| 依赖         | 版本要求   | 用途               |
| ------------ | ---------- | ------------------ |
| JDK          | ≥ 21       | 运行环境           |
| MySQL        | ≥ 8.0      | 关系数据库         |
| Redis        | ≥ 6.x      | 缓存/计数          |
| Kafka        | ≥ 3.x      | 异步事件           |
| Elasticsearch | ≥ 8.x    | 全文搜索           |
| Maven        | ≥ 3.9      | 构建工具           |
| wkhtmltoimage | —         | 分享图生成（可选） |

### 1️⃣ 克隆并配置

```bash
git clone <repo-url>
cd community
```

### 2️⃣ 初始化数据库

创建 MySQL 数据库 `community`，然后执行 `src/main/resources/` 下的初始化脚本（如存在 `schema.sql` / `data.sql`），或让 JPA / MyBatis 自动建表。

### 3️⃣ 修改配置

编辑 `src/main/resources/application.properties`，根据本地环境修改：

```properties
# 数据库
spring.datasource.url=jdbc:mysql://localhost:3306/community?...
spring.datasource.username=root
spring.datasource.password=your-password

# Redis
spring.data.redis.host=localhost

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Elasticsearch
spring.elasticsearch.uris=http://localhost:9200

# 七牛云（头像/分享图存储）
qiniu.key.access=your-access-key
qiniu.key.secret=your-secret-key

# 邮箱（注册激活/找回密码）
spring.mail.host=smtp.qq.com
spring.mail.username=your-email@qq.com
spring.mail.password=your-auth-code

# wkhtmltoimage（分享图生成，可选）
wk.image.command=/path/to/wkhtmltoimage
```

### 4️⃣ 编译运行

```bash
# 编译
./mvnw clean package -DskipTests

# 运行
java -jar target/community-0.0.1-SNAPSHOT.jar

# 或使用 Maven 插件直接启动
./mvnw spring-boot:run
```

### 5️⃣ 访问

打开浏览器访问：**[http://localhost:8080/community](http://localhost:8080/community)**

## 📁 项目结构

```
src/main/java/com/tang/community/
├── CommunityApplication.java      # 应用入口
├── aspect/                        # AOP 切面（日志等）
├── annotation/                    # 自定义注解（@LoginRequired）
├── config/                        # 配置类
│   ├── SecurityConfig.java        # Spring Security 配置
│   ├── RedisConfig.java           # Redis 配置
│   ├── KaptchaConfig.java         # 验证码配置
│   ├── QuartzConfig.java          # 定时任务配置
│   └── WetMvcConfig.java          # MVC 拦截器注册
├── controller/                    # 控制器层
│   ├── HomeController.java        # 首页
│   ├── LoginController.java       # 登录/注册/激活/找回密码
│   ├── DiscussPostController.java # 帖子
│   ├── CommentController.java     # 评论
│   ├── LikeController.java        # 点赞
│   ├── FollowController.java      # 关注
│   ├── MessageController.java     # 私信/通知
│   ├── UserController.java        # 个人主页/设置
│   ├── SearchController.java      # 全文搜索
│   ├── DataController.java        # 数据统计
│   ├── ShareController.java       # 分享图
│   └── controller/interceptor/    # 拦截器
│   └── controller/advice/         # 全局异常处理
├── service/                       # 业务逻辑层
├── dao/                           # 数据访问层 (MyBatis Mapper)
│   └── elasticsearch/             # ES Repository
├── entity/                        # 领域模型
│   ├── User.java                  # 用户
│   ├── DiscussPost.java           # 帖子
│   ├── Comment.java               # 评论
│   ├── Message.java               # 消息
│   ├── LoginTicket.java           # 登录凭证
│   └── Page.java                  # 分页模型
├── event/                         # Kafka 事件驱动
│   ├── EventProducer.java         # 事件生产者
│   └── EventConsumer.java         # 事件消费者
├── quartz/                        # Quartz 定时任务
│   └── PostScoreRefreshJob.java   # 帖子热分刷新
├── task/                          # Spring @Scheduled 任务
├── util/                          # 工具类
│   ├── CommunityConstant.java     # 系统常量
│   ├── SensitiveFilter.java       # 敏感词过滤器 (DFA)
│   ├── RedisKeyUtil.java          # Redis Key 工具
│   ├── HostHolder.java            # 请求线程持有者
│   ├── MailClient.java            # 邮件客户端
│   └── CookieUtil.java            # Cookie 工具
└── actuator/                      # Actuator 扩展端点

src/main/resources/
├── application.properties         # 主配置文件
├── logback-spring.xml             # 日志配置
├── sensitive-words.txt            # 敏感词库
├── mapper/                        # MyBatis XML Mapper
├── static/                        # 静态资源 (CSS/JS/IMG)
│   ├── css/                       # 样式
│   ├── js/                        # 脚本
│   └── img/                       # 图片
└── templates/                     # Thymeleaf 页面模板
    ├── index.html                 # 首页
    ├── site/                      # 业务页面
    │   ├── login.html / register.html / forget.html
    │   ├── discuss-detail.html    # 帖子详情
    │   ├── profile.html / setting.html
    │   ├── letter.html / letter-detail.html
    │   ├── notice.html / notice-detail.html
    │   ├── followee.html / follower.html
    │   ├── search.html
    │   └── admin/data.html        # 数据统计
    ├── mail/                      # 邮件模板
    └── error/                     # 错误页面
```

## 🌐 API 概览

| 功能         | 路径                                     | 方法   |
| ------------ | ---------------------------------------- | ------ |
| 首页         | `/community/index`                       | GET    |
| 登录         | `/community/login`                       | GET/POST |
| 注册         | `/community/register`                    | GET/POST |
| 激活         | `/community/activation/{id}/{code}`      | GET    |
| 密码找回     | `/community/forget`                      | GET/POST |
| 帖子详情     | `/community/discuss/detail/{id}`         | GET    |
| 发布帖子     | `/community/discuss/add`                 | POST   |
| 评论         | `/community/comment/add/{postId}`        | POST   |
| 点赞         | `/community/like`                        | POST   |
| 关注         | `/community/follow`                      | POST   |
| 私信列表     | `/community/letter/list`                 | GET    |
| 系统通知     | `/community/notice/list`                 | GET    |
| 搜索         | `/community/search`                      | GET    |
| 数据统计     | `/community/data/uv` / `data/dau`        | GET    |

> 完整 API 见各 Controller 文件中的 `@RequestMapping` 定义。

## 📄 许可

[MIT](LICENSE)

---

*Built with Spring Boot 4 + Java 21 · 2025*
