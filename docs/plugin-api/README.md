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
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("plugin.compose") version "2.2.20"
    id("org.jetbrains.compose") version "1.7.3"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly(files("path/to/plugin-api-jvm.jar"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Compose
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
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
    
    // 窗口配置（仅桌面端）
    val windowWidth: Dp get() = 600.dp
    val windowHeight: Dp get() = 500.dp
    val windowTitle: String get() = "Plugin Window"
    val windowResizable: Boolean get() = true
    
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

### 添加主窗口

让插件实现 `PluginUIProvider` 并设置 `hasMainWindow = true`：

```kotlin
import androidx.compose.ui.unit.dp

class MyPlugin : Plugin, PluginUIProvider {
    override val hasMainWindow = true
    
    // 自定义窗口大小和标题
    override val windowWidth = 800.dp
    override val windowHeight = 600.dp
    override val windowTitle = "My Plugin Window"
    override val windowResizable = true
    
    @Composable
    override fun MainWindow(onClose: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text("My Plugin Window", style = MaterialTheme.typography.headlineMedium)
            
            Spacer(Modifier.height(16.dp))
            
            Button(onClick = onClose) { 
                Text("Close") 
            }
        }
    }
}
```

### 添加设置页面

让插件实现 `PluginSettingsProvider`：

```kotlin
class MyPlugin : Plugin, PluginSettingsProvider {
    private var context: PluginContext? = null
    
    override fun onLoad(context: PluginContext) {
        this.context = context
    }
    
    @Composable
    override fun SettingsContent() {
        var enabled by remember { 
            mutableStateOf(context?.getBoolean("feature_enabled", false) ?: false) 
        }
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable feature")
                Switch(
                    checked = enabled, 
                    onCheckedChange = { 
                        enabled = it
                        context?.putBoolean("feature_enabled", it)
                    }
                )
            }
        }
    }
}
```

### 同时使用多个接口

一个插件可以同时实现多个接口：

```kotlin
class MyPlugin : Plugin, PluginUIProvider, PluginSettingsProvider {
    private var context: PluginContext? = null
    
    override val manifest = PluginManifest(
        id = "com.example.myplugin",
        name = "My Plugin",
        version = "1.0.0",
        author = "Your Name",
        description = "A plugin with UI and settings",
        minApiVersion = "1.0.0",
        mainClass = "com.example.myplugin.MyPlugin"
    )
    
    // PluginUIProvider
    override val hasMainWindow = true
    override val windowWidth = 700.dp
    override val windowHeight = 500.dp
    override val windowTitle = "My Plugin"
    
    @Composable
    override fun MainWindow(onClose: () -> Unit) {
        // 主窗口内容
    }
    
    // PluginSettingsProvider
    @Composable
    override fun SettingsContent() {
        // 设置页面内容
    }
}
```

## 本地化

插件支持多语言本地化。在资源目录创建 `i18n/` 文件夹：

```
src/main/resources/
└── i18n/
    ├── en.json
    ├── zh-CN.json
    └── ja.json
```

JSON 文件格式：

```json
{
  "plugin.name": "My Plugin",
  "plugin.description": "A sample plugin",
  "settings.title": "Settings",
  "button.save": "Save"
}
```

在代码中使用本地化：

```kotlin
class MyPlugin : Plugin, PluginSettingsProvider {
    private var context: PluginContext? = null
    
    override fun onLoad(context: PluginContext) {
        this.context = context
        // 加载本地化资源
        context.loadLocale("zh-CN")
    }
    
    @Composable
    override fun SettingsContent() {
        Column {
            // 使用本地化字符串
            Text(context?.getString("settings.title") ?: "Settings")
            Button(onClick = { /* ... */ }) {
                Text(context?.getString("button.save") ?: "Save")
            }
        }
    }
}
```

### 支持的本地化方法

`PluginContext` 提供以下本地化方法：

```kotlin
interface PluginContext {
    // 加载指定语言
    fun loadLocale(languageTag: String)
    
    // 获取当前语言
    val currentLocale: String
    
    // 获取本地化字符串
    fun getString(key: String): String?
    fun getString(key: String, defaultValue: String): String
    
    // 带参数的格式化字符串
    fun formatString(key: String, vararg args: Any): String
}
```

### 语言标签格式

使用 IETF BCP 47 语言标签：
- `en` - 英语
- `zh-CN` - 简体中文
- `zh-TW` - 繁体中文
- `ja` - 日语
- `ko` - 韩语
- `de` - 德语
- `fr` - 法语
- `es` - 西班牙语

### 回退机制

如果当前语言的翻译不存在，系统会按以下顺序回退：
1. 请求的具体语言（如 `zh-CN`）
2. 基础语言（如 `zh`）
3. 插件默认语言（`plugin.json` 中声明）
4. 英语（`en`）
5. 返回键名本身

### 实现 PluginLocalizationProvider

插件可以实现 `PluginLocalizationProvider` 接口来提供本地化字符串：

```kotlin
class MyPlugin : Plugin, PluginSettingsProvider, PluginLocalizationProvider {
    private var context: PluginContext? = null
    
    // 插件本地化提供者
    override fun getLocalizedString(languageCode: String, key: String): String? {
        return when (languageCode) {
            "zh", "zh-CN" -> zhStrings[key]
            "en" -> enStrings[key]
            else -> enStrings[key]
        }
    }
    
    override fun getSupportedLanguages(): List<String> {
        return listOf("zh", "en")
    }
    
    @Composable
    override fun SettingsContent() {
        // 使用本地化
        val strings = context?.localization
        Text(strings?.getString("settings_title", "Settings") ?: "Settings")
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

## 插件存储

插件可以使用 `PluginContext` 存储持久化数据：

```kotlin
override fun onLoad(context: PluginContext) {
    // 读取设置
    val userName = context.getString("user_name", "")
    val counter = context.getInt("counter", 0)
    
    // 保存设置
    context.putString("user_name", "John")
    context.putInt("counter", counter + 1)
}
```

数据存储在插件专属目录中，卸载插件时会被删除。

## 插件更新

当导入相同 ID 但版本号更高的插件时，系统会自动：
1. 禁用旧版本插件
2. 删除旧版本文件
3. 安装新版本
4. 如果旧版本是启用状态，新版本会自动启用

版本号格式：`主版本.次版本.修订版本`（如 `1.2.3`）

## 文档

- [API 参考](api-reference.md)
- [插件包格式](plugin-format.md)
- [权限系统](permissions.md)
- [最佳实践](best-practices.md)

## 示例项目

查看 `examples/sample-plugin/` 获取完整的示例插件项目。
