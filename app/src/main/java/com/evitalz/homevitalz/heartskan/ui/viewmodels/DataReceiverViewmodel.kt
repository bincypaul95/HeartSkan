package com.evitalz.homevitalz.heartskan.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.heartskan.api.ApiManager
import com.evitalz.homevitalz.heartskan.database.DeviceReadingsRepository
import com.evitalz.homevitalz.heartskan.database.Device_Readings
import com.evitalz.homevitalz.heartskan.database.ECGRepository
import com.evitalz.homevitalz.heartskan.database.Spo2Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DataReceiverViewmodel(application: Application):AndroidViewModel(application) {

    private val deviceReadingsRepository: DeviceReadingsRepository
    private val ecgRepository: ECGRepository
    val datetime : Calendar
    var rowId: Long = 0
    var result1: Long = 0
    var result2: Long = 0
    var result3: Long = 0
    var result4: Long = 0

    init {
        val deviceReadingdao = Spo2Database.getDatabase(application).devicereadingdao()
        deviceReadingsRepository = DeviceReadingsRepository(deviceReadingdao)
        val ecgReadingsDao = Spo2Database.getDatabase(application).ecgreadingdao()
        ecgRepository = ECGRepository(ecgReadingsDao)
        datetime = Calendar.getInstance()
    }

    fun insertdevicereadings(deviceReadings: Device_Readings, selectedplacement: String ) = viewModelScope.launch(Dispatchers.IO){
        when(selectedplacement){
            "Left Hand" -> {
                result1 = deviceReadingsRepository.insertdevicereadings(deviceReadings)
                rowId = result1
            }
            "Left Wrist" -> {
                result2 = deviceReadingsRepository.insertdevicereadings(deviceReadings)
                rowId = result2
            }
            "Left Leg" -> {
                result3 = deviceReadingsRepository.insertdevicereadings(deviceReadings)
                rowId = result3
            }
            "Chest" -> {
                result4 = deviceReadingsRepository.insertdevicereadings(deviceReadings)
                rowId = result4
            }
        }
        ApiManager.insertdata(rowId , getApplication())
    }



}
