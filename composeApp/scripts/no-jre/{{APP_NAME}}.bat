@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set "APP_NAME={{APP_NAME}}"
set "APP_VERSION={{APP_VERSION}}"
set "MAIN_CLASS={{MAIN_CLASS}}"
set "APP_HOME=%~dp0"

:: 检查 Java 环境
call :check_java
if "%JAVA_FOUND%"=="false" (
    echo [错误] 未找到 Java 运行时环境。
    echo.
    echo 请安装 Java 17 或更高版本，并确保以下之一：
    echo   1. JAVA_HOME 环境变量已设置
    echo   2. java 命令已在 PATH 中
    echo.
    echo 您可以从以下地址下载 Java：
    echo   - Eclipse Temurin: https://adoptium.net/
    echo   - Oracle JDK: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

echo 使用 Java: %JAVA_CMD%
"%JAVA_CMD%" -version 2>&1 | findstr "version" | findstr "17\|18\|19\|20\|21\|22\|23" >nul
if errorlevel 1 (
    echo [警告] 检测到 Java 版本可能不兼容。建议使用 Java 17 或更高版本。
)

:: 构建类路径
set "CLASSPATH=%APP_HOME%lib\*"

:: 启动应用
echo 启动 %APP_NAME% %APP_VERSION%...
"%JAVA_CMD%" -cp "%CLASSPATH%" %MAIN_CLASS% %*

exit /b %ERRORLEVEL%

:: 检查 Java 环境的子程序
:check_java
set "JAVA_FOUND=false"

:: 首先检查 JAVA_HOME
if not "%JAVA_HOME%"=="" (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        set "JAVA_FOUND=true"
        goto :eof
    )
)

:: 然后检查 PATH 中的 java
where java >nul 2>&1
if %ERRORLEVEL%==0 (
    for /f "tokens=*" %%a in ('where java 2^>nul') do (
        set "JAVA_CMD=%%a"
        set "JAVA_FOUND=true"
        goto :eof
    )
)

goto :eof
