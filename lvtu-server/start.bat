@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Installing dependencies and running...
cd /d "C:\Users\32070\Lvtu-main\lvtu-server"

if not exist ".mvn\wrapper\maven-wrapper.jar" (
    echo Downloading Maven wrapper...
    powershell -Command "(New-Object System.Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '.mvn/wrapper/maven-wrapper.jar')"
)

echo Running Spring Boot...
"%JAVA_HOME%\bin\java" -jar ".mvn\wrapper\maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=C:\Users\32070\Lvtu-main\lvtu-server" org.apache.maven.wrapper.MavenWrapperMain spring-boot:run