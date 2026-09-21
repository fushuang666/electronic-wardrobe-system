# 电子衣橱系统（基于 Spring Boot + Vue3）

毕业设计 / 课程设计示例项目：个人与家庭服饰数字化管理。

## 一、技术栈
- 前端：Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios + ECharts
- 后端：Spring Boot 2.7（Java 8）+ Spring Data JPA + JWT（jjwt）+ BCrypt
- 数据库：MySQL 5.7+ / 8.0
- 天气：OpenWeatherMap（未配置 Key 时自动使用本地模拟数据）

## 二、目录结构
```
wardrobe/
├── backend/        # Spring Boot 后端（Java 8）
│   ├── pom.xml
│   ├── sql/schema.sql          # 数据库建表脚本
│   └── src/main/...
├── frontend/       # Vue3 前端
└── README.md
```

## 三、数据库初始化
```bash
mysql -u root -p
# 在 mysql 中执行：
source wardrobe/sql/schema.sql
# 或
mysql -u root -p wardrobe < wardrobe/sql/schema.sql
```
默认库名 `wardrobe`，账号 `root` / 密码 `root`（与后端 application.yml 一致，按实际修改）。

## 四、后端运行（需要 JDK 8 + Maven 3.6+）
```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库连接与上传路径
mvn clean package -DskipTests
java -jar target/wardrobe-backend-1.0.0.jar
# 或开发模式：mvn spring-boot:run
```
服务默认端口 8080。首次启动后可调用 `/api/auth/register` 注册账号。

关键配置（application.yml）：
- `spring.datasource`：MySQL 连接
- `wardrobe.upload.path`：图片上传保存目录（建议绝对路径）
- `wardrobe.jwt.secret` / `expiration`：令牌密钥与有效期
- `wardrobe.weather.api-key`：OpenWeatherMap Key（留空则用模拟数据）

## 五、前端运行（需要 Node.js 16+）
```bash
cd frontend
npm install
npm run dev        # 开发服务器，默认 http://localhost:5173
# 生产构建：
npm run build      # 产物在 dist/，可交由 Nginx 托管
```
前端通过 Vite 代理把 `/api` 转发到 `http://localhost:8080`，无需跨域配置。

## 六、功能清单
1. 用户与权限：注册 / 登录（JWT）/ 个人资料 / 多角色（所有者·管理员·成员）/ 多衣柜切换
2. 衣物管理：拍照 / 相册添加、分类筛选、关键词搜索、CRUD
3. 手动搭配：创建搭配、查看明细、应用记录穿着
4. 天气联动：自定义城市、实时天气与穿衣建议（真实 API 或模拟）
5. 数据分析：品类 / 季节 / 颜色分布、穿着频率 Top5、闲置率（ECharts）
6. 场景提醒：闲置 / 天气 / 搭配 / 不均衡四类提醒，一键生成

## 七、接口约定
- 统一返回：`{ code: 200, message: "success", data: ... }`
- 鉴权：请求头 `Authorization: Bearer <token>`
- 除 `/api/auth/**`、`/api/files/**` 外所有接口需登录

## 八、说明
- 本项目为论文 / 演示用途，已按 Java 8 规范编写（不使用 var、switch 表达式等 11+ 特性）。
- 文件上传默认存本地磁盘并通过静态资源映射访问，生产环境建议替换为对象存储。
