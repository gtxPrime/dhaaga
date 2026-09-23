package com.dhaaga.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Generation Mode: Cloud API (Magic Hour) vs On-Device (ML Kit)
 */
enum class ImageGenerationMode(val displayName: String, val subtitle: String) {
    API("Cloud API Model (Magic Hour)", "Ultra-photorealistic Qwen Edit with multiple backup API keys & cloud rendering"),
    ON_DEVICE("On-Device (ML Kit Studio Engine)", "Fast local subject extraction and pure white cyclorama studio rendering")
}

/**
 * Free/Tier compatible models supported by Magic Hour AI Image Editor
 */
data class MagicHourModelInfo(
    val id: String,
    val name: String,
    val costTag: String,
    val description: String
)

/**
 * MagicHourService:
 * Implements the Magic Hour image editing API pipeline corresponding to F:\API testing\edit_image.py:
 * 1. Uploads original image via pre-signed URL (POST /v1/files/upload-urls -> PUT bytes).
 * 2. Generates edited image (POST /v1/ai-image-editor) using qwen-edit (default) or other selected tier models.
 * 3. Polls project status (GET /v1/image-projects/{id}) until completed.
 * 4. Downloads high-resolution rendered result.
 *
 * Includes multi-key pool with automatic rotation on HTTP 402 / low credit errors.
 */
object MagicHourService {

    private const val TAG = "MagicHourService"
    private const val PREFS_NAME = "dhaaga_ai_prefs"

    private const val KEY_IMAGE_GEN_MODE = "image_gen_mode"
    private const val KEY_MAGIC_HOUR_KEYS = "magichour_api_keys"
    private const val KEY_ACTIVE_KEY_INDEX = "magichour_active_key_index"
    private const val KEY_SELECTED_MODEL = "magichour_model"

    const val DEFAULT_MODEL = "qwen-edit"

    val AVAILABLE_MODELS = listOf(
        MagicHourModelInfo(
            id = "qwen-edit",
            name = "Qwen Edit (Recommended)",
            costTag = "10 credits",
            description = "High-precision e-commerce product staging, shadow casting & lighting"
        ),
        MagicHourModelInfo(
            id = "flux-2-klein",
            name = "Flux 2 Klein",
            costTag = "5 credits",
            description = "Lightweight & ultra-fast product generation"
        ),
        MagicHourModelInfo(
            id = "krea-2",
            name = "Krea 2",
            costTag = "10 credits",
            description = "Artistic commercial product enhancements & vibrant textures"
        )
    )

    // Bundled fallback & rotation keys provided by user (9 multi-key rotation pool)
    val BUNDLED_KEYS = listOf(
        "mhk_live_z6YkdnrbgRuC83TopK4qQKmqBNY5WEXGwoAqC4n0fE9mfl1EmWFiIXGgTwejlAtVM87trcPVKsE7wZGr",
        "mhk_live_EVlYixPhd9JtaxnRa4vHj6D2qUAiUPZ8L7LfMohiQrt2WiZc8NhniwWvm5KOVuDOO74Cui3zAPwDcaDY",
        "mhk_live_apj1thMZ0KzXH5sWDL6EQmhEcpBNPjNefkr16kLV3NUNme7G6CWcHi0qO2pHBuHOcijELbZ3RmyP6QwV",
        "mhk_live_QCON5RuSGa0pXvkzlVsRH92ydhReAhqJXgO5lvqSIo4jGi0ir0R277QlNyRgOlQuViDCgDdm0YaomRpi",
        "mhk_live_nMaN9olO1xzn8R7x0C4abGsTXqMg7FeUsxi7kKnCA1v68vBmj1IELxuNYjw45WwhIFIoSwKVCxdVsuX5",
        "mhk_live_jSAwYwbIg89EOjyfhHvmnIWAmBm7MxV7eeQK7QczChUx8i2Jkbau4cZsmCT7eIxgm5QXpEXUCVEj9V9x",
        "mhk_live_Ui32NRruFQwg5gZO8IH2Flldi2CiTL9KfvESz3klK0TiWLqJGNrVSOgsEpszb8rsCRXcY98GtaVMWOZf",
        "mhk_live_T2yfqSXsvlujHE4kL6Rv6nhIFkVCpr1vxxu33Enju6pIPhSirVOrjSnq7mTtJymDp30REdW7YGybkuPe",
        "mhk_live_WRaTUSQmyxAZEvzgCglqxorcvxiIL2rI2FdQ5EM41JS2cRc0iTSEHsydp0c0tHjW4HmLuvN8QqDvSO1H",
        "mhk_live_q4uIz0p1iRKb79SgcE2wamtZka1tkmWEiLjTNAUCd25u649Rg3ENqpBWKgKS690vTB4nVGBwugPWmiHf"
    )

