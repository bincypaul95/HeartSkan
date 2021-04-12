package com.evitalz.homevitalz.heartskan.ui.viewmodels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.evitalz.homevitalz.heartskan.ui.model.MyDeviceModel

class SearchViewModel(application: Application): AndroidViewModel(application){
    val deviceList=ArrayList<MyDeviceModel>()
    val deviceavilableList=ArrayList<MyDeviceModel>()
    val deviceavailableListLive = MutableLiveData<List<MyDeviceModel>>()

    init {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = adapter.bondedDevices
            for(device in pairedDevices){
                val deviceName = device.name
                if(deviceName.substring(0, 4) == "PM10"){
                   addavailableDevice(MyDeviceModel(deviceName, device))
                }
            }
    }


    fun addDevice(deviceModel: MyDeviceModel){
        deviceList.add(deviceModel)
    }
    fun addavailableDevice(deviceModel: MyDeviceModel){
        deviceavilableList.add(deviceModel)
        deviceList.add(deviceModel)
        deviceavailableListLive.value=deviceavilableList
    }
    fun isduplicate(bluetoothDevice: BluetoothDevice):Boolean{
        for(device in deviceList){
            if(device.devicemac==bluetoothDevice){
                return true
            }
        }
        return false
    }



}

