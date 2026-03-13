# MicYou Plugin API

MicYou 插件开发快速开始指南。

## 概述

MicYou 插件系统允许开发者扩展应用功能。插件可以：
- 提供自定义 UI 界面（主窗口或对话框）
- 提供设置页面
- 访问插件专属存储空间
- 请求特定权限（网络、存储等）

## 快速开始

### 1. 创建项目

创建一个新的 Kotlin/JVM Gradle 项目：

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

dependencies {
    compileOnly("com.lanrhyme.micyou:plugin-api:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
```

### 2. 实现 Plugin 接口

创建插件主类：

```kotlin
package com.example.myplugin

import com.lanrhyme.micyou.plugin.*

class MyPlugin : Plugin {
    override val manifest = PluginManifest(
        id = "com.example.myplugin",
        name = "My Plugin",
        version = "1.0.0",
        author = "Your Name",
        description = "A sample plugin for MicYou",
        minApiVersion = "1.0.0",
        mainClass = "com.example.myplugin.MyPlugin"
    )
    
    override fun onLoad(context: PluginContext) {
        context.log("Plugin loaded!")
    }
    
    override fun onEnable() {
        // 插件启用时调用
    }
    
    override fun onDisable() {
        // 插件禁用时调用
    }
    
    override fun onUnload() {
        // 插件卸载时调用
    }
}
```

### 3. 创建 plugin.json

在资源目录创建 `plugin.json`：

```json
{
  "id": "com.example.myplugin",
  "name": "My Plugin",
  "version": "1.0.0",
  "author": "Your Name",
  "description": "A sample plugin for MicYou",
  "minApiVersion": "1.0.0",
  "mainClass": "com.example.myplugin.MyPlugin"
}
```

### 4. 打包插件

构建 JAR 文件并打包：

```bash
./gradlew jar

# 创建插件包
mkdir -p plugin-package
cp build/libs/my-plugin.jar plugin-package/plugin.jar
cp src/main/resources/plugin.json plugin-package/
cd plugin-package && zip -r ../my-plugin.micyou-plugin.zip .
```

### 5. 安装插件

1. 打开 MicYou 应用
2. 进入设置 → 插件
3. 点击"导入插件"按钮
4. 选择 `.micyou-plugin.zip` 文件
5. 启用插件

## 核心接口

### Plugin

插件主接口，定义生命周期方法：

```kotlin
interface Plugin {
    val manifest: PluginManifest
    fun onLoad(context: PluginContext) {}
    fun onEnable() {}
    fun onDisable() {}
    fun onUnload() {}
}
```

### PluginContext

提供插件运行环境：

```kotlin
interface PluginContext {
    val pluginId: String
    val pluginDataDir: String
    
    // 数据存储
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)
    fun getFloat(key: String, defaultValue: Float): Float
    fun putFloat(key: String, value: Float)
    
    // 日志
    fun log(message: String)
    fun logError(message: String, throwable: Throwable? = null)
}
```

### PluginUIProvider

提供插件 UI 组件：

```kotlin
interface PluginUIProvider {
    val hasMainWindow: Boolean get() = false
    val hasDialog: Boolean get() = false
    
    @Composable
    fun MainWindow(onClose: () -> Unit) {}
    
    @Composable
    fun DialogContent(onDismiss: () -> Unit) {}
}
```

### PluginSettingsProvider

提供插件设置页面：

```kotlin
interface PluginSettingsProvider {
    @Composable
    fun SettingsContent()
}
```

## 进阶功能

### 添加 UI 界面

让插件实现 `PluginUIProvider`：

```kotlin
class MyPlugin : Plugin, PluginUIProvider {
    override val hasMainWindow = true
    
    @Composable
    override fun MainWindow(onClose: () -> Unit) {
        Column {
            Text("My Plugin Window")
            Button(onClick = onClose) { Text("Close") }
        }
    }
}
```

### 添加设置页面

让插件实现 `PluginSettingsProvider`：

```kotlin
class MyPlugin : Plugin, PluginSettingsProvider {
    @Composable
    override fun SettingsContent() {
        var enabled by remember { mutableStateOf(false) }
        Row {
            Text("Enable feature")
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}
```

### 请求权限

在 `plugin.json` 中声明权限：

```json
{
  "permissions": ["network", "storage"]
}
```

可用权限：
- `storage` - 文件存储访问
- `network` - 网络访问
- `camera` - 摄像头访问
- `microphone` - 麦克风访问
- `bluetooth` - 蓝牙访问

### 平台兼容性

指定插件支持的平台：

```json
{
  "platform": "desktop"
}
```

可用值：
- `mobile` - 仅移动端
- `desktop` - 仅桌面端
- `both` - 两端都需要安装（默认）

## 文档

- [API 参考](api-reference.md)
- [插件包格式](plugin-format.md)
- [权限系统](permissions.md)
- [最佳实践](best-practices.md)

## 示例项目

查看 `examples/sample-plugin/` 获取完整的示例插件项目。
