package com.jonathanbernal.scan

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

typealias BarcodeListener = (barcode: String) -> Unit

class MLKitScanner(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        ).build()

    private val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage != null) {
            val imageScanning =
                InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            scanner.process(imageScanning)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcodeListener(barcode.rawValue ?: "")
                    }
                }
                .addOnCompleteListener {
                    image.close()
                }
        }

    }


}