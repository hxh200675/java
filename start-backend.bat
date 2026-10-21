@echo off
cd /d %~dp0
title cinema-backend
echo ============================================
echo  影院后端启动 (Spring Boot :8081)
echo ============================================

REM 1. 定位 JDK
if not defined JAVA_HOME (
  set "JAVA_HOME=C:\Program Files\Zulu\zulu-21"
)
echo [1/4] JAVA_HOME = %JAVA_HOME%
if not exist "%JAVA_HOME%\bin\java.exe" (
  echo [错误] 未找到 JDK，请安装 Zulu 21 或设置 JAVA_HOME 后重试。
  pause
  exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM 2. 检查 MySQL (3306)
echo [2/4] 检查 MySQL (3306) ...
netstat -ano 2>nul | findstr ":3306" >nul
if errorlevel 1 (
  echo [警告] 未检测到 MySQL 监听 3306，后端可能无法连接数据库。
  echo         请先启动本地 MySQL （root/123456）。
) else (
  echo [OK] MySQL 3306 已监听
)

REM 3. 释放 8081 端口
echo [3/4] 检查 8081 端口占用 ...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8081 " ^| findstr "LISTENING"') do (
  echo   结束占用 8081 的进程 PID=%%a
  taskkill /f /pid %%a >nul 2>&1
)

REM 4. 清除会劫持端口的环境变量
echo [4/4] 清除 SERVER__PORT / SERVER_PORT 环境变量 ...
set "SERVER__PORT="
set "SERVER_PORT="

cd /d "%~dp0springBootTest"
echo.
echo 正在启动后端 (mvnw spring-boot:run, 端口 8081) ...
echo 首次编译较慢，请耐心等待 Tomcat started on port 8081。
echo.
call mvnw.cmd -Dspring-boot.run.jvmArguments=-Dserver.port=8081 spring-boot:run
