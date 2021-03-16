package com.evitalz.homevitalz.cardfit

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class RfTestActivity : AppCompatActivity() {
    val SERVICE_UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_UUID = UUID.fromString("00002a6c-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_UUID_1 = UUID.fromString("00002a18-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_UUID_2 = UUID.fromString("00002a6d-0000-1000-8000-00805f9b34fb")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rf_test)


        GlobalScope.launch{
            async {
                getData()
            }
        }
        Log.d("ble_test", "success")
    }

    fun getData() = lifecycleScope.async{
        val bluetoothManager : BluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        val list = adapter.bondedDevices
        Log.d("paireddevices", "onCreate: " + list.size)
//        val device: BluetoothDevice = adapter.getRemoteDevice("C0:26:DA:00:01:47")
        val device: BluetoothDevice = adapter.getRemoteDevice("A2:C1:20:10:05:14")
        val socket = device.createRfcommSocketToServiceRecord(CHARACTERISTIC_UUID)
        val buffer = ByteArray(1024)
        while(socket.inputStream.read(buffer) != -1){
            Log.d("ble_test" , "data coming")
        }
    }

}