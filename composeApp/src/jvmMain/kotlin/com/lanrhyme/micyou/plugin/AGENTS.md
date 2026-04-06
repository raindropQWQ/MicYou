# AGENTS for desktop plugin package

Overview: Desktop plugin subsystem that loads, isolates, and hosts plugins in a secured JVM environment.

## WHERE TO LOOK
- PluginManager.kt — coordinates discovery, loading, unloading, and lifecycle state of plugins.
- PluginClassLoader.kt — provides a per-plugin class loader to ensure isolation between plugins and host.
- PluginSecurityManager.kt — performs security validation before loading a plugin and enforces permission boundaries.
- PluginHostImpl.kt — concrete host implementation that exposes host services and hooks for plugins.
- PluginStorage.kt — on-disk storage for plugin artifacts, metadata, and installation layout.
- PluginDataChannelImpl.kt — IPC-like channel for host<->plugin communication, requests, and events.
- Related artifacts: PluginManifest, Plugin, and PluginHost interfaces in plugin-api as context for integration points.

## CONVENTIONS
- Always validate before loading: every plugin must pass PluginSecurityManager checks prior to being loaded by its PluginClassLoader.
- Isolation by design: each plugin runs in its own class loader instance to prevent cross-plugin contamination and class conflicts.
- Stable host surface: PluginHostImpl provides a controlled API surface to plugins, avoiding leakage of host internals.
- Clear lifecycle: loading, initialization, activation, and shutdown follow a deterministic lifecycle with explicit state transitions.
- Metadata-driven: PluginStorage maintains versioning, origin, and artifact paths to support reproducible loads and updates.
- Communication discipline: PluginDataChannelImpl enforces defined message formats and timeouts to prevent blocking or runaway calls.
- Observability: use standard Logger facilities for instrumentation during plugin load, unload, and runtime events.

## ANTI-PATTERNS
- Do not load plugins from the host classpath or share a single class loader across plugins; maintain per-plugin isolation.
- Do not bypass PluginSecurityManager checks or trust plugins by default; enforce a least-privilege model.
- Do not expose internal host classes to plugins beyond the PluginHost surface; keep clear API boundaries.
- Do not retain strong references to plugin instances after shutdown; allow GC and release resources promptly.
- Do not perform heavy work on the host UI or main threads during plugin initialization; delegate to executor services.
- Do not mix plugin artifacts with core host assets in a way that defeats versioning or update semantics.

This file serves as a high-level map for the JVM-based plugin stack. It emphasizes security before load, strict isolation, and clean host-plugin interfaces.
