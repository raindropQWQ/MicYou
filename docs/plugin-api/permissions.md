# 权限系统

MicYou 插件系统使用权限机制来保护用户数据和系统安全。

## 概述

插件需要在 `plugin.json` 中声明所需权限。未声明的权限操作将被拒绝。

## 可用权限

| 权限 | ID | 说明 | 风险等级 |
|------|------|------|----------|
| 存储 | `storage` | 访问插件专属存储空间 | 低 |
| 网络 | `network` | 网络访问（HTTP/HTTPS） | 中 |
| 摄像头 | `camera` | 访问摄像头设备 | 高 |
| 麦克风 | `microphone` | 访问麦克风设备 | 高 |
| 蓝牙 | `bluetooth` | 访问蓝牙功能 | 中 |

## 声明权限

在 `plugin.json` 中声明：

```json
{
  "id": "com.example.network-plugin",
  "name": "Network Plugin",
  "permissions": ["network", "storage"],
  ...
}
```

## 权限验证

### 导入时验证

插件导入时会检查权限声明：
- 验证权限 ID 是否有效
- 显示权限列表给用户确认

### 运行时验证

插件运行时，所有敏感操作都会检查权限：
- 未声明权限的操作将被拒绝
- 返回错误或默认值

## 权限详情

### storage（存储）

**说明：** 访问插件专属存储空间

**允许的操作：**
- 在 `pluginDataDir` 目录下读写文件
- 使用 `PluginContext` 的存储方法

**不允许的操作：**
- 访问其他插件的存储空间
- 访问应用私有目录外的文件

**示例：**
```kotlin
override fun onLoad(context: PluginContext) {
    // 使用 PluginContext 存储（自动获得权限）
    context.putString("key", "value")
    
    // 使用文件系统（需要 storage 权限）
    val dataDir = File(context.pluginDataDir)
    val file = File(dataDir, "data.txt")
    file.writeText("content")
}
```

### network（网络）

**说明：** 网络访问权限

**允许的操作：**
- HTTP/HTTPS 请求
- WebSocket 连接
- TCP/UDP 通信

**安全限制：**
- 只能访问公开网络资源
- 不能访问本地服务（localhost）

**示例：**
```kotlin
// 需要 network 权限
val client = HttpClient()
val response = client.get("https://api.example.com/data")
```

### camera（摄像头）

**说明：** 访问摄像头设备

**允许的操作：**
- 打开摄像头预览
- 捕获图像/视频

**安全限制：**
- 用户必须明确授权
- 使用时显示指示器

**注意：** 此权限目前为预留功能，实际支持取决于平台实现。

### microphone（麦克风）

**说明：** 访问麦克风设备

**允许的操作：**
- 录制音频

**安全限制：**
- 用户必须明确授权
- 录制时显示指示器

**注意：** 此权限目前为预留功能，实际支持取决于平台实现。

### bluetooth（蓝牙）

**说明：** 访问蓝牙功能

**允许的操作：**
- 扫描蓝牙设备
- 建立蓝牙连接
- 蓝牙数据传输

**安全限制：**
- 需要 Android 蓝牙权限
- 桌面端可能需要系统权限

**注意：** 此权限目前为预留功能，实际支持取决于平台实现。

## 安全沙箱

### 文件访问限制

插件只能访问以下位置：
- `pluginDataDir` - 插件专属数据目录
- 插件包内的资源文件

访问其他位置将抛出 `SecurityException`。

### 网络访问限制

- 只允许声明了 `network` 权限的插件进行网络请求
- 禁止访问本地回环地址（localhost/127.0.0.1）
- 禁止访问内网地址（可选配置）

### 类加载限制

插件使用独立的 `URLClassLoader`，与主应用隔离：
- 插件无法访问主应用的内部类
- 插件之间相互隔离

## 最佳实践

### 最小权限原则

只申请必要的权限：

```json
// 好：只申请需要的权限
{
  "permissions": ["network"]
}

// 避免：申请不必要的权限
{
  "permissions": ["network", "storage", "camera", "microphone", "bluetooth"]
}
```

### 优雅降级

处理权限缺失的情况：

```kotlin
override fun onEnable() {
    if (hasNetworkPermission()) {
        startNetworkService()
    } else {
        context?.log("Network permission not granted, using offline mode")
        startOfflineMode()
    }
}
```

### 权限说明

在插件描述中说明权限用途：

```json
{
  "description": "A plugin that syncs data to cloud. Requires network permission for API calls."
}
```

## 权限检查 API

插件可以检查自身权限：

```kotlin
// 检查是否有特定权限
fun hasPermission(permission: PluginPermission): Boolean {
    return manifest.permissions.contains(permission.id)
}

// 使用示例
if (hasPermission(PluginPermission.NETWORK)) {
    makeNetworkRequest()
}
```

## 未来规划

- 运行时权限请求对话框
- 权限使用审计日志
- 更细粒度的权限控制
- 用户可撤销已授权权限
