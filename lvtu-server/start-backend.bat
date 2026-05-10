@echo off
echo ========================================
echo Lvtu Backend Service Start Script
echo ========================================
echo.

cd /d "%~dp0"

echo [1/3] Checking Java environment...
if not exist "C:\Program Files\Java\jdk-17" (
    echo [ERROR] JDK 17 not found
    pause
    exit /b 1
)

set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [OK] Java version:
java -version
echo.

echo [2/3] Checking Maven environment...
if not exist "C:\Maven\apache-maven-3.9.15\bin\mvn.cmd" (
    echo [ERROR] Maven not found
    pause
    exit /b 1
)

echo [OK] Maven version:
"C:\Maven\apache-maven-3.9.15\bin\mvn.cmd" -version
echo.

echo [3/3] Starting Spring Boot application...
echo ========================================
echo.

"C:\Maven\apache-maven-3.9.15\bin\mvn.cmd" spring-boot:run

pause