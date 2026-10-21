# AI 项目备忘（机器重置后，请先读我，避免重复思考）

> 本文件写给 AI 助手自己看。下次会话开始先读它，再决定做什么。
> 最后更新：2026-10-16（会话：新增个人中心——昵称修改 + 头像上传；后端 user 加 avatar 列 + /uploads 静态映射 + 前端弹窗）

## 项目启动验证（2026-10-16 实测通过）
- 后端 8081 / 前端 5173 均已启动；`cinema_db` 与 `user` 表自动创建成功。
- 注意：`.m2/wrapper/dists/` 下的 hash 目录（如 `4nf9hui3q3djbarqar9g711ggc`）系统重置后可能变化，启动前先 `ls ~/.m2/wrapper/dists/` 确认实际路径。
- 本机 `SERVER__PORT` 环境变量每次开机值会变（曾见 63538 / 53121），启动前务必 unset。

## 项目概况
- 名称：影院登录 / 注册全栈（Vue3 + Spring Boot + MySQL）
- 仓库根目录：`java-main/`（本文件所在目录），结构已清理干净（2026-10-14）：
  - 后端：`springBootTest/`（Spring Boot，端口 8081，包名 `com.fenglin.springboottest`）
  - 前端：`vue-cinema-login/`（Vue3 + Vite，端口 5173，18 个源文件，最完整版本）
  - `backend/`：**另一个独立后端**（com.cine.auth，非重复项目，别混淆、别删）
  - 根目录 `index.html/script.js/style.css`：独立静态页，与 vue 前端无关（2026-10-21 已移入 `legacy-static/`，双击其中 index.html 仍可用）
  - `README.md`：面向人的目录结构说明（前后端划分、启动方式、注意事项），2026-10-21 新增
- ⚠️ 已清理：曾经的嵌套副本 `java-main/java-main/`（41MB/961 文件）已于 2026-10-14 删除，**不要再去嵌套路径找东西**。曾经存在 `java-main.zip`（桌面）为早期归档。
- 重置规律：每次开机 `node_modules/` 与 `target/` 会被清空；但 `.mvn/`、`pom.xml`、`src/**`、`vue 源码`、`mvnw.cmd`、本备忘、GBK 脚本都保留。
- 本地账号：MySQL `root / 123456`；后端库 `cinema_db` 会自动创建。
- 环境实测：JDK 在 `C:\Program Files\Zulu\zulu-21`（`java` 已在 PATH）；Node v22 / npm 10 在 PATH；MySQL 在 3306 运行；Maven 3.9.9 发行版缓存于 `~/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/`。

## 一键启动（GBK 编码 .bat，双击即可）
- `start-all.bat`      一键启动前后端（各开一个 cmd 窗口）
- `start-backend.bat`  后端：查 MySQL → 定位 JDK → 释放 8081 → 清环境变量 → mvnw 启动
- `start-frontend.bat` 前端：node_modules 缺失先 `npm install` 再 `vite dev`
- `stop-project.bat`   按端口 8081/5173 + 窗口标题停止
- 全部用 `%~dp0` 相对定位（脚本在哪个目录都能跑），不依赖 `node_modules`/`target`。

## 后端启动三大坑（已踩过，勿重复排查）
1. **端口劫持**：本机环境变量 `SERVER__PORT=63538` 会被 Spring Boot 宽松绑定当成 `server.port`，导致不在 8081 启动。启动前必须 `set SERVER__PORT=` / `unset SERVER__PORT SERVER_PORT`。
2. **fork JVM 端口传参**：`spring-boot:run` 默认 fork 独立 JVM，直接 `-Dserver.port=8081` **传不进去**。必须用：
   `mvnw.cmd -Dspring-boot.run.jvmArguments=-Dserver.port=8081 spring-boot:run`
