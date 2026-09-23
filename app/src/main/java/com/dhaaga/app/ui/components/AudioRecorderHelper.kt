package com.dhaaga.app.ui.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

/**
 * AudioRecorderHelper — Wraps Android's native SpeechRecognizer to transcribe
 * artisan speech in real time across regional Indian languages.
 */
class AudioRecorderHelper(
    private val context: Context,
    private val onPartialResult: (String) -> Unit = {},
    private val onFinalResult: (String) -> Unit = {},
    private val onError: (String) -> Unit = {},
    private val onRmsUpdate: (Float) -> Unit = {}
) {
    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean = false
        private set

    companion object {
        const val TAG = "AudioRecorderHelper"
    }

    fun startListening(languageCode: String = "hi-IN") {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition is not available on this device.")
            return
        }

        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                        Log.d(TAG, "Ready for speech in language: $languageCode")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(TAG, "Artisan began speaking")
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        try {
                            onRmsUpdate(rmsdB)
                        } catch (_: Exception) {}
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        Log.d(TAG, "End of speech detected")
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error. Please check your internet or type your note below."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Speech network timed out. Please try speaking again or type your note below."
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak close to the microphone."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Microphone busy. Please wait a moment and tap again."
                            SpeechRecognizer.ERROR_SERVER -> "Speech recognition server temporarily busy. Please try again."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Microphone timed out. Please tap and speak immediately."
                            else -> "Recognition error ($error). Please speak again or type your note."
                        }
                        Log.w(TAG, "SpeechRecognizer error: $errorMsg")
                        onError(errorMsg)
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        Log.i(TAG, "Final recognized speech: $text")
                        onFinalResult(text)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        onPartialResult(text)
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                // Generous silence intervals to prevent premature timeouts
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3500L)
            }

            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start speech recognition: ${e.message}", e)
            isListening = false
            onError(e.message ?: "Failed to start speech recognition")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.w(TAG, "Error cleaning up speech recognizer: ${e.message}")
        } finally {
            speechRecognizer = null
            isListening = false
        }
    }
}
