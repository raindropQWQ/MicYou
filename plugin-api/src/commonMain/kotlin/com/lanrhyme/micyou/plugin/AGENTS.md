## OVERVIEW
Defines the core plugin API used by hosts and plugins for load, enable, disable lifecycle and audio processing.

## WHERE TO LOOK
- Plugin interface: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/Plugin.kt
- PluginHost: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/PluginHost.kt
- PluginManifest: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/PluginManifest.kt
- PluginInfo: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/PluginInfo.kt
- AudioEffectPlugin: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/AudioEffectPlugin.kt
- AudioEffectProvider: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/AudioEffectProvider.kt
- PluginDataChannel: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/PluginDataChannel.kt
- PluginContext: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/PluginContext.kt
- PluginLocalization: plugin-api/src/commonMain/kotlin/com/lanrhyme/micyou/plugin/PluginLocalization.kt

## CONVENTIONS
- Lifecycle pattern to implement: onLoad(context: PluginContext), onEnable(): Boolean, onDisable(): Boolean
- AudioEffectPlugin.process() signature should be stable across hosts, e.g.:
  process(input: AudioBuffer, context: PluginContext): AudioBuffer
- API types like PluginInfo, PluginManifest, PluginContext, and PluginLocalization are defined here and used by hosts and plugins without platform-specific imports.
- Prefer immutable data classes for information holders like PluginInfo and PluginManifest.
- Avoid leaking host details; interact with the host only through PluginHost and PluginContext.
- Use the provided PluginLocalization for any user-facing strings.

## ANTI-PATTERNS
- Do not bypass the lifecycle by performing long I/O in onLoad; defer work to onEnable or background tasks.
- Do not keep strong, direct references to host UI components or platform-specific objects in plugin code.
- Do not expose mutable state in public API data structures; prefer val immutable properties.
- Do not couple plugins to a concrete host implementation; rely on PluginHost and PluginContext abstractions.
- Do not hardcode resources; fetch them through PluginContext-localized access or PluginManifest declarations.
