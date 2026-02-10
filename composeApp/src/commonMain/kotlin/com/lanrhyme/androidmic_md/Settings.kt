package com.lanrhyme.androidmic_md

interface Settings {
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    fun getLong(key: String, defaultValue: Long): Long
    fun putLong(key: String, value: Long)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}

expect object SettingsFactory {
    fun getSettings(): Settings
}
