package com.jonathanbernal.jsbscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jonathanbernal.scan.ScannerView
import com.jonathanbernal.scan.StateScanHandler

class MainActivity : AppCompatActivity() {

    private lateinit var scannerView: ScannerView
    private lateinit var stateScanHandler: StateScanHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initScan()
    }

    private fun initScan(){
        scannerView = this.findViewById(R.id.scanner_view)
        scannerView.setupView(this)
        stateScanHandler = scannerView
        stateScanHandler.onScanValue = {
            Log.e(this::class.simpleName,"codigo escaneado $it")
            stateScanHandler.startScanning()
        }
    }
}