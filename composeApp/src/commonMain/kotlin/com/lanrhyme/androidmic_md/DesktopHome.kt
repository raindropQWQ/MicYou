package com.lanrhyme.androidmic_md

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DesktopHome(
    viewModel: MainViewModel,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val audioLevel by viewModel.audioLevels.collectAsState(initial = 0f)
    val platform = remember { getPlatform() }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 左侧：状态与信息
            Card(
                modifier = Modifier.weight(1.2f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("AndroidMic Desktop", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SelectionContainer {
                         Text("本机 IP: ${platform.ipAddress}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("监听端口: ${state.port}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val statusText = when(state.streamState) {
                        StreamState.Idle -> "空闲"
                        StreamState.Connecting -> "连接中..."
                        StreamState.Streaming -> "正在串流"
                        StreamState.Error -> "错误"
                    }
                    Text("状态: $statusText", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(state.errorMessage ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // 中间：控制与可视化
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val isRunning = state.streamState == StreamState.Streaming
                    
                    // 背景可视化 (简单模拟)
                    if (isRunning) {
                        CircularProgressIndicator(
                            progress = { audioLevel },
                            modifier = Modifier.fillMaxSize(0.8f),
                            strokeWidth = 8.dp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            trackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
                        )
                    }

                    // 开关按钮
                    IconButton(
                        onClick = { viewModel.toggleStream() },
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            if (isRunning) Icons.Filled.MicOff else Icons.Filled.Mic,
                            contentDescription = "Toggle Stream",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // 右侧：系统操作
            Column(
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 窗口控制
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onMinimize, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Minimize, contentDescription = "Minimize", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 设置
                FilledTonalIconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        }
    }
}
