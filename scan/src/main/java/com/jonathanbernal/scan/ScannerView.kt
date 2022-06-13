package com.jonathanbernal.scan

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.lang.IllegalArgumentException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class ScannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : PreviewView(context, attrs), StateScanHandler {

    override var onScanValue: ((String) -> Unit)? = null
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var cameraExecutor: ExecutorService
    private var scannerActivated = AtomicBoolean(true)

    fun setupView(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(this.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        MLKitScanner { barcode ->
                            if (scannerActivated.compareAndSet(true, false)) {
                                stopScanning()
                                onScanValue?.invoke(barcode)
                            }
                        }
                    )
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exception: IllegalArgumentException) {
                exception.message?.let { Log.e(this::class.simpleName, it) }
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun startScanning() {
        scannerActivated.set(true)
    }

    override fun stopScanning() {
        scannerActivated.set(false)
    }

    override fun destroyScanner() {
        cameraExecutor.shutdown()
    }
}