# 电子衣橱系统 — 全量源码交付概览

> 论文题目：《基于 Spring Boot 与 Vue3 的电子衣橱系统的设计与实现》
> 技术栈：Spring Boot 2.7 (Java 8) + JPA + JWT + MySQL / Vue3 + Vite + Element Plus + Pinia + ECharts

## 交付内容
工程根目录：`wardrobe/`
- `backend/`：Spring Boot 后端（Java 8 规范，无 var / switch 表达式）
- `frontend/`：Vue3 前端（已通过 `npm run build`）
- `sql/schema.sql`：MySQL 建表脚本（8 张表 + 外键/索引）
- `README.md`：库初始化与前后端运行说明

## 功能覆盖（对应需求）
1. 用户与权限：注册 / 登录（JWT）/ 个人资料 / 多角色(所有者·管理员·成员) / 多衣柜切换
2. 衣物管理：拍照 / 相册添加（camera capture + 上传）、分类筛选、关键词搜索、CRUD、标记穿着
3. 手动搭配：创建搭配、查看明细、应用（联动衣物穿着次数+1）
4. 天气联动：自定义城市、真实 OpenWeatherMap API（无 Key 自动降级本地模拟）、穿衣建议
5. 数据分析：品类/季节/颜色分布、穿着频率 Top5、闲置率（ECharts 可视化）
6. 场景提醒：闲置 / 天气 / 搭配 / 不均衡 四类，一键生成

## 后端结构
- 公共：`Result` 统一返回、`BusinessException`、`GlobalExceptionHandler`、`JwtUtil`、`JwtInterceptor`、`WebConfig`（静态资源+拦截器）、`CorsConfig`
- 实体/Repository：User、Wardrobe、WardrobeMember、Clothing、Outfit、OutfitItem、WeatherPreference、Reminder
- 服务/控制器：UserService/AuthController、WardrobeService、ClothingService、OutfitService、WeatherService、AnalyticsService、ReminderService、FileController、UserController

## 验证情况
- ✅ 前端：`npm install` + `npm run build` 通过（2233 模块，构建成功）
- ⚠️ 后端：本机无 JDK/Maven，未做编译；已人工自查并修正 2 处隐患
  - `AnalyticsService` 缺少 `java.util.function.Function` 导入
  - 前端 `clothingIds` 数组参数改为逗号字符串，避免 Spring `@RequestParam List<Long>` 收不到
- 建议：在本机 JDK8 + Maven 环境执行 `mvn clean package -DskipTests` 验证后端

## 运行速览
```bash
# 数据库
mysql -u root -p wardrobe < wardrobe/sql/schema.sql
# 后端
cd wardrobe/backend && mvn spring-boot:run        # 端口 8080
# 前端
cd wardrobe/frontend && npm install && npm run dev # 端口 5173
```
