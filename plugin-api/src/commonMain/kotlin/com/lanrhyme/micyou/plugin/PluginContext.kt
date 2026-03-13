package com.lanrhyme.micyou.plugin

interface PluginContext {
    val pluginId: String
    val pluginDataDir: String

    /**
     * 插件本地化接口
     * 用于获取插件的本地化字符串
     */
    val localization: PluginLocalization

    /**
     * 应用全局本地化接口
     * 用于获取应用级别的本地化字符串
     */
    val appLocalization: PluginLocalization

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
