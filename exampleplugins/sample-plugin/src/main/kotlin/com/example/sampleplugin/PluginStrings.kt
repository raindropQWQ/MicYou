package com.example.sampleplugin

/**
 * 插件本地化字符串管理
 * 支持中文和英文
 */
object PluginStrings {
    private val zhStrings = mapOf(
        // Main Window
        "counter_title" to "计数器",
        "increment_button" to "增加计数",
        "user_settings_title" to "用户设置",
        "username_label" to "用户名",
        "save_button" to "保存",
        "plugin_info_title" to "插件信息",
        "author_label" to "作者",
        "description_label" to "描述",
        "tags_label" to "标签",
        "platform_label" to "平台",
        "about_button" to "关于",
        "close_button" to "关闭",
        "about_title" to "关于",
        "version_label" to "版本",
        "ok_button" to "确定",
        "about_description" to "这是一个示例插件，展示了 MicYou 插件系统的功能，包括生命周期管理、UI 组件、设置和本地化。",
        
        // Settings
        "notification_settings" to "通知设置",
        "enable_notifications" to "启用通知",
        "notification_desc" to "开启后，插件将定期发送通知提醒",
        "display_settings" to "显示设置",
        "max_items" to "最大显示项目数",
        "theme_mode" to "主题模式",
        "theme_light" to "浅色",
        "theme_dark" to "深色",
        "theme_system" to "跟随系统",
        "network_settings" to "网络设置",
        "api_endpoint" to "API 地址",
        "save_address" to "保存地址",
        "refresh_interval" to "刷新间隔",
        "plugin_id" to "插件 ID",
        "reset_defaults" to "恢复默认设置"
    )
    
    private val enStrings = mapOf(
        // Main Window
        "counter_title" to "Counter",
        "increment_button" to "Increment",
        "user_settings_title" to "User Settings",
        "username_label" to "Username",
        "save_button" to "Save",
        "plugin_info_title" to "Plugin Info",
        "author_label" to "Author",
        "description_label" to "Description",
        "tags_label" to "Tags",
        "platform_label" to "Platform",
        "about_button" to "About",
        "close_button" to "Close",
        "about_title" to "About",
        "version_label" to "Version",
        "ok_button" to "OK",
        "about_description" to "This is a sample plugin demonstrating the MicYou plugin system features, including lifecycle management, UI components, settings, and localization.",
        
        // Settings
        "notification_settings" to "Notification Settings",
        "enable_notifications" to "Enable Notifications",
        "notification_desc" to "When enabled, the plugin will send periodic notifications",
        "display_settings" to "Display Settings",
        "max_items" to "Max Items",
        "theme_mode" to "Theme Mode",
        "theme_light" to "Light",
        "theme_dark" to "Dark",
        "theme_system" to "System",
        "network_settings" to "Network Settings",
        "api_endpoint" to "API Endpoint",
        "save_address" to "Save Address",
        "refresh_interval" to "Refresh Interval",
        "plugin_id" to "Plugin ID",
        "reset_defaults" to "Reset to Defaults"
    )
    
    fun getString(languageCode: String, key: String): String? {
        return when (languageCode) {
            "zh", "zh-CN" -> zhStrings[key]
            "en" -> enStrings[key]
            else -> enStrings[key] // 默认使用英文
        }
    }
}
