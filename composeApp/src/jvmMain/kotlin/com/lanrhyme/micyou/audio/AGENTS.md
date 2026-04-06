OVERVIEW
Audio processing chain domain for desktop JVM module, detailing the audio effects pipeline.

WHERE TO LOOK
- AudioProcessorPipeline.kt — core chain that wires effects together and streams frames through process() calls.
- NoiseReducer.kt — spectral/subband noise suppression stage.
- AGCEffect.kt — adaptive gain/compression or denoise variant used in the chain.
- VADEffect.kt — Voice Activity Detection based gate/ducking or related masking stage.
- DereverbEffect.kt — dereverberation stage to reduce room reverberation artifacts.
- AmplifierEffect.kt — final gain/leveling stage with headroom handling.
- UlunasProcessor.kt — ONNX-backed neural-processor for advanced denoise/dereverb tasks.
- ResamplerEffect.kt — sample-rate conversion and channel-preserving resampling.

Note: These components are wired in an effect chain pattern; each implements a process() step and passes the result to the next stage.

CONVENTIONS
- Pattern: effect chain. Each effect exposes a process(...) method and the pipeline composes them in sequence.
- Typical process() signature patterns observed:
  - fun process(input: FloatArray, sampleRate: Int, channels: Int): FloatArray
  - or fun process(frame: AudioFrame): AudioFrame
  The exact type alias may vary per module, but the contract is a single input buffer and a produced output buffer.
- Buffer reuse: hot-path loops aim to minimize allocations; reuse preallocated buffers where possible.
- ONNX integration: UlunasProcessor uses ONNX Runtime; manage native resources and dispose when the pipeline ends.
- Resampling: ResamplerEffect must preserve channel order and minimize latency while adjusting sample rate.
- Threading: audio processing runs on a dedicated thread; avoid blocking I/O or filesystem calls inside process().
- Logging: use the project’s Logger abstraction for any diagnostic messages from the pipeline.
- Testing: unit tests per stage are encouraged; validate boundary conditions (empty buffers, multi-channel, SR changes).
- Lifecycle: ensure proper release of native resources and clean teardown of UlunasProcessor when the pipeline shuts down.

ANTI-PATTERNS
- Do not bypass the chain by short-circuiting to a fixed output; each stage should contribute in order.
- Do not mutate shared buffers without synchronization; prefer immutable passes or clearly scoped in/out buffers.
- Avoid per-sample allocations inside hot loops; where possible, operate on blocks or reuse temporaries.
- Do not ignore sample rate or channel count changes mid-stream; handle reconfiguration gracefully.
- Do not leak native resources from UlunasProcessor; implement proper close() or dispose() hooks and call them.
- Avoid coupling to UI thread or blocking I/O in process(); keep CPU-bound processing on a dedicated audio thread.
- Do not mix responsibilities across stages; each class should encapsulate a single processing concern (noise, dereverb, gain, etc.).
