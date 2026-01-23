@echo off
setlocal enabledelayedexpansion
title dbWebApp - Automated Setup

echo ==========================================
echo     dbWebApp - Automated Setup
echo ==========================================
echo.

REM Check if Docker is installed
echo [1/6] Checking Docker installation...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Docker is not installed!
    echo.
    set /p install_docker="Do you want to download and install Docker Desktop? (y/n): "
    if /i "!install_docker!"=="y" (
        echo.
        echo Downloading Docker Desktop...
        echo This may take several minutes depending on your internet speed...
        
        REM Download Docker Desktop installer
        powershell -Command "& {Invoke-WebRequest -Uri 'https://desktop.docker.com/win/main/amd64/Docker%%20Desktop%%20Installer.exe' -OutFile 'DockerDesktopInstaller.exe'}"
        
        if errorlevel 1 (
            echo [ERROR] Download failed!
            echo Please download manually from: https://www.docker.com/products/docker-desktop
            pause
            exit /b 1
        )
        
        echo.
        echo [OK] Download complete!
        echo.
        echo Starting Docker Desktop installation...
        echo Please follow the installation wizard.
        echo.
        start /wait DockerDesktopInstaller.exe install
        
        echo.
        echo [OK] Docker Desktop installed!
        echo.
        echo IMPORTANT: Docker Desktop needs to start for the first time.
        echo Please:
        echo   1. Open Docker Desktop from Start Menu
        echo   2. Accept the terms of service
        echo   3. Wait until it says "Docker Desktop is running"
        echo   4. Then run this setup script again
        echo.
        pause
        exit /b 0
    ) else (
        echo.
        echo Please install Docker Desktop from https://www.docker.com/products/docker-desktop
        echo Then run this script again.
        pause
        exit /b 1
    )
)

echo [OK] Docker is installed
echo.

REM Check if Docker is running
echo [2/6] Checking if Docker is running...
docker info >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Docker is not running!
    echo.
    echo Attempting to start Docker Desktop...
    
    REM Try to start Docker Desktop
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    
    echo Waiting for Docker to start...
    timeout /t 5 /nobreak >nul
    
    REM Wait for Docker to be ready (max 60 seconds)
    set countdown=60
    :wait_docker
    docker info >nul 2>&1
    if not errorlevel 1 goto docker_ready
    
    set /a countdown-=1
    if !countdown! leq 0 (
        echo.
        echo [ERROR] Docker did not start in time.
        echo Please start Docker Desktop manually and run this script again.
        pause
        exit /b 1
    )
    
    timeout /t 1 /nobreak >nul
    goto wait_docker
    
    :docker_ready
    echo [OK] Docker is now running!
    echo.
)

echo [OK] Docker is running
echo.

REM Check if .env exists
echo [3/6] Checking configuration...
if exist .env (
    echo [WARNING] .env file already exists.
    set /p overwrite="Do you want to overwrite it? (y/n): "
    if /i not "!overwrite!"=="y" (
        echo Using existing .env file...
        goto skip_env
    )
)

REM Create .env file interactively
echo Creating .env configuration file...
echo.
echo --- Database Configuration ---
echo.

set /p primary_password="Enter primary database password [default: admin]: "
if "!primary_password!"=="" set primary_password=admin

echo.
set /p use_backup="Do you want to configure backup database (Neon)? (y/n) [default: n]: "
if /i "!use_backup!"=="y" (
    echo.
    set /p secondary_url="Enter Neon JDBC URL: "
    set /p secondary_user="Enter Neon username: "
    set /p secondary_password="Enter Neon password: "
) else (
    set secondary_url=
    set secondary_user=
    set secondary_password=
)

REM Write .env file
(
    echo # dbWebApp Configuration
    echo # Generated on %date% at %time%
    echo.
    echo # Local PostgreSQL password
    echo PRIMARY_DB_PASSWORD=!primary_password!
    echo.
    echo # Neon backup database credentials
    echo SECONDARY_DB_URL=!secondary_url!
    echo SECONDARY_DB_USER=!secondary_user!
    echo SECONDARY_DB_PASSWORD=!secondary_password!
) > .env

echo.
echo [OK] Configuration saved to .env
echo.

:skip_env

REM Check if docker-compose.yml exists
echo [4/6] Checking project files...
if not exist docker-compose.yml (
    echo [ERROR] docker-compose.yml not found!
    echo Make sure you're running this script in the project directory.
    pause
    exit /b 1
)
if not exist Dockerfile (
    echo [ERROR] Dockerfile not found!
    echo Make sure you're running this script in the project directory.
    pause
    exit /b 1
)
echo [OK] Project files found
echo.

REM Stop any running containers
echo [5/6] Stopping existing containers...
docker-compose down >nul 2>&1
echo [OK] Cleanup complete
echo.

REM Build and start
echo [6/6] Building and starting application...
echo This may take a few minutes on first run...
echo.
docker-compose up --build -d

if errorlevel 1 (
    echo.
    echo [ERROR] Failed to start application!
    echo Check the error messages above.
    pause
    exit /b 1
)

echo.
echo ==========================================
echo     Setup Complete!
echo ==========================================
echo.
echo Your application is now running!
echo.
echo Access it at: http://localhost:8080
echo.
echo Useful commands:
echo   - View logs:        docker-compose logs -f app
echo   - Stop app:         docker-compose down
echo   - Restart app:      docker-compose restart
echo   - View status:      docker-compose ps
echo.
echo Press any key to open the application in your browser...
pause >nul

start http://localhost:8080

echo.
echo Application opened in browser!
echo.

REM Setup auto-start on Windows startup
echo [BONUS] Setting up auto-start on PC startup...
set /p autostart="Do you want the app to start automatically when Windows starts? (y/n): "
if /i "!autostart!"=="y" (
    echo.
    echo Creating startup script...
    
    REM Get current directory
    set "CURRENT_DIR=%cd%"
    
    REM Create startup script
    (
        echo @echo off
        echo cd /d "!CURRENT_DIR!"
        echo echo Starting dbWebApp...
        echo docker info ^>nul 2^>^&1
        echo if errorlevel 1 ^(
        echo     echo Docker is not running. Starting Docker Desktop...
        echo     start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
        echo     timeout /t 40 /nobreak ^>nul
        echo ^)
        echo REM Start everything together - Docker Compose handles the ordering
        echo docker-compose up -d
        echo echo dbWebApp started successfully!
    ) > "%CURRENT_DIR%\start-dbwebapp.bat"
    
    REM Create shortcut in Startup folder
    set "STARTUP_FOLDER=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup"
    
    powershell -Command "$WS = New-Object -ComObject WScript.Shell; $SC = $WS.CreateShortcut('!STARTUP_FOLDER!\dbWebApp.lnk'); $SC.TargetPath = '!CURRENT_DIR!\start-dbwebapp.bat'; $SC.WorkingDirectory = '!CURRENT_DIR!'; $SC.WindowStyle = 7; $SC.Description = 'Start dbWebApp on startup'; $SC.Save()"
    
    if errorlevel 1 (
        echo [WARNING] Could not create auto-start shortcut.
        echo You can manually add start-dbwebapp.bat to your Startup folder.
    ) else (
        echo [OK] Auto-start configured!
        echo The app will now start automatically when Windows boots.
        echo.
        echo To disable: Delete shortcut from: !STARTUP_FOLDER!
    )
)
echo.
pause