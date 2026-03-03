#Requires -Version 5.1
# MicYou 启动脚本 (PowerShell)
# 此脚本自动检测系统 Java 环境并启动应用

$AppName = "{{APP_NAME}}"
$AppVersion = "{{APP_VERSION}}"
$MainClass = "{{MAIN_CLASS}}"
$AppHome = Split-Path -Parent $MyInvocation.MyCommand.Definition

function Test-JavaInstallation {
    $javaInfo = @{
        Found = $false
        Command = $null
        Version = $null
        IsCompatible = $false
    }

    # 首先检查 JAVA_HOME
    if ($env:JAVA_HOME) {
        $javaHomePath = Join-Path $env:JAVA_HOME "bin\java.exe"
        if (Test-Path $javaHomePath) {
            $javaInfo.Command = $javaHomePath
            $javaInfo.Found = $true
        }
    }

    # 然后检查 PATH 中的 java
    if (-not $javaInfo.Found) {
        $javaCmd = Get-Command "java" -ErrorAction SilentlyContinue
        if ($javaCmd) {
            $javaInfo.Command = $javaCmd.Source
            $javaInfo.Found = $true
        }
    }

    # 检查 Java 版本
    if ($javaInfo.Found -and $javaInfo.Command) {
        try {
            $versionOutput = & $javaInfo.Command -version 2>&1
            $versionString = $versionOutput | Select-String -Pattern '"(\d+)' | Select-Object -First 1
            if ($versionString) {
                $version = [int]($versionString.Matches.Groups[1].Value)
                $javaInfo.Version = $version
                $javaInfo.IsCompatible = $version -ge 17
            }
        }
        catch {
            Write-Warning "无法检测 Java 版本: $_"
        }
    }

    return $javaInfo
}

function Show-JavaNotFoundError {
    Write-Host "[错误] 未找到 Java 运行时环境。" -ForegroundColor Red
    Write-Host ""
    Write-Host "请安装 Java 17 或更高版本，并确保以下之一：" -ForegroundColor Yellow
    Write-Host "  1. JAVA_HOME 环境变量已设置"
    Write-Host "  2. java 命令已在 PATH 中"
    Write-Host ""
    Write-Host "您可以从以下地址下载 Java：" -ForegroundColor Cyan
    Write-Host "  - Eclipse Temurin: https://adoptium.net/"
    Write-Host "  - Oracle JDK: https://www.oracle.com/java/technologies/downloads/"
    Write-Host ""
    
    $response = Read-Host "按 Enter 键退出"
    exit 1
}

# 主程序
Write-Host "正在检查 Java 环境..." -ForegroundColor Cyan

$javaInfo = Test-JavaInstallation

if (-not $javaInfo.Found) {
    Show-JavaNotFoundError
}

Write-Host "使用 Java: $($javaInfo.Command)" -ForegroundColor Green

if (-not $javaInfo.IsCompatible) {
    Write-Host "[警告] 检测到 Java 版本 $($javaInfo.Version) 可能不兼容。建议使用 Java 17 或更高版本。" -ForegroundColor Yellow
}

# 构建类路径
$libDir = Join-Path $AppHome "lib"
$classpath = Join-Path $libDir "*"

# 启动应用
Write-Host "启动 $AppName $AppVersion..." -ForegroundColor Cyan

$startArgs = @{
    FilePath = $javaInfo.Command
    ArgumentList = @("-cp", $classpath, $MainClass) + $args
    NoNewWindow = $true
    Wait = $true
    PassThru = $true
}

$process = Start-Process @startArgs
exit $process.ExitCode
