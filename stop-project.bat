@echo off
cd /d %~dp0
echo 正在停止影院项目 (8081 / 5173) ...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8081 " ^| findstr "LISTENING"') do (
  echo   结束 8081 进程 PID=%%a
  taskkill /f /pid %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":5173 " ^| findstr "LISTENING"') do (
  echo   结束 5173 进程 PID=%%a
  taskkill /f /pid %%a >nul 2>&1
)
echo 同时关闭启动窗口 ...
taskkill /f /fi "WINDOWTITLE eq cinema-backend" >nul 2>&1
taskkill /f /fi "WINDOWTITLE eq cinema-frontend" >nul 2>&1
echo 完成。如窗口未关闭，可手动关闭。
pause
