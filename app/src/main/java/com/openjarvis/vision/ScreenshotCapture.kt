package com.openjarvis.vision

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.view.WindowManager
import java.nio.ByteBuffer

class ScreenshotCapture(private val context: Context) {

    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    private var isCapturing = false
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenDensity = 0

    fun startCapture(): Boolean {
        val projectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intents = mutableListOf<Intent>()
        
        for (activity in getActivities()) {
            val intent = projectionManager.createScreenCaptureIntent()
            intents.add(intent)
        }
        
        return intents.isNotEmpty()
    }

    fun initialize(projection: MediaProjection): Bitmap? {
        release()

        val windowManager = context.getSystemService(WindowManager::class.java)
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)

        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        screenDensity = metrics.densityDpi

        handlerThread = HandlerThread("ScreenshotThread").apply { start() }
        handler = Handler(handlerThread!!.looper)

        imageReader = ImageReader.newInstance(
            screenWidth,
            screenHeight,
            PixelFormat.RGBA_8888,
            2
        )

        mediaProjection = projection
        isCapturing = true

        return null
    }

    fun capture(): Bitmap? {
        if (!isCapturing || imageReader == null) return null

        val reader = imageReader ?: return null
        val surface = reader.surface ?: return null
        
        try {
            virtualDisplay?.release()
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "Screenshot",
                reader.width,
                reader.height,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                surface,
                null,
                handler
            )

            val image = reader.acquireLatestImage()
            return image?.let { img ->
                val planes = img.planes
                val buffer: ByteBuffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * img.width

                val bitmap = Bitmap.createBitmap(
                    img.width + rowPadding / pixelStride,
                    img.height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)
                img.close()

                if (rowPadding > 0) {
                    Bitmap.createBitmap(bitmap, 0, 0, img.width, img.height)
                } else {
                    bitmap
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun release() {
        isCapturing = false
        virtualDisplay?.release()
        virtualDisplay = null
        mediaProjection?.stop()
        mediaProjection = null
        imageReader?.close()
        imageReader = null
        handlerThread?.quitSafely()
        handlerThread = null
        handler = null
    }

    private fun getActivities(): List<Activity> {
        return listOf()
    }

    companion object {
        @Volatile
        private var instance: ScreenshotCapture? = null

        fun getInstance(context: Context): ScreenshotCapture {
            return instance ?: synchronized(this) {
                instance ?: ScreenshotCapture(context.applicationContext).also { instance = it }
            }
        }
    }
}