    // Base API URL
    private const val BASE_URL = "https://api.magichour.ai"

    // Default E-commerce Commercial Hero Listing Prompt
    const val DEFAULT_COMMERCIAL_PROMPT =
        "Cinematic commercial product photo of this brand-new product, pristine flawless finish with subtle sleek shine and studio rim lighting. Seamless pure white background, realistic soft contact drop shadow beneath. Sharp focus, high-end e-commerce hero listing."

    // ------------------------------------------------------------------------
    // PREFERENCES & MULTI-KEY MANAGEMENT
    // ------------------------------------------------------------------------

    fun getGenerationMode(context: Context): ImageGenerationMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val modeStr = prefs.getString(KEY_IMAGE_GEN_MODE, ImageGenerationMode.API.name)
        return try {
            ImageGenerationMode.valueOf(modeStr ?: ImageGenerationMode.API.name)
        } catch (_: Exception) {
            ImageGenerationMode.API
        }
    }

    fun setGenerationMode(context: Context, mode: ImageGenerationMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_IMAGE_GEN_MODE, mode.name).apply()
        Log.i(TAG, "Image generation mode set to: ${mode.name}")
    }

    fun getSelectedModel(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SELECTED_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    }

    fun setSelectedModel(context: Context, modelId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SELECTED_MODEL, modelId.trim()).apply()
        Log.i(TAG, "Selected model updated to: $modelId")
    }

    fun getKeys(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_MAGIC_HOUR_KEYS, null)?.trim()
        if (saved.isNullOrBlank()) {
            // Initialize with bundled keys
            saveKeys(context, BUNDLED_KEYS)
            return BUNDLED_KEYS
        }
        val list = saved.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }
        // Ensure all bundled keys are merged in if newly added
        val merged = (list + BUNDLED_KEYS).distinct()
        if (merged.size > list.size) {
            saveKeys(context, merged)
        }
        return if (merged.isNotEmpty()) merged else BUNDLED_KEYS
    }

    fun saveKeys(context: Context, keys: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val clean = keys.map { it.trim() }.filter { it.isNotBlank() }
        prefs.edit().putString(KEY_MAGIC_HOUR_KEYS, clean.joinToString("\n")).apply()
    }

    fun getActiveKeyIndex(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val idx = prefs.getInt(KEY_ACTIVE_KEY_INDEX, 0)
        val keys = getKeys(context)
        return if (keys.isNotEmpty()) idx.coerceIn(0, keys.size - 1) else 0
    }

    fun getActiveKey(context: Context): String {
        val keys = getKeys(context)
        if (keys.isEmpty()) return ""
        val idx = getActiveKeyIndex(context)
        return keys[idx]
    }

    fun rotateToNextKey(context: Context): String {
        val keys = getKeys(context)
        if (keys.isEmpty()) return ""
        val currentIdx = getActiveKeyIndex(context)
        val nextIdx = (currentIdx + 1) % keys.size
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_ACTIVE_KEY_INDEX, nextIdx).apply()
        val newKey = keys[nextIdx]
        Log.w(TAG, "🔄 Auto-rotated API Key from slot #$currentIdx to slot #$nextIdx (${newKey.take(12)}...)")
        return newKey
    }

    fun resetToDefaultKeys(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_MAGIC_HOUR_KEYS, BUNDLED_KEYS.joinToString("\n"))
            .putInt(KEY_ACTIVE_KEY_INDEX, 0)
            .putString(KEY_SELECTED_MODEL, DEFAULT_MODEL)
            .apply()
        Log.i(TAG, "Magic Hour keys and model reset to defaults")
    }

    // ------------------------------------------------------------------------
    // API PIPELINE (Upload -> Generate -> Poll -> Download)
    // ------------------------------------------------------------------------

    /**
     * Executes the full image edit pipeline with auto-key rotation on HTTP 402 or low-credits errors.
     */
    suspend fun editImage(
        context: Context,
        inputBitmap: Bitmap,
        prompt: String,
        onStatusUpdate: (String) -> Unit = {}
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        val keys = getKeys(context)
        if (keys.isEmpty()) {
            return@withContext Result.failure(Exception("No Magic Hour API keys configured. Please configure keys in Settings."))
        }

        val model = getSelectedModel(context)
        val effectivePrompt = if (prompt.isNotBlank()) prompt else DEFAULT_COMMERCIAL_PROMPT

        // Downsample slightly if extremely large to speed up upload (e.g. max 1600px)
        val uploadBytes = prepareImageBytes(inputBitmap, maxDim = 1600)

        var attempts = 0
        val maxAttempts = keys.size

        while (attempts < maxAttempts) {
            val currentKey = getActiveKey(context)
            val currentSlot = getActiveKeyIndex(context) + 1
            Log.i(TAG, "Attempt ${attempts + 1}/$maxAttempts using key slot #$currentSlot (${currentKey.take(12)}...) with model '$model'")

            try {
                // 1. Request pre-signed upload URL
                onStatusUpdate("Requesting Magic Hour upload URL (Key #$currentSlot)...")
                val (uploadUrl, filePath) = requestUploadUrl(currentKey, extension = "jpg")

                // 2. Upload image bytes directly
                onStatusUpdate("Uploading product image...")
                uploadBytesToStorage(uploadUrl, uploadBytes)

                // 3. Trigger image editing task
                onStatusUpdate("Submitting edit task to $model...")
                val projectId = createEditorProject(
                    apiKey = currentKey,
                    filePath = filePath,
                    prompt = effectivePrompt,
                    model = model
                )

                // 4. Poll for completion
                onStatusUpdate("Magic Hour AI ($model) is rendering...")
                val downloadUrl = pollProjectUntilComplete(currentKey, projectId) { msg ->
                    onStatusUpdate(msg)
                }

                // 5. Download rendered image
                onStatusUpdate("Downloading studio-enhanced photo...")
                val resultBitmap = downloadImageBitmap(downloadUrl)

                return@withContext Result.success(resultBitmap)

            } catch (e: Exception) {
                val errMsg = e.message ?: ""
                val isCreditOrUpgradeError = errMsg.contains("402") ||
                        errMsg.contains("plan_upgrade_required", ignoreCase = true) ||
                        errMsg.contains("credit", ignoreCase = true) ||
                        errMsg.contains("payment_required", ignoreCase = true) ||
                        errMsg.contains("quota", ignoreCase = true)

                if (isCreditOrUpgradeError && attempts < maxAttempts - 1) {
                    val rotatedKey = rotateToNextKey(context)
                    val nextSlot = getActiveKeyIndex(context) + 1
                    Log.w(TAG, "Low credits / plan upgrade required on slot #$currentSlot. Rotating to slot #$nextSlot.")
                    onStatusUpdate("Slot #$currentSlot exhausted. Rotating to backup API key #$nextSlot...")
                    attempts++
                    delay(1000)
                    continue
                } else {
                    Log.e(TAG, "Magic Hour edit failed on attempt ${attempts + 1}: ${e.message}", e)
                    return@withContext Result.failure(e)
                }
            }
        }

        Result.failure(Exception("All configured Magic Hour API keys exhausted or encountered errors."))
    }

    // ------------------------------------------------------------------------
    // HTTP HELPERS
    // ------------------------------------------------------------------------

    private fun requestUploadUrl(apiKey: String, extension: String): Pair<String, String> {
        val endpoint = "$BASE_URL/v1/files/upload-urls"
        val requestBody = JsonObject().apply {
            val items = JsonArray().apply {
                val item = JsonObject().apply {
                    addProperty("type", "image")
                    addProperty("extension", extension)
                }
                add(item)
            }
            add("items", items)
        }

        val responseText = executeHttpRequest(
            urlString = endpoint,
            method = "POST",
            headers = mapOf(
                "Authorization" to "Bearer $apiKey",
                "Content-Type" to "application/json"
            ),
            jsonBody = requestBody.toString()
        )

        val json = JsonParser.parseString(responseText).asJsonObject
        val items = json.getAsJsonArray("items")
        if (items == null || items.size() == 0) {
            throw Exception("Failed to obtain upload URL from Magic Hour: empty items in response.")
        }
        val first = items.get(0).asJsonObject
        val uploadUrl = first.get("upload_url").asString
        val filePath = first.get("file_path").asString
        return Pair(uploadUrl, filePath)
    }

    private fun uploadBytesToStorage(uploadUrl: String, bytes: ByteArray) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(uploadUrl)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                doOutput = true
                useCaches = false
                setRequestProperty("Content-Type", "image/jpeg")
                setRequestProperty("Content-Length", bytes.size.toString())
                connectTimeout = 40000
                readTimeout = 40000
            }

            conn.outputStream.use { os ->
                os.write(bytes)
                os.flush()
            }

            val code = conn.responseCode
            if (code !in 200..299) {
                val err = conn.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                throw Exception("Failed to upload image bytes to storage: HTTP $code ($err)")
            }
        } finally {
            conn?.disconnect()
        }
    }

    private fun createEditorProject(
        apiKey: String,
        filePath: String,
        prompt: String,
        model: String
    ): String {
        val endpoint = "$BASE_URL/v1/ai-image-editor"
        val body = JsonObject().apply {
            val assets = JsonObject().apply {
                addProperty("image_file_path", filePath)
            }
            add("assets", assets)

            val style = JsonObject().apply {
                addProperty("prompt", prompt)
            }
            add("style", style)

            addProperty("model", model)
            addProperty("name", "Product Image Edit ($model)")
        }

        val responseText = executeHttpRequest(
            urlString = endpoint,
            method = "POST",
            headers = mapOf(
                "Authorization" to "Bearer $apiKey",
                "Content-Type" to "application/json"
            ),
            jsonBody = body.toString()
        )

        val json = JsonParser.parseString(responseText).asJsonObject
        if (!json.has("id")) {
            throw Exception("Magic Hour did not return project ID: $responseText")
        }
        return json.get("id").asString
    }

    private suspend fun pollProjectUntilComplete(
        apiKey: String,
        projectId: String,
        onProgress: (String) -> Unit
    ): String {
        val endpoint = "$BASE_URL/v1/image-projects/$projectId"
        val maxWaitTimeMs = 90_000L
        val intervalMs = 1800L
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < maxWaitTimeMs) {
            delay(intervalMs)
            val responseText = executeHttpRequest(
                urlString = endpoint,
                method = "GET",
                headers = mapOf("Authorization" to "Bearer $apiKey")
            )

            val json = JsonParser.parseString(responseText).asJsonObject
            val status = json.get("status")?.asString ?: "unknown"

            when (status) {
                "complete" -> {
                    val downloads = json.getAsJsonArray("downloads")
                    if (downloads != null && downloads.size() > 0) {
                        return downloads.get(0).asJsonObject.get("url").asString
                    }
                    throw Exception("Project marked complete but downloads array is empty.")
                }
                "error", "canceled" -> {
                    val errorObj = json.getAsJsonObject("error")
                    val errorMsg = errorObj?.get("message")?.asString ?: "Generation ended with status $status"
                    throw Exception("Magic Hour error: $errorMsg")
                }
                "queued" -> {
                    onProgress("Magic Hour: Queued in AI render queue...")
                }
                "rendering" -> {
                    onProgress("Magic Hour: AI rendering in progress...")
                }
                else -> {
                    onProgress("Magic Hour: Processing ($status)...")
                }
            }
        }

        throw Exception("Image generation timed out after ${maxWaitTimeMs / 1000} seconds.")
    }

    private fun downloadImageBitmap(downloadUrl: String): Bitmap {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(downloadUrl)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 30000
                readTimeout = 40000
            }
            val code = conn.responseCode
            if (code !in 200..299) {
                throw Exception("Failed to download result image: HTTP $code")
            }
            conn.inputStream.use { inputStream ->
                val bmp = BitmapFactory.decodeStream(inputStream)
                    ?: throw Exception("Failed to decode rendered image from Magic Hour stream.")
                return bmp
            }
        } finally {
            conn?.disconnect()
        }
    }

    private fun executeHttpRequest(
        urlString: String,
        method: String,
        headers: Map<String, String>,
        jsonBody: String? = null
    ): String {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = method
                doInput = true
                useCaches = false
                connectTimeout = 30000
                readTimeout = 40000
                for ((k, v) in headers) {
                    setRequestProperty(k, v)
                }
            }

            if (jsonBody != null && (method == "POST" || method == "PUT")) {
                conn.doOutput = true
                OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                    writer.write(jsonBody)
                    writer.flush()
                }
            }

            val responseCode = conn.responseCode
            val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""

            if (responseCode !in 200..299) {
                Log.e(TAG, "HTTP $responseCode from $urlString: $responseText")
                throw Exception("HTTP $responseCode: $responseText")
            }

            return responseText
        } finally {
            conn?.disconnect()
        }
    }

    private fun prepareImageBytes(source: Bitmap, maxDim: Int): ByteArray {
        val (width, height) = if (source.width > maxDim || source.height > maxDim) {
            val scale = maxDim.toFloat() / maxOf(source.width, source.height)
            Pair((source.width * scale).toInt(), (source.height * scale).toInt())
        } else {
            Pair(source.width, source.height)
        }

        val scaled = if (width != source.width || height != source.height) {
            Bitmap.createScaledBitmap(source, width, height, true)
        } else {
            source
        }

        val bos = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 92, bos)
        return bos.toByteArray()
    }
}
