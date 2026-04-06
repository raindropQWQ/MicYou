## OVERVIEW
Shared UI and logic layer in the common module focusing on ViewModels, App.kt composition, localization, platform abstraction, and the AudioEngine contract.

## WHERE TO LOOK
- App.kt: root Composable, theme setup, global dialogs, and locale-aware styling hooks.
- MainViewModel.kt: central facade coordinating Settings, AudioStream, Localization, and cross-cut concerns.
- Localization.kt: AppStrings/LocalAppStrings, language switching, and string resolution.
- Platform.kt: platform abstraction (Platform, Logger) used by shared code and injected into ViewModels.
- AudioEngine.kt: expect class defining the cross-platform audio interface; concrete implementations reside in androidMain and jvmMain.
- AudioStreamViewModel.kt: orchestrates connection modes, config, and stream lifecycle, using AudioEngine for audio I/O.
- App composition patterns: App.kt composes theming, localization, and navigation scaffolding with CompositionLocal handles.
- Shared state types: StateFlow-based UI state exposed by ViewModels for predictable recompositions.
- LocalAppStrings: accessible via CompositionLocal for localizing UI.
- AppStrings: central repository of translatable texts used by Localization.
- Testing: ViewModel and localization tests live alongside modules in test sources.

## ANTI-PATTERNS
- Do not place platform-specific APIs in the common module. Keep Android/Desktop logic in their respective actual modules.
- Avoid direct Android/JVM calls from ViewModels or UI. Always route through AudioEngine or Platform abstractions.
- Do not hardcode strings. Use Localization and AppStrings to support multiple locales.
- Do not perform heavy I/O or network work on the main thread from shared code. Use coroutines and proper dispatchers.
- Do not couple UI state to business logic. Keep ViewModel state as clean data and expose via StateFlow.
- Avoid circular dependencies between ViewModels and domain/services.
- Do not bypass the AudioEngine contract; ensure a consistent interface for audio operations across platforms.
- Do not duplicate UI or localization resources in multiple modules; centralize in common where possible.
- Use the logger via Platform.Logger; avoid println-based debugging in shared code.
- Prefer dependency injection patterns to supply Platform and AudioEngine implementations in tests.
- Keep AGENTS.md in sync with code to minimize drift.

// End of AGENTS.md
