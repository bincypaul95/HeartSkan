package com.evitalz.homevitalz.cardfit.ui.activities.spo2

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.evitalz.homevitalz.cardfit.HandlerDeviceConnect
import com.evitalz.homevitalz.cardfit.R
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.databinding.ActivityConnectSpo2Binding
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.ConnectDeviceActivity.Companion.RESULT_NEW_DEVICE
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.Device_UUID.METER_UUID_SPO2
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.SearchDeviceActivity
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.SearchDeviceActivity.Companion.PERMISSIONS
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.SearchDeviceActivity.Companion.REQUEST_ALL_PERMISSIONS
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.SearchDeviceActivity.Companion.displayLocationSettingsRequest
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.SearchDeviceActivity.Companion.hasPermissions
import com.evitalz.homevitalz.cardfit.ui.model.MyDeviceModel

class ConnectSpo2Activity : AppCompatActivity(), HandlerDeviceConnect {
    val adapter : BluetoothAdapter by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }
    lateinit var binding: ActivityConnectSpo2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityConnectSpo2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler= this
        if (!adapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1001)
        }
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                REQUEST_ALL_PERMISSIONS
            )
        } else {
            displayLocationSettingsRequest(this)
        }

        registerReceiver(mReceiver2, IntentFilter(BluetoothDevice.ACTION_FOUND))
        adapter.startDiscovery()
    }

    private val mReceiver2: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (BluetoothDevice.ACTION_FOUND == intent.action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.d("search_test", "found")

//                if (device != null && device.name != null && device.name.equals("TNG SPO2") && (!viewmodel.isduplicate(device))) {
//                    binding.tvvailabledev.visibility= View.VISIBLE
//                    binding.ivsearch.visibility= View.VISIBLE
//                    val deviceName = device.name.trim { it <= ' ' }
//                    viewmodel.addavailableDevice(MyDeviceModel(deviceName, device))
//
//                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        LocalBroadcastManager.getInstance(this).registerReceiver(pairReceiver, filter)
        registerReceiver(pairReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(pairReceiver)
    }

    private val pairReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d("bluetooth_test", "paired")
        }
    }

    companion object{
//        fun hasItem(device : BluetoothDevice, deviceList : List<MyDeviceModel>) : Boolean{
//            for(dev in deviceList){
//                if(dev.devicemac.address == device.address){
//                    return true
//                }
//            }
//            return false
//        }

        fun getScanSettings(scanMode: Int): ScanSettings? {
            return if (scanMode == ScanSettings.SCAN_MODE_LOW_POWER || scanMode == ScanSettings.SCAN_MODE_LOW_LATENCY || scanMode == ScanSettings.SCAN_MODE_BALANCED || scanMode == ScanSettings.SCAN_MODE_OPPORTUNISTIC) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ScanSettings.Builder()
                        .setScanMode(scanMode)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                        .setReportDelay(0L)
                        .build()
                } else {
                    ScanSettings.Builder()
                        .setScanMode(scanMode)
                        .setReportDelay(0L)
                        .build()
                }
            } else {
                throw IllegalArgumentException("invalid scan mode")
            }
        }

    }

    override fun onAddDeviceClicked(view: View) {
        startActivityForResult(Intent(this, SearchDeviceActivity::class.java)  , RESULT_NEW_DEVICE )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_NEW_DEVICE && resultCode == RESULT_OK){
            if(data != null){
                val deviceMac = data.getStringExtra(Utility.DEVICE_MAC)
                val deviceName = data.getStringExtra(Utility.DEVICE_NAME)
                binding.let {
                    it.name = deviceName
                    it.address = deviceMac
                    it.executePendingBindings()
                }
            }
        }
    }


    override fun onConnectDevice(model: MyDeviceModel) {
        TODO("Not yet implemented")
    }

    override fun onConnectClicked(view: View) {
            startActivity(Intent(this, DataReceiverSpo2Activity::class.java))
    }
}