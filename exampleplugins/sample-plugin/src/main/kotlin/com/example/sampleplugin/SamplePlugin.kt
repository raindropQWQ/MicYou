package com.example.sampleplugin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanrhyme.micyou.plugin.*
import androidx.compose.ui.unit.Dp

class SamplePlugin : Plugin, PluginUIProvider {

    private var context: PluginContext? = null
    private var counter: Int = 0

    override val manifest = PluginManifest(
        id = "com.example.sample-plugin",
        name = "Sample Plugin",
        version = "1.0.0",
        author = "MicYou Team",
        description = "A sample plugin demonstrating the MicYou Plugin API. Shows how to implement plugin lifecycle, UI components, and settings.",
        tags = listOf("utility", "demo"),
        platform = PluginPlatform.DESKTOP,
        minApiVersion = "1.0.0",
        permissions = listOf("storage"),
        mainClass = "com.example.sampleplugin.SamplePlugin"
    )

    override val hasMainWindow: Boolean = true
    
    // 自定义窗口大小和标题
    override val windowWidth: Dp get() = 500.dp
    override val windowHeight: Dp get() = 600.dp
    override val windowTitle: String get() = "Sample Plugin - Demo Window"
    override val windowResizable: Boolean get() = true

    override fun onLoad(context: PluginContext) {
        this.context = context
        counter = context.getInt("counter", 0)
        context.log("SamplePlugin loaded with counter=$counter")
    }

    override fun onEnable() {
        context?.log("SamplePlugin enabled")
    }

    override fun onDisable() {
        context?.log("SamplePlugin disabled")
    }

    override fun onUnload() {
        context?.log("SamplePlugin unloaded")
        context = null
    }

    fun incrementCounter() {
        counter++
        context?.putInt("counter", counter)
        context?.log("Counter incremented to $counter")
    }

    fun getCounter(): Int = counter

    @Composable
    override fun MainWindow(onClose: () -> Unit) {
        var localCounter by remember { mutableStateOf(counter) }
        var userName by remember { mutableStateOf(context?.getString("userName", "") ?: "") }
        var showAboutDialog by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 标题
                Text(
                    text = manifest.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "v${manifest.version}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 计数器卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "计数器",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = localCounter.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                incrementCounter()
                                localCounter = getCounter()
                            }
                        ) {
                            Text("增加计数")
                        }
                    }
                }

                // 用户信息卡片
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "用户设置",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("用户名") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                context?.putString("userName", userName)
                                context?.log("User name saved: $userName")
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("保存")
                        }
                    }
                }

                // 插件信息卡片
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "插件信息",
                            style = MaterialTheme.typography.titleMedium
                        )

                        InfoRow("作者", manifest.author)
                        InfoRow("描述", manifest.description)
                        InfoRow("标签", manifest.tags.joinToString(", "))
                        InfoRow("平台", manifest.platform.name)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = { showAboutDialog = true }) {
                        Text("关于")
                    }

                    Button(onClick = onClose) {
                        Text("关闭")
                    }
                }
            }
        }

        // 关于对话框
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("关于 ${manifest.name}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("版本: ${manifest.version}")
                        Text("作者: ${manifest.author}")
                        Text("描述: ${manifest.description}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "这是一个示例插件，展示了 MicYou 插件系统的功能。",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
    }

    @Composable
    private fun InfoRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
