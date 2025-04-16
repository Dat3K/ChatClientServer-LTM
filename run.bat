@echo off
echo Chat Client-Server Application
echo ============================
echo.
echo 1. Run both client and server
echo 2. Run only server
echo 3. Run only client
echo 4. Build the application
echo 5. Exit
echo.

set /p choice=Enter your choice (1-5): 

if "%choice%"=="1" (
    echo Starting both client and server...
    java -jar app\build\libs\app.jar both
) else if "%choice%"=="2" (
    echo Starting server...
    java -cp app\build\libs\app.jar chatclientserver.ltm.server.ServerMain
) else if "%choice%"=="3" (
    echo Starting client...
    java -cp app\build\libs\app.jar chatclientserver.ltm.client.ClientMain
) else if "%choice%"=="4" (
    echo Building the application...
    call gradlew.bat clean build jar
    echo Build completed. JAR file created at app\build\libs\app.jar
) else if "%choice%"=="5" (
    echo Exiting...
    exit
) else (
    echo Invalid choice. Please try again.
    pause
    call %0
)