3. **Git Bash 无法运行 mvnw**：MinGW 路径解析失败报 `找不到主类 classworlds.launcher.Launcher`。AI 在 Bash 工具里启动后端要用 java 直接调 Maven 启动器，且**必须传 Windows 路径（C:/... 而非 /c/...）**：
   ```
   "$JAVA_HOME/bin/java" \
     -classpath "C:/Users/Administrator/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/boot/plexus-classworlds-2.8.0.jar" \
     "-Dclassworlds.conf=C:/Users/Administrator/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/bin/m2.conf" \
     "-Dmaven.home=C:/Users/Administrator/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9" \
     "-Dmaven.multiModuleProjectDirectory=C:/Users/Administrator/Desktop/java-main/springBootTest" \
     org.codehaus.plexus.classworlds.launcher.Launcher \
     -Dspring-boot.run.jvmArguments=-Dserver.port=8081 spring-boot:run
   ```
4. **沙箱限制**：AI 的 Bash 工具里 `cmd.exe` 被安全策略拦截；PowerShell 的 `Add-Type` 与 COM 对象也被拦截。后台任务用工具自带 `run_in_background`，不要用 `nohup ... &`（会被回收）。
5. **AI 沙箱进程回收（2026-10-16 实测，最重要）**：AI 工具启动的所有进程（后台任务、分离进程、fork 的 JVM）都会在**会话空闲约 1~2 分钟后被整体回收**——后端也不例外（曾误判为前端 stdin 问题，实为全局回收）。AI 无法让服务长期存活：
   - `schtasks.exe` 在程序黑名单，`cmd.exe`、`powershell.exe` 从 Bash 调用也被拦截，PowerShell 工具无法启动外部 exe；
   - **AI 会话内**：直接用 `run_in_background` 拉起前后端即可（够当轮测试用，回合结束约 2 分钟后会被回收）；
   - **长期运行**：让用户双击 `start-all.bat`（这是唯一可靠方式）。若用户把 `schtasks.exe` 移出黑名单，可改用计划任务方式拉起。

## 写 GBK 脚本的坑（AI 自用）
- Git Bash 的 `iconv -f UTF-8 -t GBK` 会中文转换失败，且 `iconv | sed > file && mv` 管道会**掩盖失败、产出截断文件**。改用 Python：读 UTF-8 → `text.encode("gbk")` 写 bytes，并回读 assert 校验。

## 后端要点
1. profile：`application.properties` 设 `spring.profiles.active=dev`；**dev 配置是干净的**（8081 + MySQL）。`application-prob/test.properties` 里有损坏的合并行（`server.port=8082server.port=8080...`），但未激活，不影响。
2. 数据库：`jdbc:mysql://localhost:3306/cinema_db?createDatabaseIfNotExist=true`；启动自动建库建表（`schema.sql` 幂等）。
3. 日志：`logging.file.name=log/xx.log`（工程下 `log/` 目录）。

## 已实现功能
- 用户名 + 图形验证码 注册 / 登录（实体 `@TableField("account")` 兼容旧表列）
- 注册校验：`^[a-zA-Z][a-zA-Z0-9_]{3,19}$`
- JWT（`JwtUtil` / `JwtInterceptor`），`/api/user/me` 需鉴权
- 前端：登录/注册页（含验证码）、Home 昵称 + 退出、路由守卫
- Vite 代理 `/api` -> `http://localhost:8081`
- **首页展示**：影片列表（分类筛选 / 排序 hot|new|all）+ 评分 Top3 轮播（后端驱动，前端 HomeView 已从硬编码假数据改为调用后端）
- **搜索**：按 片名/类型/分类/地区 模糊搜索（`keyword` 参数）
- **购票**：场次列表 + 选座下单（扣减余票，事务）+ 我的订单（均需登录）
- 海报图片：前端 `src/assets/posters/movie-{id}.png`（AI 生成），按影片 id 映射
- **个人中心**：昵称修改（`PUT /api/user/profile`）+ 头像上传（`POST /api/user/avatar`，multipart，存 `uploads/`，返回 `/uploads/avatar_{id}_{ts}.{ext}`，经 `/uploads/**` 静态映射对外）
  - 老库 `user` 表缺 `avatar` 列：`CREATE TABLE IF NOT EXISTS` 不会补列，故新增 `config/SchemaInitializer.java`（`ApplicationRunner` 查 `information_schema.COLUMNS`，缺列则幂等 `ALTER TABLE user ADD COLUMN avatar ...`）
  - 头像限制：仅 `image/*`、≤5MB（`spring.servlet.multipart.max-file-size=5MB`）、扩展名白名单 png/jpg/jpeg/gif/webp/bmp
  - 前端：`HomeView.vue` 顶栏「个人中心」按钮 + 头像可点击 → 弹窗（用户名只读 / 昵称输入保存 / 更换头像）；`api/auth.js` 暴露 `updateProfile`/`uploadAvatar`，`request()` 已适配 FormData（不写 JSON Content-Type）
  - Vite 已新增 `/uploads` 代理到 8081（前端才能显示头像）

