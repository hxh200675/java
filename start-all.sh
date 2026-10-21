#!/usr/bin/env bash
# start-all.sh - 在 Git Bash / AI 环境下用脚本一键拉起前后端
# 注意：Windows 资源管理器里请双击 start-all.bat（本脚本用于不支持 cmd.exe 的环境）
set -e

# 项目根目录（Windows 形式，供 Maven 启动器使用）
ROOT="$(cd "$(dirname "$0")" && pwd -W)"

# Maven 发行版缓存（与 start-backend.bat 对应的路径）
MVN="C:/Users/Administrator/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9"
JAVA_BIN="/c/Program Files/Zulu/zulu-21/bin/java"

echo "[start-all] ROOT = $ROOT"

# ---- 后端 :8081 ----
echo "[start-all] 启动后端 springBootTest (:8081)"
unset SERVER__PORT SERVER_PORT   # 清除会劫持端口的环境变量
if [ ! -x "$JAVA_BIN" ]; then
  echo "[start-all][错误] 未找到 JDK: $JAVA_BIN"
  exit 1
fi
(
  cd "$ROOT/springBootTest"
  exec "$JAVA_BIN" \
    -classpath "$MVN/boot/plexus-classworlds-2.8.0.jar" \
    "-Dclassworlds.conf=$MVN/bin/m2.conf" \
    "-Dmaven.home=$MVN" \
    "-Dmaven.multiModuleProjectDirectory=$ROOT/springBootTest" \
    org.codehaus.plexus.classworlds.launcher.Launcher \
    -Dspring-boot.run.jvmArguments=-Dserver.port=8081 spring-boot:run
) > "$ROOT/backend.log" 2>&1 &
BACK_PID=$!
echo "[start-all] 后端 PID=$BACK_PID  (日志: $ROOT/backend.log)"

# ---- 前端 :5173 ----
echo "[start-all] 启动前端 vue-cinema-login (:5173)"
cd "$ROOT/vue-cinema-login"
if ! command -v node >/dev/null 2>&1; then
  echo "[start-all][错误] 未找到 node"
  exit 1
fi
if [ ! -d node_modules ]; then
  echo "[start-all] 未检测到 node_modules，执行 npm install ..."
  npm install
fi
npm run dev > "$ROOT/frontend.log" 2>&1 &
FRONT_PID=$!
echo "[start-all] 前端 PID=$FRONT_PID  (日志: $ROOT/frontend.log)"

echo "[start-all] 前后端已启动。"
echo "[start-all] 后端 http://localhost:8081   前端 http://localhost:5173"
echo "[start-all] 按 Ctrl-C 或会话空闲后进程会被回收；Windows 下请用 start-all.bat 持久运行。"
wait
