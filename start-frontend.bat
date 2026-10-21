@echo off
cd /d %~dp0
title cinema-frontend
echo ============================================
echo  影院前端启动 (Vue3 + Vite :5173)
echo ============================================

cd /d "%~dp0vue-cinema-login"

REM 检查 node
where node >nul 2>&1
if errorlevel 1 (
  echo [错误] 未找到 node，请安装 Node.js v22 / npm 10。
  pause
  exit /b 1
)
echo [1/2] node 路径：
where node

REM node_modules 缺失则安装
if not exist "node_modules" (
  echo [2/2] 未检测到 node_modules，执行 npm install ...
  call npm install
  if errorlevel 1 (
    echo [错误] npm install 失败，请检查网络或 npm 配置。
    pause
    exit /b 1
  )
) else (
  echo [2/2] node_modules 已存在，跳过安装。
)

echo.
echo 正在启动前端 (vite dev, 端口 5173) ...
echo 启动后请在浏览器访问 http://localhost:5173
echo.
call npm run dev