## 关键接口
- `GET  /api/auth/captcha` -> `{captchaKey, image(base64)}`
- `POST /api/auth/register` -> `{username,password,confirm,captchaKey,captchaCode}`
- `POST /api/auth/login`    -> 同上 -> `{token,user:{...}}`
- `GET  /api/user/me`（Bearer token）-> 用户信息（含 `avatar`）
- `PUT  /api/user/profile`（Bearer token）-> body `{nickname}` -> 修改昵称
- `POST /api/user/avatar`（Bearer token, multipart 字段 `file`）-> 上传头像 -> `{avatar, user}`
- `GET  /uploads/**`（静态，匿名）-> 上传的图片（如头像）
- `GET  /api/movie/list?category=&keyword=&tab=hot|new|all`（匿名）-> 影片列表
- `GET  /api/movie/banners`（匿名）-> 评分 Top3
- `GET  /api/movie/{id}`（匿名）-> 详情
- `GET  /api/movie/{id}/sessions`（匿名）-> 场次列表
- `POST /api/order/create`（Bearer token）-> body `{sessionId, seats:[...]}` -> 订单
- `GET  /api/order/my`（Bearer token）-> 我的订单

## 数据库表（`schema.sql` 启动自动建，`CREATE TABLE IF NOT EXISTS` + `INSERT IGNORE` 幂等）
- `user`：用户（原有；2026-10-16 新增 `avatar VARCHAR(255)` 列，由 `SchemaInitializer` 幂等迁移补列）
- `movie`：影片（id,title,category,genre,score,year,region,duration,description,release_date），初始 12 条
- `movie_session`：放映场次（movie_id,cinema,hall,show_time,price,total_seats,available_seats），初始 10 条
- `orders`：订单（order_no,username,movie_id,movie_title,session_id,cinema,hall,show_time,seats,seat_count,amount,status）
- 新增文件：`entity/{Movie,MovieSession,Order}.java`、`mapper/{MovieMapper,MovieSessionMapper,OrderMapper}.java`、`dto/CreateOrderRequest.java`、`service/{MovieService,OrderService}(+impl)`、`controller/{MovieController,OrderController}.java`
- `WebMvcConfig` 已放行 `/api/movie/**`（匿名可访问），`/api/order/**` 仍需登录

## 明显缺失 / 可下一步（按优先级）
1. ~~注册后自动登录~~（**已实现**：2026-10-16 全链路实测，`register` 直接返回 `{token, user}`）
2. 忘记密码（前端有占位，无后端逻辑）
3. ~~影院核心业务：影片列表、选座下单、订单~~（**已实现** 2026-10-16：movie/movie_session/orders 三表 + 首页展示/搜索/购票接口 + 前端对接）
4. ~~影片 / 订单等表与 CRUD~~（**已实现**；影片为启动种子数据，暂未做后台增删改接口）
5. ~~头像上传、昵称修改~~（**已实现** 2026-10-16：`PUT /api/user/profile` + `POST /api/user/avatar` + 前端「个人中心」弹窗）
6. 座位级占用（当前仅按 available_seats 扣减，未记录具体座位是否已被选）
7. 单元测试 / 接口测试
8. 部署：Dockerfile / docker-compose（可选）
9. 根目录统一 `.gitignore`（前后端各自已有）

## 快速自测
- 后端起来：`curl --noproxy "*" localhost:8081/api/auth/captcha`
- 前端起来：浏览器访问 `http://localhost:5173`
- 端口确认：`netstat -ano | findstr "8081"`（或 `":8081" | grep LISTENING`）
- 全链路：验证码 → 注册 → 登录 → 带 token 查 `/api/user/me`
