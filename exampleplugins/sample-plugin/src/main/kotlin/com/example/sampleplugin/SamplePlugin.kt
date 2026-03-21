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

class SamplePlugin : Plugin, PluginUIProvider, PluginSettingsProvider, PluginLocalizationProvider {

    private var context: PluginContext? = null
    private var counter: Int = 0

    override val manifest = PluginManifest(
        id = "com.example.sample-plugin",
        name = "Sample Plugin",
        version = "1.0.0",
        author = "MicYou Team",
        description = "A sample plugin demonstrating the MicYou Plugin API. Shows how to implement plugin lifecycle, UI components, settings, and localization.",
        tags = listOf("utility", "demo"),
        platform = PluginPlatform.DESKTOP,
        minApiVersion = "1.0.0",
        mainClass = "com.example.sampleplugin.SamplePlugin"
    )

    override val hasMainWindow: Boolean = true
    
    override val windowWidth: Dp get() = 500.dp
    override val windowHeight: Dp get() = 600.dp
    override val windowTitle: String get() = "Sample Plugin - Demo Window"
    override val windowResizable: Boolean get() = true
    
    override val mobileUIMode: MobileUIMode get() = MobileUIMode.NewScreen

    override fun getLocalizedString(languageCode: String, key: String): String? {
        return PluginStrings.getString(languageCode, key)
    }

    override fun getSupportedLanguages(): List<String> {
        return listOf("zh", "en")
    }

    override fun onLoad(context: PluginContext) {
        this.context = context
        counter = context.getInt("counter", 0)
        context.log("SamplePlugin loaded with counter=$counter")
        
        val host = context.host
        context.log("Platform: ${host.platform.name}, isDesktop: ${host.platform.isDesktop}")
    }

