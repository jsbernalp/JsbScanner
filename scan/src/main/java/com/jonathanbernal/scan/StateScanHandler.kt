package com.jonathanbernal.scan

interface StateScanHandler {
    var onScanValue: ((String) -> Unit)?
    fun startScanning()
    fun stopScanning()
    fun destroyScanner()
}