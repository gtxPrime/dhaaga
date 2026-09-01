package com.dhaaga.app.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object AppTtsManager {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var pendingSpeakText: String? = null
    private var pendingLanguage: String = "hi"

    fun init(context: Context) {
        if (tts != null) return
        val appContext = context.applicationContext
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f) // Standard natural conversational speed
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })

                // Warm up locales
                try {
                    tts?.language = Locale("hi", "IN")
                } catch (_: Exception) {}

                _isReady.value = true

                // If a speech request was waiting during warm-up, trigger it immediately
                pendingSpeakText?.let { text ->
                    speak(text, pendingLanguage)
                    pendingSpeakText = null
                }
            }
        }
    }

    fun speak(text: String, languageCode: String = "hi") {
        if (text.isBlank()) return
        if (!isInitialized || tts == null) {
            pendingSpeakText = text
            pendingLanguage = languageCode
            return
        }

        try {
            val loc = if (languageCode == "hi") Locale("hi", "IN") else Locale.US
            tts?.language = loc
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(1.0f)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "onboarding_prompt_${System.currentTimeMillis()}")
            _isSpeaking.value = true
        } catch (_: Exception) {
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
        pendingSpeakText = null
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        isInitialized = false
        _isReady.value = false
        _isSpeaking.value = false
    }
}
