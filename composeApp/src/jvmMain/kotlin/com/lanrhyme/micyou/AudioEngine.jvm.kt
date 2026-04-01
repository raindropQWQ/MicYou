package com.lanrhyme.micyou

import com.lanrhyme.micyou.audio.AudioOutputManager
import com.lanrhyme.micyou.audio.AudioProcessorPipeline
import com.lanrhyme.micyou.network.NetworkServer
import com.lanrhyme.micyou.platform.AdbManager
import com.lanrhyme.micyou.platform.PlatformInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.sqrt

actual class AudioEngine actual constructor() {
    private val _state = MutableStateFlow(StreamState.Idle)
    actual val streamState: Flow<StreamState> = _state
    private val _audioLevels = MutableStateFlow(0f)
    actual val audioLevels: Flow<Float> = _audioLevels
    private val _lastError = MutableStateFlow<String?>(null)
    actual val lastError: Flow<String?> = _lastError
    
    private val _isMuted = MutableStateFlow(false)
    actual val isMuted: Flow<Boolean> = _isMuted
    
    private val _pluginSyncReceived = MutableStateFlow<PluginSyncMessage?>(null)
    val pluginSyncReceived: Flow<PluginSyncMessage?> = _pluginSyncReceived
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null
    private var audioProcessingJob: Job? = null
    private val startStopMutex = Mutex()
    
    private val audioOutputManager = AudioOutputManager()
    private val audioPipeline = AudioProcessorPipeline()
    
    private val audioPacketChannel = Channel<AudioPacketMessage>(
        capacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    private val networkServer = NetworkServer(
        onAudioPacketReceived = { audioPacket ->
            processReceivedPacket(audioPacket)
        },
        onMuteStateChanged = { muted ->
            _isMuted.value = muted
        },
        onPluginSyncReceived = { syncMessage ->
            _pluginSyncReceived.value = syncMessage
        }
    )
    
    init {
        scope.launch {
            networkServer.state.collect { newState ->
                if (newState == StreamState.Streaming) {
                    audioPipeline.reset()
                    startAudioProcessing()
                } else if (newState == StreamState.Idle || newState == StreamState.Error) {
                    stopAudioProcessing()
                }
                _state.value = newState
            }
        }
        scope.launch {
            networkServer.lastError.collect { error ->
                if (error != null) {
                    _lastError.value = error
                }
            }
        }
    }
    
    private fun startAudioProcessing() {
        if (audioProcessingJob?.isActive == true) return
        
        audioProcessingJob = scope.launch(Dispatchers.Default) {
            Logger.d("AudioEngine", "音频处理协程已启动")
            while (isActive) {
                try {
                    val audioPacket = audioPacketChannel.receiveCatching().getOrNull() ?: break
                    
                    if (!audioOutputManager.init(audioPacket.sampleRate, audioPacket.channelCount)) {
                        Logger.e("AudioEngine", "初始化音频输出失败")
                        continue
                    }

                    val queuedMs = audioOutputManager.getQueuedDurationMs()
                    
                    val processedBuffer = audioPipeline.process(
                        inputBuffer = audioPacket.buffer,
                        audioFormat = audioPacket.audioFormat,
                        channelCount = audioPacket.channelCount,
                        queuedDurationMs = queuedMs
                    )

                    if (processedBuffer != null) {
                        audioOutputManager.write(processedBuffer, 0, processedBuffer.size)

                        val rms = calculateRMS(processedBuffer)
                        _audioLevels.value = rms
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.e("AudioEngine", "音频处理错误", e)
                }
            }
            Logger.d("AudioEngine", "音频处理协程已停止")
        }
    }
    
    private fun stopAudioProcessing() {
        audioProcessingJob?.cancel()
        audioProcessingJob = null
        while (audioPacketChannel.tryReceive().isSuccess) {
        }
    }

    actual val installProgress: Flow<String?> = VirtualAudioDeviceManager.installProgress
    
    actual suspend fun installDriver() {
        VirtualAudioDeviceManager.installVirtualDevice()
    }
    
    actual fun updateConfig(
        enableNS: Boolean,
        nsType: NoiseReductionType,
        enableAGC: Boolean,
        agcTargetLevel: Int,
        enableVAD: Boolean,
        vadThreshold: Int,
        enableDereverb: Boolean,
        dereverbLevel: Float,
        amplification: Float
    ) {
        audioPipeline.updateConfig(
            enableNS, nsType, enableAGC, agcTargetLevel,
            enableVAD, vadThreshold, enableDereverb, dereverbLevel,
            amplification
        )
        
        if (System.getProperty("micyou.debugAudioConfig") == "true") {
            Logger.d("AudioEngine", "配置更新: 放大器=$amplification, VAD=$enableVAD ($vadThreshold), AGC=$enableAGC ($agcTargetLevel), NS=$enableNS ($nsType)")
        }
    }

    actual suspend fun start(
        ip: String, 
        port: Int, 
        mode: ConnectionMode, 
        isClient: Boolean,
        sampleRate: SampleRate,
        channelCount: ChannelCount,
        audioFormat: AudioFormat
    ) {
        if (isClient) return 
        Logger.i("AudioEngine", "启动 JVM AudioEngine: 模式=$mode, 端口=$port, 采样率=${sampleRate.value}, 声道=${channelCount.label}, 格式=${audioFormat.label}")
        
        _lastError.value = null
        
        if (mode == ConnectionMode.Usb) {
            Logger.i("AudioEngine", "正在为 USB 模式执行 ADB reverse，端口 $port")
            if (AdbManager.runAdbReverse(port)) {
                Logger.i("AudioEngine", "ADB reverse 成功，USB 隧道已建立")
            } else {
                val errorMsg = "ADB reverse 失败。请确保已安装 ADB 且 Android 设备已连接。\n或者手动运行: adb reverse tcp:$port tcp:$port"
                Logger.e("AudioEngine", errorMsg)
                _lastError.value = errorMsg
                _state.value = StreamState.Error
                return
            }
        }
        
        val jobToJoin = startStopMutex.withLock {
            val currentJob = job
            if (currentJob != null && !currentJob.isCompleted) {
                Logger.w("AudioEngine", "AudioEngine 已在运行，忽略启动请求")
                null
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    networkServer.start(port, mode)
                }.also { job = it }
            }
        }
        jobToJoin?.join()
    }

    private suspend fun processReceivedPacket(audioPacket: AudioPacketMessage) {
        try {
            audioPacketChannel.send(audioPacket)
        } catch (e: Exception) {
            Logger.e("AudioEngine", "发送音频包到处理通道失败", e)
        }
    }
    
    actual suspend fun setMute(muted: Boolean) {
        _isMuted.value = muted
        networkServer.sendMuteState(muted)
    }
    
    suspend fun sendPluginSync(plugins: List<PluginInfoMessage>, platform: String) {
        networkServer.sendPluginSync(plugins, platform)
    }
    
    actual fun setMonitoring(enabled: Boolean) {
        audioOutputManager.setMonitoring(enabled)
    }

    actual fun setStreamingNotificationEnabled(enabled: Boolean) {
    }

    actual fun setAudioSource(sourceName: String) {
        // JVM 端不支持音频源选择
    }

    actual fun stop() {
         try {
             job?.cancel()
             job = null
             runBlocking {
                 networkServer.stop()
             }
             _lastError.value = null
             _state.value = StreamState.Idle
         } catch (e: Exception) {
             Logger.e("AudioEngine", "Error stopping audio engine: ${e.message}", e)
         } finally {
             audioOutputManager.release()
             audioPipeline.release()
         }
    }
    
    private fun calculateRMS(buffer: ByteArray): Float {
        var sum = 0.0
        var count = 0
        var i = 0
        while (i + 1 < buffer.size) {
            val lo = buffer[i].toInt() and 0xFF
            val hi = buffer[i + 1].toInt()
            val sample = (hi shl 8) or lo
            val normalized = sample / 32768.0
            sum += normalized * normalized
            count++
            i += 2
        }
        return if (count > 0) sqrt(sum / count.toDouble()).toFloat() else 0f
    }
}
