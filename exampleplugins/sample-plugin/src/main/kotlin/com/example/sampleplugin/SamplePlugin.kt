package com.example.sampleplugin

import com.lanrhyme.micyou.plugin.*

class SamplePlugin : Plugin, PluginUIProvider, PluginSettingsProvider {
    
    private var context: PluginContext? = null
    private var message: String = ""
    private var counter: Int = 0
    
    override val manifest = PluginManifest(
        id = "com.example.sample-plugin",
        name = "Sample Plugin",
        version = "1.0.0",
        author = "MicYou Team",
        description = "A sample plugin demonstrating the MicYou Plugin API",
        tags = listOf("utility", "demo"),
        platform = PluginPlatform.DESKTOP,
        minApiVersion = "1.0.0",
        permissions = listOf("storage"),
        mainClass = "com.example.sampleplugin.SamplePlugin"
    )
    
    override val hasMainWindow: Boolean = true
    override val hasDialog: Boolean = true
    
    override fun onLoad(context: PluginContext) {
        this.context = context
        message = context.getString("message", "Hello from Sample Plugin!")
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
    
    fun setMessage(newMessage: String) {
        message = newMessage
        context?.putString("message", newMessage)
    }
    
    fun getMessage(): String = message
    
    fun getCounter(): Int = counter
}
