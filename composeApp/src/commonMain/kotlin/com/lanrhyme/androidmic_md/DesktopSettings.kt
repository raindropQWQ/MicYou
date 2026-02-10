package com.lanrhyme.androidmic_md

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopSettings(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    // 预设种子颜色
    val seedColors = listOf(
        0xFF6750A4L, // Purple (Default)
        0xFFB3261EL, // Red
        0xFFFBC02DL, // Yellow
        0xFF388E3CL, // Green
        0xFF006C51L, // Teal
        0xFF2196F3L, // Blue
        0xFFE91E63L  // Pink
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("设置", style = MaterialTheme.typography.headlineSmall)
            
            HorizontalDivider()
            
            // 主题模式
            Text("主题模式", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = state.themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) },
                        label = { Text(mode.name) }
                    )
                }
            }
            
            HorizontalDivider()
            
            // 主题颜色
            Text("主题颜色", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                seedColors.forEach { colorHex ->
                    val color = Color(colorHex.toInt())
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape)
                            .clickable { viewModel.setSeedColor(colorHex) }
                            .then(
                                if (state.seedColor == colorHex) {
                                    Modifier.padding(2.dp).background(MaterialTheme.colorScheme.onSurface, CircleShape).padding(2.dp).background(color, CircleShape)
                                } else Modifier
                            )
                    )
                }
            }
            
            HorizontalDivider()
            
            // 端口设置
            Text("连接设置", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.port,
                onValueChange = { viewModel.setPort(it) },
                label = { Text("监听端口") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onClose,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("关闭")
            }
        }
    }
}
