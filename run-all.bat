@echo off
set "MVN=C:\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd"
set "TOMCAT=C:\Program Files\Apache Software Foundation\Tomcat 10.1"
cd /d "%~dp0"

echo [1/4] Starting MySQL...
net start MySQL80 >nul 2>&1

echo [2/4] Building...
call "%MVN%" -q clean package -DskipTests
if errorlevel 1 (echo BUILD FAILED & pause & exit /b 1)

echo [3/4] Deploying...
net stop Tomcat10 >nul 2>&1
rmdir /s /q "%TOMCAT%\webapps\CampusGig" 2>nul
copy /y target\CampusGig.war "%TOMCAT%\webapps\CampusGig.war" >nul
if errorlevel 1 (echo DEPLOY FAILED - run this as Administrator & pause & exit /b 1)

echo [4/4] Starting Tomcat...
net start Tomcat10
timeout /t 8 >nul
start http://localhost:8080/CampusGig/dashboard.html
