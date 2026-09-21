# 电子衣橱系统（Electronic Wardrobe）

> 基于 **Spring Boot 2.7（Java 8）** 后端 + **Vue3 + Element Plus** 前端的电子衣橱管理系统，前后端分离架构。本仓库为毕业设计项目源码与论文支撑材料。

## 功能模块

- 用户与权限：注册 / 登录（JWT）/ 个人资料
- 多角色多衣柜：所有者 / 管理员 / 成员，数据按衣柜隔离、可切换
- 衣物管理：拍照 / 相册添加、分类筛选、关键词搜索、增删改查
- 手动搭配：选衣组合、明细查看、一键应用（联动穿着次数）
- 天气联动：自定义城市，真实天气 API（无 Key 自动降级本地模拟）+ 穿衣建议
- 数据分析：品类 / 季节 / 颜色分布、穿着 Top5、闲置率（ECharts 可视化）
- 场景提醒：闲置 / 天气 / 搭配 / 不均衡四类提醒，一键生成

## 目录结构

```
.
├── wardrobe/                 # 项目源码
│   ├── backend/              # Spring Boot 后端（Java 8）
│   ├── frontend/             # Vue3 + Element Plus 前端
│   ├── sql/                  # MySQL 建表脚本
│   └── 论文支撑材料.md         # 论文用：E-R 图 / 架构图 / 接口清单 / 章节目录
├── 电子衣橱系统-功能需求分析.md  # 功能需求分析（论文第 3/4 章素材）
└── overview.md               # 项目交付概览
```

## 快速开始

详见 [`wardrobe/README.md`](wardrobe/README.md)，包含数据库初始化、后端（`mvn spring-boot:run`）与前端（`npm install && npm run dev`）启动步骤。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | Vue 3 / Vite / Element Plus / Pinia / Axios / ECharts |
| 后端 | Spring Boot 2.7 / Spring Data JPA / Spring Security Crypto（BCrypt）/ jjwt |
| 数据库 | MySQL 8 |
| 部署 | 前后端分离，Nginx 反代（可选） |
