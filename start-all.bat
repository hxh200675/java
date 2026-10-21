@echo off
setlocal
cd /d "%~dp0"
title cinema-launcher

echo ============================================
echo   影院全栈 一键启动  (前端 + 后端)
echo   后端 :8081    前端 :5173
echo ============================================
echo.

REM ---------- 0. 释放旧端口占用 ----------
echo [0/4] 释放旧端口占用 (8081 / 5173) ...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8081 " ^| findstr "LISTENING"') do taskkill /f /pid %%a >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":5173 " ^| findstr "LISTENING"') do taskkill /f /pid %%a >nul 2>&1

REM ---------- 1. 启动后端窗口 ----------
echo [1/4] 打开后端窗口 ...
start "cinema-backend" cmd /k call "%~dp0start-backend.bat"

REM ---------- 2. 启动前端窗口 ----------
echo [2/4] 打开前端窗口 ...
start "cinema-frontend" cmd /k call "%~dp0start-frontend.bat"

echo.
echo [3/4] 等待服务就绪后自动打开浏览器 ...
echo       首次启动需编译，约 10~60 秒，请勿关闭本窗口 ...
echo.

REM ---------- 探测工具检查 ----------
where curl.exe >nul 2>&1
if errorlevel 1 (
  echo [提示] 未检测到 curl.exe，无法自动探测，固定等待 45 秒后打开浏览器 ...
  timeout /t 45 /nobreak >nul
  goto open_page
)

REM ---------- 3a. 等待前端 5173 ----------
set /a TRY=0
:wait_front
set /a TRY+=1
if %TRY% gtr 120 goto front_timeout
curl.exe -s -o nul -m 2 --noproxy "*" "http://localhost:5173/"
if not errorlevel 1 goto front_ready
<nul set /p "=."
timeout /t 1 /nobreak >nul
goto wait_front
:front_ready
echo.
echo [OK] 前端已就绪  http://localhost:5173

REM ---------- 3b. 等待后端 8081 ----------
set /a TRY=0
:wait_back
set /a TRY+=1
if %TRY% gtr 150 goto back_timeout
curl.exe -s -o nul -m 2 --noproxy "*" "http://localhost:8081/api/auth/captcha"
if not errorlevel 1 goto back_ready
<nul set /p "=."
timeout /t 1 /nobreak >nul
goto wait_back
:back_ready
echo.
echo [OK] 后端已就绪  http://localhost:8081
goto open_page

REM ---------- 超时处理 ----------
:front_timeout
echo.
echo [警告] 等待前端超时，5173 无响应。
echo        请查看 "cinema-frontend" 窗口里的报错信息。
echo.
pause
goto finish

:back_timeout
echo.
echo [警告] 后端 150 秒仍未就绪，登录验证码可能暂不可用。
echo        请查看 "cinema-backend" 窗口；仍为你打开前端页面。
echo.
goto open_page

REM ---------- 4. 打开浏览器 ----------
:open_page
echo.
echo [4/4] 正在打开浏览器： http://localhost:5173
start "" "http://localhost:5173"
echo.
echo 启动完成。本窗口 8 秒后自动关闭；停止项目请双击 stop-project.bat
timeout /t 8 /nobreak >nul

:finish
exit
