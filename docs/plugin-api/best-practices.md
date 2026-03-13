# 插件开发最佳实践

本文档提供 MicYou 插件开发的最佳实践和建议。

## 项目结构

### 推荐目录结构

```
my-plugin/
├── build.gradle.kts
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/example/myplugin/
│       │       ├── MyPlugin.kt           # 主类
│       │       ├── MyPluginUI.kt         # UI 组件
│       │       ├── MyPluginSettings.kt   # 设置页面
│       │       └── model/                # 数据模型
│       │           └── Config.kt
│       └── resources/
│           └── plugin.json               # 清单文件
└── README.md
```

### 代码组织

将不同功能分离到不同文件：

```kotlin
// MyPlugin.kt - 主类
class MyPlugin : Plugin {
    override val manifest = PluginManifest(...)
    // 生命周期方法
}

// MyPluginUI.kt - UI 组件
class MyPluginUI : PluginUIProvider {
    // UI 实现
}

// MyPluginSettings.kt - 设置页面
class MyPluginSettings : PluginSettingsProvider {
    // 设置页面实现
}
```

## 生命周期管理

### 正确管理状态

```kotlin
class MyPlugin : Plugin {
    private var context: PluginContext? = null
    private var isRunning = false
    
    override fun onLoad(context: PluginContext) {
        this.context = context
        context.log("Plugin loaded")
    }
    
    override fun onEnable() {
        if (isRunning) return
        isRunning = true
        startBackgroundTask()
    }
    
    override fun onDisable() {
        if (!isRunning) return
        isRunning = false
        stopBackgroundTask()
    }
    
    override fun onUnload() {
        context = null
        releaseAllResources()
    }
}
```

### 避免内存泄漏

```kotlin
class MyPlugin : Plugin {
    private val jobs = mutableListOf<Job>()
    
    override fun onEnable() {
        jobs.add(startTask1())
        jobs.add(startTask2())
    }
    
    override fun onDisable() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
}
```

## 数据存储

### 使用 PluginContext 存储

```kotlin
class MyPlugin : Plugin {
    private var context: PluginContext? = null
    
    fun saveConfig(config: Config) {
        context?.apply {
            putString("serverUrl", config.serverUrl)
            putInt("port", config.port)
            putBoolean("ssl", config.useSsl)
        }
    }
    
    fun loadConfig(): Config {
        return context?.let {
            Config(
                serverUrl = it.getString("serverUrl", "localhost"),
                port = it.getInt("port", 8080),
                useSsl = it.getBoolean("ssl", false)
            )
        } ?: Config()
    }
}
```

### 文件存储

```kotlin
class MyPlugin : Plugin {
    private fun saveDataFile(data: String) {
        val context = context ?: return
        val file = File(context.pluginDataDir, "data.json")
        file.writeText(data)
    }
    
    private fun loadDataFile(): String? {
        val context = context ?: return null
        val file = File(context.pluginDataDir, "data.json")
        return if (file.exists()) file.readText() else null
    }
}
```

## UI 开发

### 响应式 UI

```kotlin
class MyPluginUI : PluginUIProvider {
    override val hasMainWindow = true
    
    @Composable
    override fun MainWindow(onClose: () -> Unit) {
        var data by remember { mutableStateOf(emptyList<Item>()) }
        var loading by remember { mutableStateOf(true) }
        
        LaunchedEffect(Unit) {
            data = loadData()
            loading = false
        }
        
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(data) { item ->
                    ItemRow(item)
                }
            }
        }
    }
}
```

### 设置页面最佳实践

```kotlin
class MyPluginSettings : PluginSettingsProvider {
    @Composable
    override fun SettingsContent() {
        var serverUrl by remember { mutableStateOf("") }
        var port by remember { mutableStateOf(8080) }
        
        Column(Modifier.padding(16.dp).fillMaxWidth()) {
            Text("Connection Settings", style = MaterialTheme.typography.titleMedium)
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(Modifier.height(8.dp))
            
            OutlinedTextField(
                value = port.toString(),
                onValueChange = { port = it.toIntOrNull() ?: 8080 },
                label = { Text("Port") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = { saveSettings(serverUrl, port) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}
```

## 错误处理

### 健壮的错误处理

