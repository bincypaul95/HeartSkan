package com.evitalz.homevitalz.cardfit.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.cardfit.api.ApiManager
import com.evitalz.homevitalz.cardfit.database.DeviceReadingsRepository
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import com.evitalz.homevitalz.cardfit.ui.model.DeviceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataReceiveSpo2Viewmodel(application: Application): AndroidViewModel(application) {
    private val list = ArrayList<DeviceData>()
    private val repository: DeviceReadingsRepository
//    private val logrepository: LogRepository
//    val readings : LiveData<List<Device_Readings>>
    var selectedtab : Int=0
    var deviceDataResult : MutableLiveData<DeviceData> = MutableLiveData()
    var results : MutableLiveData<DeviceData> = MutableLiveData()
    init {
        val readingsDao = Spo2Database.getDatabase(application).devicereadingdao()
        repository = DeviceReadingsRepository(readingsDao)
//        val logDao = EvitalzO2Database.getDatabase(application).logdao()
//        logrepository = LogRepository(logDao)
//        readings=readingsDao.getDeviceReadingspo2()
//        logs=logDao.getlogs()
    }

    fun addResult(deviceData: DeviceData) {
//        list.add(deviceData)
        results.value = deviceData
    }

    fun insertreadings(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        val rowId = repository.insertdevicereadings(deviceReadings)
        ApiManager.insertdata(rowId , getApplication())
    }

    fun insertlog(log: Log) = viewModelScope.launch(Dispatchers.IO){
//        logrepository.insertlog(log)
    }

}