    override fun onEnable() {
        context?.log("SamplePlugin enabled")
        context?.host?.streamState?.value?.let { state ->
            context?.log("Current stream state: $state")
        }
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
        val strings = context?.localization
        var localCounter by remember { mutableStateOf(counter) }
        var userName by remember { mutableStateOf(context?.getString("userName", "") ?: "") }
        var showAboutDialog by remember { mutableStateOf(false) }
        
        val host = context?.host
        val streamState by host?.streamState?.collectAsState() ?: remember { mutableStateOf(StreamState.Idle) }
        val audioLevel by host?.audioLevels?.collectAsState() ?: remember { mutableStateOf(0f) }
        val isMuted by host?.isMuted?.collectAsState() ?: remember { mutableStateOf(false) }

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
                            text = strings?.getString("counter_title", "Counter") ?: "Counter",
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
                            Text(strings?.getString("increment_button", "Increment") ?: "Increment")
                        }
                    }
                }

                host?.let { h ->
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
                                text = "Host Status",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Stream State:")
                                Text(streamState.name)
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Audio Level:")
                                Text("%.2f".format(audioLevel))
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Muted:")
                                Text(if (isMuted) "Yes" else "No")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        h.showSnackbar("Hello from ${manifest.name}!")
                                    }
                                ) {
                                    Text("Show Snackbar")
                                }
                                
                                Button(
                                    onClick = {
                                        h.showNotification(manifest.name, "This is a notification from the plugin!")
                                    }
                                ) {
                                    Text("Notify")
                                }
                            }
                        }
                    }
                }

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
                            text = strings?.getString("user_settings_title", "User Settings") ?: "User Settings",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text(strings?.getString("username_label", "Username") ?: "Username") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                context?.putString("userName", userName)
                                context?.log("User name saved: $userName")
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(strings?.getString("save_button", "Save") ?: "Save")
                        }
                    }
                }

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
                            text = strings?.getString("plugin_info_title", "Plugin Info") ?: "Plugin Info",
                            style = MaterialTheme.typography.titleMedium
                        )

                        InfoRow(strings?.getString("author_label", "Author") ?: "Author", manifest.author)
                        InfoRow(strings?.getString("description_label", "Description") ?: "Description", manifest.description)
                        InfoRow(strings?.getString("tags_label", "Tags") ?: "Tags", manifest.tags.joinToString(", "))
                        InfoRow(strings?.getString("platform_label", "Platform") ?: "Platform", manifest.platform.name)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = { showAboutDialog = true }) {
                        Text(strings?.getString("about_button", "About") ?: "About")
                    }

                    Button(onClick = onClose) {
                        Text(strings?.getString("close_button", "Close") ?: "Close")
                    }
                }
            }
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text(strings?.getString("about_title", "About")?.let { "$it ${manifest.name}" } ?: "About ${manifest.name}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${strings?.getString("version_label", "Version")}: ${manifest.version}")
                        Text("${strings?.getString("author_label", "Author")}: ${manifest.author}")
                        Text("${strings?.getString("description_label", "Description")}: ${manifest.description}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            strings?.getString("about_description", "This is a sample plugin demonstrating the MicYou plugin system.") 
                                ?: "This is a sample plugin demonstrating the MicYou plugin system.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text(strings?.getString("ok_button", "OK") ?: "OK")
                    }
                }
            )
        }
    }

    @Composable
    override fun SettingsContent() {
        val strings = context?.localization
        var enableNotifications by remember { 
            mutableStateOf(context?.getBoolean("enableNotifications", true) ?: true) 
        }
        var maxItems by remember { 
            mutableStateOf(context?.getInt("maxItems", 10) ?: 10) 
        }
        var theme by remember { 
            mutableStateOf(context?.getString("theme", "system") ?: "system") 
        }
        var apiEndpoint by remember { 
            mutableStateOf(context?.getString("apiEndpoint", "https://api.example.com") ?: "https://api.example.com") 
        }
        var refreshInterval by remember { 
            mutableStateOf(context?.getFloat("refreshInterval", 5.0f) ?: 5.0f) 
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        strings?.getString("notification_settings", "Notification Settings") ?: "Notification Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(strings?.getString("enable_notifications", "Enable Notifications") ?: "Enable Notifications")
                        Switch(
                            checked = enableNotifications,
                            onCheckedChange = { 
                                enableNotifications = it
                                context?.putBoolean("enableNotifications", it)
                            }
                        )
                    }
                    
                    Text(
                        strings?.getString("notification_desc", "When enabled, the plugin will send periodic notifications") 
                            ?: "When enabled, the plugin will send periodic notifications",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        strings?.getString("display_settings", "Display Settings") ?: "Display Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Text(strings?.getString("max_items", "Max Items")?.let { "$it: $maxItems" } ?: "Max Items: $maxItems")
                    Slider(
                        value = maxItems.toFloat(),
                        onValueChange = { 
                            maxItems = it.toInt()
                            context?.putInt("maxItems", maxItems)
                        },
                        valueRange = 1f..50f,
                        steps = 49
                    )
                    
                    HorizontalDivider()
                    
                    Text(strings?.getString("theme_mode", "Theme Mode") ?: "Theme Mode")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = theme == "light",
                            onClick = { 
                                theme = "light"
                                context?.putString("theme", theme)
                            },
                            label = { Text(strings?.getString("theme_light", "Light") ?: "Light") }
                        )
                        FilterChip(
                            selected = theme == "dark",
                            onClick = { 
                                theme = "dark"
                                context?.putString("theme", theme)
                            },
                            label = { Text(strings?.getString("theme_dark", "Dark") ?: "Dark") }
                        )
                        FilterChip(
                            selected = theme == "system",
                            onClick = { 
                                theme = "system"
                                context?.putString("theme", theme)
                            },
                            label = { Text(strings?.getString("theme_system", "System") ?: "System") }
                        )
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        strings?.getString("network_settings", "Network Settings") ?: "Network Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = apiEndpoint,
                        onValueChange = { apiEndpoint = it },
                        label = { Text(strings?.getString("api_endpoint", "API Endpoint") ?: "API Endpoint") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = { context?.putString("apiEndpoint", apiEndpoint) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(strings?.getString("save_address", "Save Address") ?: "Save Address")
                    }
                    
                    HorizontalDivider()
                    
                    Text(strings?.getString("refresh_interval", "Refresh Interval")?.let { "$it: ${refreshInterval.toInt()}s" } 
                        ?: "Refresh Interval: ${refreshInterval.toInt()}s")
                    Slider(
                        value = refreshInterval,
                        onValueChange = { 
                            refreshInterval = it
                            context?.putFloat("refreshInterval", it)
                        },
                        valueRange = 1f..60f,
                        steps = 59
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        strings?.getString("about_title", "About") ?: "About",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("${strings?.getString("version_label", "Version")}: ${manifest.version}")
                    Text("${strings?.getString("plugin_id", "Plugin ID")}: ${manifest.id}")
                    
                    TextButton(
                        onClick = {
                            enableNotifications = true
                            maxItems = 10
                            theme = "system"
                            apiEndpoint = "https://api.example.com"
                            refreshInterval = 5.0f
                            
                            context?.putBoolean("enableNotifications", true)
                            context?.putInt("maxItems", 10)
                            context?.putString("theme", "system")
                            context?.putString("apiEndpoint", "https://api.example.com")
                            context?.putFloat("refreshInterval", 5.0f)
                        },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text(strings?.getString("reset_defaults", "Reset to Defaults") ?: "Reset to Defaults")
                    }
                }
            }
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