```kotlin
class MyPlugin : Plugin {
    override fun onEnable() {
        try {
            initializePlugin()
        } catch (e: Exception) {
            context?.logError("Failed to initialize plugin", e)
            // 优雅降级
            enterFallbackMode()
        }
    }
    
    private fun initializePlugin() {
        // 可能失败的操作
    }
    
    private fun enterFallbackMode() {
        // 提供基本功能
    }
}
```

### 网络请求错误处理

```kotlin
suspend fun fetchData(): Result<Data> {
    return try {
        val response = httpClient.get("https://api.example.com/data")
        if (response.status == HttpStatusCode.OK) {
            Result.success(response.body())
        } else {
            Result.failure(Exception("HTTP ${response.status}"))
        }
    } catch (e: Exception) {
        context?.logError("Network request failed", e)
        Result.failure(e)
    }
}
```

## 性能优化

### 异步操作

```kotlin
class MyPlugin : Plugin {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    override fun onEnable() {
        scope.launch {
            val data = loadDataAsync()
            processData(data)
        }
    }
    
    override fun onDisable() {
        scope.cancel()
    }
}
```

### 懒加载

```kotlin
class MyPlugin : Plugin {
    private val heavyResource: Resource by lazy {
        loadHeavyResource()
    }
    
    fun useResource() {
        // 只在第一次使用时加载
        heavyResource.doSomething()
    }
}
```

### 缓存策略

```kotlin
class MyPlugin : Plugin {
    private val cache = mutableMapOf<String, Data>()
    private var lastCacheTime = 0L
    private val cacheTimeout = 5 * 60 * 1000L // 5 minutes
    
    fun getData(key: String): Data? {
        val now = System.currentTimeMillis()
        if (now - lastCacheTime > cacheTimeout) {
            cache.clear()
            lastCacheTime = now
        }
        
        return cache.getOrPut(key) {
            fetchDataFromNetwork(key)
        }
    }
}
```

## 安全考虑

### 输入验证

```kotlin
fun processUserInput(input: String) {
    // 验证输入
    if (input.length > 1000) {
        context?.logError("Input too long")
        return
    }
    
    if (input.contains(Regex("[<>\"']"))) {
        context?.logError("Invalid characters in input")
        return
    }
    
    // 处理输入
    safeProcess(input)
}
```

### 敏感数据处理

```kotlin
class MyPlugin : Plugin {
    private fun saveApiKey(key: String) {
        // 不要明文存储敏感数据
        val encrypted = encrypt(key)
        context?.putString("apiKey", encrypted)
    }
    
    private fun getApiKey(): String? {
        val encrypted = context?.getString("apiKey", "") ?: return null
        return if (encrypted.isNotEmpty()) decrypt(encrypted) else null
    }
}
```

## 版本兼容性

### API 版本检查

```kotlin
class MyPlugin : Plugin {
    override fun onLoad(context: PluginContext) {
        val apiVersion = getApiVersion()
        val minRequired = parseVersion(manifest.minApiVersion)
        
        if (apiVersion < minRequired) {
            context.logError("API version ${manifest.minApiVersion} required, but $apiVersion available")
            return
        }
        
        initializePlugin()
    }
}
```

### 向后兼容

```kotlin
fun loadConfig(): Config {
    val version = context?.getInt("configVersion", 1) ?: 1
    
    return when (version) {
        1 -> loadConfigV1()
        2 -> loadConfigV2()
        else -> Config() // 默认配置
    }
}
```

## 测试

### 单元测试

```kotlin
class MyPluginTest {
    @Test
    fun `test plugin initialization`() {
        val plugin = MyPlugin()
        val mockContext = mockPluginContext()
        
        plugin.onLoad(mockContext)
        
        verify { mockContext.log("Plugin loaded") }
    }
    
    private fun mockPluginContext(): PluginContext {
        return mockk {
            every { pluginId } returns "test-plugin"
            every { pluginDataDir } returns "/tmp/test"
            every { log(any()) } just Runs
        }
    }
}
```

## 发布清单

发布插件前检查：

- [ ] `plugin.json` 信息完整准确
- [ ] 版本号已更新
- [ ] 所有权限已声明
- [ ] 无敏感信息硬编码
- [ ] 错误处理完善
- [ ] 资源已清理
- [ ] 文档已更新
- [ ] 测试通过
