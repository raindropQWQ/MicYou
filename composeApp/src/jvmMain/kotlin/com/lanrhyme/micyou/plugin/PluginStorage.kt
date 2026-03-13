package com.lanrhyme.micyou.plugin

import com.lanrhyme.micyou.Logger
import java.io.File
import java.util.prefs.Preferences

class PluginStorage(
    override val pluginId: String,
    private val dataDir: File,
    private val pluginInstallDir: File,
    private val appLanguageProvider: () -> String = { "en" },
    private val appStringProvider: ((String) -> String)? = null
) : PluginContext {
    private val prefs = Preferences.userRoot().node("micyou/plugins/$pluginId")

    override val pluginDataDir: String get() = dataDir.absolutePath

    /**
     * 插件本地化接口
     */
    override val localization: PluginLocalization by lazy {
        PluginLocalizationImpl(pluginId, pluginInstallDir, appLanguageProvider)
    }

    /**
     * 应用全局本地化接口
     */
    override val appLocalization: PluginLocalization by lazy {
        if (appStringProvider != null) {
            AppPluginLocalization(appStringProvider, appLanguageProvider)
        } else {
            // 如果没有提供应用字符串提供者，使用插件本地化作为回退
            localization
        }
    }

    override fun getString(key: String, defaultValue: String): String = prefs.get(key, defaultValue)
    override fun putString(key: String, value: String) = prefs.put(key, value)
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    override fun putBoolean(key: String, value: Boolean) = prefs.putBoolean(key, value)
    override fun getInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)
    override fun putInt(key: String, value: Int) = prefs.putInt(key, value)
    override fun getFloat(key: String, defaultValue: Float): Float = prefs.getFloat(key, defaultValue)
    override fun putFloat(key: String, value: Float) = prefs.putFloat(key, value)

    override fun log(message: String) {
        Logger.i("Plugin-$pluginId", message)
    }

    override fun logError(message: String, throwable: Throwable?) {
        Logger.e("Plugin-$pluginId", message, throwable)
    }
}
