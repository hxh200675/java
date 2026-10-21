# 影院全栈项目 · 目录结构说明

> 一句话：**两个在用的主项目** —— 前端 `vue-cinema-login/`、后端 `springBootTest/`。
> 另有 `backend/`（独立旧后端）和 `legacy-static/`（静态原型页），都不参与当前运行。

## 一、前后端划分

| 端 | 目录 | 技术栈 | 端口 | 说明 |
|---|---|---|---|---|
| **前端** | `vue-cinema-login/` | Vue 3 + Vite 5 | **5173** | 登录/注册、首页影片、选座购票、个人中心 |
| **后端** | `springBootTest/` | Spring Boot 3.4.5 + MyBatis-Plus + MySQL | **8081** | 包名 `com.fenglin.springboottest` |

前端通过 Vite 代理把 `/api`、`/uploads` 转发到后端 8081。

## 二、完整目录树

```
java-main/
├── vue-cinema-login/              【前端】Vue3 + Vite
│   ├── src/                       源码（视图 / 路由 / api / 资源）
│   ├── index.html
│   ├── vite.config.js             含 /api、/uploads 代理到 8081
│   ├── package.json
│   └── node_modules/              依赖，可再生（npm install）
│
├── springBootTest/                【后端】Spring Boot
│   ├── src/main/java/com/fenglin/springboottest/
│   │   ├── controller/            Auth / Captcha / Me / Movie / Order
│   │   ├── service/               User / Captcha / Movie / Order
│   │   ├── mapper/                MyBatis-Plus Mapper
│   │   ├── entity/                User / Movie / MovieSession / Order
│   │   ├── config/                JwtInterceptor / WebMvcConfig / SchemaInitializer
│   │   └── util/                  JwtUtil
│   ├── src/main/resources/
│   │   ├── application.properties      默认 dev 环境
│   │   ├── application-dev.properties  8081 + MySQL cinema_db
│   │   └── schema.sql                  启动自动建表 + 种子数据（幂等）
│   ├── mvnw / mvnw.cmd           Maven Wrapper
│   ├── 数据库建表语句.sql          建表参考
│   ├── target/                   编译产物，可再生
│   └── log/                      运行日志
│
├── backend/                       【独立旧后端】com.cine.auth（另一套，非本前端使用）
│                                  ⚠️ AI_NOTE 明确标注「别删」
│
├── legacy-static/                 【静态原型页】index.html + style.css + script.js
│                                  独立单页，与 Vue 前端无关，双击 index.html 可预览
│
├── cinema_db.sql                  数据库脚本（数据库名 cinema_db）
├── AI_NOTE.md                     项目备忘（含启动踩坑记录、接口清单，重要）
├── README.md                      本文件
│
├── start-all.bat                  一键启动前后端 + 自动打开浏览器
├── start-backend.bat              仅启动后端（8081）
├── start-frontend.bat             仅启动前端（5173）
├── stop-project.bat               停止前后端
└── start-all.sh                   Git Bash 版启动脚本
```

## 三、怎么启动

**双击 `start-all.bat`** 即可：释放旧端口 → 两个窗口启动前后端 → 等待真正就绪 → 自动打开 `http://localhost:5173`。

也可以单独启动：`start-backend.bat` / `start-frontend.bat`；停止用 `stop-project.bat`。

## 四、注意事项

1. **`backend/` 不要删**：它是另一套独立后端（`com.cine.auth`），与 `springBootTest` 不是同一项目，本备忘已多次标注。
2. **`node_modules/`、`target/`**：纯构建产物，可随时删除重建（`npm install` / Maven 编译），但删除后需要联网恢复。
3. **`legacy-static/`**：早期静态原型，仅作参考，当前前端不引用。
4. 本项目**没有 git 版本控制**，删除文件不可回滚，操作前请确认。
5. 本地数据库：MySQL `root/123456`，库名 `cinema_db`，后端启动时自动创建表结构与种子数据。
