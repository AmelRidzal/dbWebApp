@echo off
title dbWebApp - Uninstall

echo ==========================================
echo     dbWebApp - Uninstall
echo ==========================================
echo.
echo WARNING: This will stop and remove:
echo  - All running containers
echo  - All database data
echo  - Docker volumes
echo.
set /p confirm="Are you sure? This cannot be undone! (y/n): "

if /i not "%confirm%"=="y" (
    echo.
    echo Uninstall cancelled.
    pause
    exit /b 0
)

echo.
echo Stopping and removing containers...
docker-compose down -v

if errorlevel 1 (
    echo.
    echo [ERROR] Failed to remove containers.
    pause
    exit /b 1
)

echo.
echo [OK] All containers and volumes removed!

REM Remove auto-start
echo.
echo Removing auto-start configuration...
set "STARTUP_FOLDER=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup"
if exist "%STARTUP_FOLDER%\dbWebApp.lnk" (
    del "%STARTUP_FOLDER%\dbWebApp.lnk"
    echo [OK] Auto-start shortcut removed
)
if exist "start-dbwebapp.bat" (
    del "start-dbwebapp.bat"
    echo [OK] Startup script removed
)

echo.
echo Note: The .env file and source code remain.
echo Delete them manually if needed.
echo.
pause