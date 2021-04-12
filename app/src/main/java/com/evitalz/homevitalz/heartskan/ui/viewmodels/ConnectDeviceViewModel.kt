package com.evitalz.homevitalz.heartskan.ui.viewmodels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.evitalz.homevitalz.heartskan.ui.model.MyDeviceModel

class ConnectDeviceViewModel(application: Application):AndroidViewModel(application) {
    val deviceListLive = MutableLiveData<List<MyDeviceModel>>()
    val deviceavailableListLive = MutableLiveData<List<MyDeviceModel>>()
    val deviceList=ArrayList<MyDeviceModel>()
    val deviceavilableList=ArrayList<MyDeviceModel>()
    lateinit var currentDevice : BluetoothDevice

    init {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = adapter.bondedDevices
    }

    fun addDevice(deviceModel: MyDeviceModel){
        deviceList.add(deviceModel)
        deviceListLive.value=deviceList
    }

    fun removedevice(deviceModel: MyDeviceModel){
        deviceList.remove(deviceModel)
        deviceListLive.value=deviceList
    }

//    fun addavailableDevice(deviceModel: MyDeviceModel){
////        deviceavilableList.add(deviceModel)
//        deviceavailableListLive.value=deviceavilableList
//    }

}