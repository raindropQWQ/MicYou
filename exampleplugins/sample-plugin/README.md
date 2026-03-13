# Sample Plugin

这是一个示例插件项目，演示如何使用 MicYou Plugin API 开发插件。

## 功能

- 演示插件生命周期（onLoad, onEnable, onDisable, onUnload）
- 展示如何实现主窗口 UI（`PluginUIProvider`）
- 展示如何实现对话框 UI
- 展示如何实现设置页面（`PluginSettingsProvider`）
- 演示数据持久化存储

## 项目结构

```
sample-plugin/
├── build.gradle.kts          # Gradle 构建脚本
├── settings.gradle.kts       # Gradle 设置
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/example/sampleplugin/
│       │       ├── SamplePlugin.kt      # 主类
│       │       └── SamplePluginUI.kt    # UI 组件
│       └── resources/
│           └── plugin.json              # 插件清单
└── README.md
```

## 构建

### 前提条件

1. 确保 MicYou 项目已构建，`plugin-api` 模块可用
2. 安装 JDK 17 或更高版本

### 构建步骤

```bash
# 进入项目目录
cd examples/sample-plugin

# 构建 JAR
./gradlew jar

# 创建插件包
./gradlew createPluginPackage
```

构建完成后，插件包位于 `build/sample-plugin.micyou-plugin.zip`

## 安装

1. 打开 MicYou 应用
2. 进入设置 → 插件
3. 点击"导入插件"按钮
4. 选择 `sample-plugin.micyou-plugin.zip` 文件
5. 启用插件

## 使用

### 主窗口

点击主界面插件按钮，然后点击 "Sample Plugin" 打开主窗口：
- 查看和增加计数器
- 设置自定义消息

### 对话框

从插件列表点击插件图标可打开对话框，显示当前计数器值。

### 设置页面

在设置 → 插件 → Sample Plugin 中可以：
- 修改显示消息
- 管理计数器
- 查看插件信息

## 代码说明

### 主类 (SamplePlugin.kt)

```kotlin
class SamplePlugin : Plugin, PluginUIProvider, PluginSettingsProvider {
    // 实现 Plugin 接口
    override val manifest = PluginManifest(...)
    
    override fun onLoad(context: PluginContext) {
        // 初始化，加载数据
    }
    
    override fun onEnable() {
        // 插件启用
    }
    
    override fun onDisable() {
        // 插件禁用
    }
    
    // 实现 PluginUIProvider
    override val hasMainWindow = true
    override val hasDialog = true
    
    // 实现 PluginSettingsProvider
    @Composable
    override fun SettingsContent() {
        // 设置页面 UI
    }
}
```

### 数据存储

使用 `PluginContext` 存储数据：

```kotlin
// 保存数据
context.putString("message", "Hello")
context.putInt("counter", 42)

// 读取数据
val message = context.getString("message", "")
val counter = context.getInt("counter", 0)
```

## 扩展开发

基于此示例开发自己的插件：

1. 复制此项目作为模板
2. 修改 `plugin.json` 中的 ID、名称等信息
3. 实现 `Plugin` 接口
4. 根据需要实现 `PluginUIProvider` 和 `PluginSettingsProvider`
5. 构建并打包

## 相关文档

- [快速开始指南](../../docs/plugin-api/README.md)
- [API 参考](../../docs/plugin-api/api-reference.md)
- [插件包格式](../../docs/plugin-api/plugin-format.md)
- [最佳实践](../../docs/plugin-api/best-practices.md)
