package com.evitalz.homevitalz.heartskan.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.heartskan.api.ApiManager
import com.evitalz.homevitalz.heartskan.database.DeviceReadingsRepository
import com.evitalz.homevitalz.heartskan.database.Device_Readings
import com.evitalz.homevitalz.heartskan.database.Spo2Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Spo2Viewmodel(application: Application, val dateinlong: Long, val deviceReadings: Device_Readings?, val update: Boolean): AndroidViewModel(application) {
    val strDate : ObservableField<String> = ObservableField()
    val strtime : ObservableField<String> = ObservableField()
    val spo2 : ObservableField<String> = ObservableField()
    val pulse : ObservableField<String> = ObservableField()
    val btnsave : ObservableField<String> = ObservableField()
    val datetime : Calendar
    val note : ObservableField<String> = ObservableField()
    private val strFormatter : SimpleDateFormat
    private val strTimeFormatter : SimpleDateFormat
    private val deviceReadingsRepository: DeviceReadingsRepository

    init {
        val date=Date(dateinlong)
        datetime = Calendar.getInstance().apply { time = date }
        strFormatter = SimpleDateFormat("MMM dd" , Locale.getDefault())
        strTimeFormatter = SimpleDateFormat("hh:mm a" , Locale.getDefault())
        strDate.set(strFormatter.format(datetime.time))
        strtime.set(strTimeFormatter.format(datetime.time))
        val deviceReadingdao = Spo2Database.getDatabase(application).devicereadingdao()
        deviceReadingsRepository = DeviceReadingsRepository(deviceReadingdao)
        btnsave.set("SAVE")
        if(update){
            spo2.set(deviceReadings!!.dread1)
            pulse.set(deviceReadings!!.dread2)
            note.set(deviceReadings!!.note)
            strDate.set(strFormatter.format(deviceReadings.datetime))
            strtime.set(strTimeFormatter.format(deviceReadings.datetime))
            btnsave.set("UPDATE")
        }
    }
    fun insertdevicereadings(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        val rowId = deviceReadingsRepository.insertdevicereadings(deviceReadings)
        ApiManager.insertdata(rowId , getApplication())
    }
    fun updatedevicereadings(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        deviceReadingsRepository.updatedevicereadings(deviceReadings)
    }
    fun changeDate(dateTime: Date){

        datetime.time = dateTime
        Log.d("datetimeSet" , "year ${datetime.get(Calendar.YEAR)} month ${datetime.get(Calendar.MONTH)} , dayofmonth ${datetime.get(Calendar.DAY_OF_MONTH)}")
        strDate.set(strFormatter.format(datetime.time))
    }

    fun changeTime(dateTime: Date){
        datetime.time = dateTime
        strtime.set(strTimeFormatter.format(datetime.time))
    }
}