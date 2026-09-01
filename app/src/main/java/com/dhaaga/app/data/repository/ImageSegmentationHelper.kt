package com.dhaaga.app.data.repository

import android.graphics.*
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.tasks.await
import java.nio.FloatBuffer

/**
 * ImageSegmentationHelper:
 * Uses Google ML Kit Subject Segmentation to detect and extract foreground products
 * (textiles, pottery, handicrafts, jewelry, decor) and completely remove cluttered backgrounds.
 */
object ImageSegmentationHelper {
    private const val TAG = "ImageSegmentationHelper"

    /**
     * Extracts foreground subject/product with transparent background.
     * Returns an ARGB_8888 Bitmap where all background pixels are completely transparent (alpha = 0).
     */
    suspend fun extractProductForeground(source: Bitmap): Bitmap {
        try {
            val options = SubjectSegmenterOptions.Builder()
                .enableForegroundBitmap()
                .enableForegroundConfidenceMask()
                .build()

            val segmenter = SubjectSegmentation.getClient(options)
            val inputImage = InputImage.fromBitmap(source, 0)
            val result = segmenter.process(inputImage).await()

            val fgBitmap = result.foregroundBitmap
            if (fgBitmap != null) {
                Log.i(TAG, "✅ Successfully extracted product foreground with ML Kit Subject Segmentation!")
                return fgBitmap
            }

            val mask = result.foregroundConfidenceMask
            if (mask != null) {
                Log.i(TAG, "Applying foreground confidence mask to extract product...")
                return applyConfidenceMask(source, mask)
            }
        } catch (e: Exception) {
            Log.w(TAG, "ML Kit Subject Segmentation exception (${e.message}). Falling back to saliency segmentation.")
        }

        // High quality on-device fallback saliency & edge cutout
        return extractForegroundFallback(source)
    }

    private fun applyConfidenceMask(source: Bitmap, mask: FloatBuffer): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        mask.rewind()
        for (i in pixels.indices) {
            val confidence = if (mask.hasRemaining()) mask.get() else 1f
            val alpha = (confidence * 255).toInt().coerceIn(0, 255)
            val color = pixels[i]
            pixels[i] = (alpha shl 24) or (color and 0x00FFFFFF)
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    /**
     * Fallback algorithmic segmentation using edge-boundary analysis and color distance.
     */
    fun extractForegroundFallback(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val borderSamples = listOf(
            pixels[0],
            pixels[width - 1],
            pixels[(height - 1) * width],
            pixels[width * height - 1],
            pixels[width / 2],
            pixels[(height - 1) * width + width / 2],
            pixels[(height / 2) * width],
            pixels[(height / 2) * width + width - 1]
        )

        var avgR = 0; var avgG = 0; var avgB = 0
        for (c in borderSamples) {
            avgR += Color.red(c)
            avgG += Color.green(c)
            avgB += Color.blue(c)
        }
        avgR /= borderSamples.size
        avgG /= borderSamples.size
        avgB /= borderSamples.size

        val threshold = 40.0
        val centerDistMax = Math.hypot((width / 2).toDouble(), (height / 2).toDouble())

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val c = pixels[idx]
                val r = Color.red(c)
                val g = Color.green(c)
                val b = Color.blue(c)

                val colorDist = Math.sqrt(
                    Math.pow((r - avgR).toDouble(), 2.0) +
                    Math.pow((g - avgG).toDouble(), 2.0) +
                    Math.pow((b - avgB).toDouble(), 2.0)
                )

                val distFromCenter = Math.hypot((x - width / 2).toDouble(), (y - height / 2).toDouble()) / centerDistMax

                if (colorDist < threshold && distFromCenter > 0.35) {
                    val fade = ((colorDist / threshold) * 255).toInt().coerceIn(0, 255)
                    pixels[idx] = (fade shl 24) or (c and 0x00FFFFFF)
                } else if (colorDist < threshold * 0.6) {
                    pixels[idx] = 0
                }
            }
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }
}
