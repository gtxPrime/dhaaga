package com.dhaaga.app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * ImageUploadRepository — Uploads artisan product & profile images to shared PHP hosting
 * server via HTTP multipart request and returns direct public image URLs.
 * Equipped with comprehensive Android logging (TAG: DhaagaUpload).
 */
object ImageUploadRepository {

    private const val TAG = "DhaagaUpload"

    // Default live API configuration
    var DEFAULT_UPLOAD_URL = "https://dhaaga.thecoolestportfolio.site/upload.php"
    var DEFAULT_API_KEY = "dhaaga_sih2026_secure_upload_key"

    /**
     * Uploads an image from Uri to the PHP shared hosting API.
     * @return Result.success with the public direct image URL string, or Result.failure with Exception.
     */
    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        uploadUrl: String = DEFAULT_UPLOAD_URL,
        apiKey: String = DEFAULT_API_KEY
    ): Result<String> = withContext(Dispatchers.IO) {
        val boundary = "----DhaagaUploadBoundary" + UUID.randomUUID().toString()
        val lineEnd = "\r\n"
        val twoHyphens = "--"

        var connection: HttpURLConnection? = null
        var outputStream: DataOutputStream? = null

        Log.i(TAG, "================ START IMAGE UPLOAD ================")
        Log.i(TAG, "Target Upload URL: $uploadUrl")
        Log.i(TAG, "Input Image URI  : $imageUri")

        try {
            val url = URL(uploadUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                doInput = true
                doOutput = true
                useCaches = false
                requestMethod = "POST"
                setRequestProperty("Connection", "Keep-Alive")
                setRequestProperty("User-Agent", "DhaagaAndroidApp/1.0")
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                setRequestProperty("X-API-KEY", apiKey)
                connectTimeout = 30000
                readTimeout = 30000
            }

            outputStream = DataOutputStream(connection.outputStream)

            // Add API key form field as fallback parameter
            outputStream.writeBytes(twoHyphens + boundary + lineEnd)
            outputStream.writeBytes("Content-Disposition: form-data; name=\"api_key\"$lineEnd$lineEnd")
            outputStream.writeBytes(apiKey + lineEnd)

            // Open image file stream from ContentResolver
            val inputStream: InputStream? = try {
                context.contentResolver.openInputStream(imageUri)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to open InputStream from URI: $imageUri", e)
                null
            }

            if (inputStream == null) {
                val err = "Cannot open image stream from URI: $imageUri"
                Log.e(TAG, "❌ $err")
                return@withContext Result.failure(Exception(err))
            }

            val fileName = "upload_" + System.currentTimeMillis() + ".jpg"
            Log.d(TAG, "Uploading file name: $fileName")

            // Write image file multipart header
            outputStream.writeBytes(twoHyphens + boundary + lineEnd)
            outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"$fileName\"$lineEnd")
            outputStream.writeBytes("Content-Type: image/jpeg$lineEnd$lineEnd")

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesSent: Long = 0
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesSent += bytesRead
            }
            outputStream.writeBytes(lineEnd)
            inputStream.close()

            Log.d(TAG, "Bytes written to stream: $totalBytesSent bytes")

            // End multipart request
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
            outputStream.flush()

            val responseCode = connection.responseCode
            Log.i(TAG, "Server HTTP Response Code: $responseCode")

            val responseStream = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val responseText = responseStream?.bufferedReader()?.use { it.readText() } ?: ""
            Log.d(TAG, "Raw Server Response Body: $responseText")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val json = JSONObject(responseText)
                val status = json.optString("status")
                if (status == "success") {
                    val imageUrl = json.getString("url")
                    Log.i(TAG, "✅ UPLOAD SUCCESSFUL! Image URL: $imageUrl")
                    Log.i(TAG, "================ END IMAGE UPLOAD ================")
                    Result.success(imageUrl)
                } else {
                    val msg = json.optString("message", "Upload failed")
                    Log.e(TAG, "❌ Server returned non-success status: $status | Message: $msg")
                    Log.i(TAG, "================ END IMAGE UPLOAD ================")
                    Result.failure(Exception("Server Error: $msg"))
                }
            } else {
                Log.e(TAG, "❌ HTTP Error $responseCode: $responseText")
                Log.i(TAG, "================ END IMAGE UPLOAD ================")
                Result.failure(Exception("HTTP Error $responseCode: $responseText"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception occurred during image upload: ${e.message}", e)
            Log.i(TAG, "================ END IMAGE UPLOAD ================")
            Result.failure(e)
        } finally {
            try { outputStream?.close() } catch (_: Exception) {}
            try { connection?.disconnect() } catch (_: Exception) {}
        }
    }
}
