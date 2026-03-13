# API 参考文档

本文档详细描述 MicYou Plugin API 的所有接口、类和方法。

## 目录

- [Plugin](#plugin)
- [PluginManifest](#pluginmanifest)
- [PluginContext](#plugincontext)
- [PluginInfo](#plugininfo)
- [PluginPlatform](#pluginplatform)
- [PluginPermission](#pluginpermission)
- [PluginUIProvider](#pluginuiprovider)
- [PluginSettingsProvider](#pluginsettingsprovider)

---

## Plugin

插件主接口，所有插件必须实现此接口。

```kotlin
interface Plugin {
    val manifest: PluginManifest
    fun onLoad(context: PluginContext) {}
    fun onEnable() {}
    fun onDisable() {}
    fun onUnload() {}
}
```

### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `manifest` | `PluginManifest` | 插件元数据清单，包含插件的基本信息 |

### 方法

#### onLoad

```kotlin
fun onLoad(context: PluginContext)
```

插件加载时调用。这是插件初始化的最佳时机。

**参数：**
- `context` - 插件上下文，提供运行环境访问能力

**调用时机：** 插件包被解析并验证通过后，在插件实例创建后立即调用。

**示例：**
```kotlin
override fun onLoad(context: PluginContext) {
    this.context = context
    context.log("Plugin ${manifest.name} loaded")
}
```

#### onEnable

```kotlin
fun onEnable()
```

插件启用时调用。

**调用时机：** 用户在设置中启用插件时调用。

**示例：**
```kotlin
override fun onEnable() {
    context?.log("Plugin enabled")
    startServices()
}
```

#### onDisable

```kotlin
fun onDisable()
```

插件禁用时调用。

**调用时机：** 用户在设置中禁用插件时调用。

**示例：**
```kotlin
override fun onDisable() {
    context?.log("Plugin disabled")
    stopServices()
}
```

#### onUnload

```kotlin
fun onUnload()
```

插件卸载时调用。

**调用时机：** 插件被删除或应用关闭时调用。

**示例：**
```kotlin
override fun onUnload() {
    releaseResources()
}
```

---

## PluginManifest

插件元数据清单，描述插件的基本信息。

```kotlin
@Serializable
data class PluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String = "",
    val tags: List<String> = emptyList(),
    val platform: PluginPlatform = PluginPlatform.BOTH,
    @SerialName("minApiVersion")
    val minApiVersion: String,
    val permissions: List<String> = emptyList(),
    @SerialName("mainClass")
    val mainClass: String
)
```

### 属性

| 属性 | 类型 | 必需 | 默认值 | 说明 |
|------|------|------|--------|------|
| `id` | `String` | 是 | - | 插件唯一标识符，反向域名格式 |
| `name` | `String` | 是 | - | 插件显示名称 |
| `version` | `String` | 是 | - | 版本号，遵循语义化版本规范 |
| `author` | `String` | 是 | - | 作者名称 |
| `description` | `String` | 否 | `""` | 插件描述 |
| `tags` | `List<String>` | 否 | `emptyList()` | 标签列表 |
| `platform` | `PluginPlatform` | 否 | `BOTH` | 支持的平台 |
| `minApiVersion` | `String` | 是 | - | 最低 API 版本要求 |
| `permissions` | `List<String>` | 否 | `emptyList()` | 所需权限列表 |
| `mainClass` | `String` | 是 | - | 主类全限定名 |

### 示例

```kotlin
val manifest = PluginManifest(
    id = "com.example.myplugin",
    name = "My Plugin",
    version = "1.0.0",
    author = "Developer",
    description = "A sample plugin",
    tags = listOf("utility", "audio"),
    platform = PluginPlatform.DESKTOP,
    minApiVersion = "1.0.0",
    permissions = listOf("network", "storage"),
    mainClass = "com.example.myplugin.MyPlugin"
)
```

---

## PluginContext

插件运行上下文，提供插件运行环境访问能力。

```kotlin
interface PluginContext {
    val pluginId: String
    val pluginDataDir: String
    
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)
    fun getFloat(key: String, defaultValue: Float): Float
    fun putFloat(key: String, value: Float)
    
    fun log(message: String)
    fun logError(message: String, throwable: Throwable? = null)
}
```

### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `pluginId` | `String` | 插件唯一标识符 |
| `pluginDataDir` | `String` | 插件专属数据目录路径 |

### 数据存储方法

#### getString / putString

```kotlin
fun getString(key: String, defaultValue: String): String
fun putString(key: String, value: String)
```

存储和读取字符串值。

**示例：**
```kotlin
context.putString("lastUrl", "https://example.com")
val url = context.getString("lastUrl", "")
```

#### getBoolean / putBoolean

```kotlin
fun getBoolean(key: String, defaultValue: Boolean): Boolean
fun putBoolean(key: String, value: Boolean)
```

存储和读取布尔值。

**示例：**
```kotlin
context.putBoolean("autoConnect", true)
val autoConnect = context.getBoolean("autoConnect", false)
```

#### getInt / putInt

```kotlin
fun getInt(key: String, defaultValue: Int): Int
fun putInt(key: String, value: Int)
```

存储和读取整数值。

**示例：**
```kotlin
context.putInt("volume", 80)
val volume = context.getInt("volume", 50)
```

#### getFloat / putFloat

```kotlin
fun getFloat(key: String, defaultValue: Float): Float
fun putFloat(key: String, value: Float)
```

存储和读取浮点数值。

**示例：**
```kotlin
context.putFloat("gain", 1.5f)
val gain = context.getFloat("gain", 1.0f)
```

### 日志方法

#### log

```kotlin
fun log(message: String)
```

记录信息日志。

**示例：**
```kotlin
context.log("Connection established")
```

#### logError

```kotlin
fun logError(message: String, throwable: Throwable? = null)
```

记录错误日志。

**示例：**
```kotlin
try {
    // some operation
} catch (e: Exception) {
    context.logError("Operation failed", e)
}
```

---

## PluginInfo

插件运行时信息。

```kotlin
data class PluginInfo(
    val manifest: PluginManifest,
    val isEnabled: Boolean = false,
    val isLoaded: Boolean = false,
    val installPath: String,
    val iconPath: String? = null
)
```

### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `manifest` | `PluginManifest` | 插件元数据 |
| `isEnabled` | `Boolean` | 是否已启用 |
| `isLoaded` | `Boolean` | 是否已加载 |
| `installPath` | `String` | 安装路径 |
| `iconPath` | `String?` | 图标路径（可选） |

---

## PluginPlatform

插件支持平台枚举。

```kotlin
enum class PluginPlatform {
    MOBILE,
    DESKTOP,
    BOTH
}
```

### 值说明

| 值 | 说明 |
|------|------|
| `MOBILE` | 仅移动端（Android）支持 |
| `DESKTOP` | 仅桌面端（JVM）支持 |
| `BOTH` | 两端都需要安装，用于跨平台功能 |

---

## PluginPermission

插件权限枚举。

```kotlin
enum class PluginPermission(val id: String) {
    STORAGE("storage"),
    NETWORK("network"),
    CAMERA("camera"),
    MICROPHONE("microphone"),
    BLUETOOTH("bluetooth")
}
```

### 权限说明

| 权限 | ID | 说明 |
|------|------|------|
| `STORAGE` | `storage` | 文件存储访问权限 |
| `NETWORK` | `network` | 网络访问权限 |
| `CAMERA` | `camera` | 摄像头访问权限 |
| `MICROPHONE` | `microphone` | 麦克风访问权限 |
| `BLUETOOTH` | `bluetooth` | 蓝牙访问权限 |

---

## PluginUIProvider

插件 UI 提供者接口。实现此接口可为插件提供 UI 组件。

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

### 属性

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `hasMainWindow` | `Boolean` | `false` | 是否提供主窗口 |
| `hasDialog` | `Boolean` | `false` | 是否提供对话框 |

### 方法

#### MainWindow

```kotlin
@Composable
fun MainWindow(onClose: () -> Unit)
```

插件主窗口内容。当用户从插件列表点击插件时显示。

**参数：**
- `onClose` - 关闭窗口的回调

**示例：**
```kotlin
@Composable
override fun MainWindow(onClose: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("My Plugin Window", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClose) {
            Text("Close")
        }
    }
}
```

#### DialogContent

```kotlin
@Composable
fun DialogContent(onDismiss: () -> Unit)
```

插件对话框内容。

**参数：**
- `onDismiss` - 关闭对话框的回调

**示例：**
```kotlin
@Composable
override fun DialogContent(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Plugin Dialog") },
        text = { Text("This is a plugin dialog content") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
```

---

## PluginSettingsProvider

插件设置页面提供者接口。实现此接口可为插件提供设置页面。

```kotlin
interface PluginSettingsProvider {
    @Composable
    fun SettingsContent()
}
```

### 方法

#### SettingsContent

```kotlin
@Composable
fun SettingsContent()
```

插件设置页面内容。在设置 → 插件详情中显示。

**示例：**
```kotlin
class MyPlugin : Plugin, PluginSettingsProvider {
    @Composable
    override fun SettingsContent() {
        var enabled by remember { mutableStateOf(false) }
        var serverUrl by remember { mutableStateOf("") }
        
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Plugin Settings", style = MaterialTheme.typography.titleMedium)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable feature")
                Switch(
                    checked = enabled,
                    onCheckedChange = { enabled = it }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```
