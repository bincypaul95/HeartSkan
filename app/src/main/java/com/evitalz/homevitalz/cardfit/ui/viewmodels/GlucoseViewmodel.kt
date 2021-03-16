package com.evitalz.homevitalz.cardfit.ui.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.cardfit.api.ApiManager
import com.evitalz.homevitalz.cardfit.database.DeviceReadingsRepository
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GlucoseViewmodel(application: Application, val dateinlong: Long, val deviceReadings: Device_Readings?, val update: Boolean): AndroidViewModel(application) {
    val strDate : ObservableField<String> = ObservableField()
    val strtime : ObservableField<String> = ObservableField()
    val datetime : Calendar
    val btnsave: ObservableField<String> = ObservableField()
    val bglevelinmmol: ObservableField<String> = ObservableField()
    val bglevelinmgdl: ObservableField<String> = ObservableField()
    val testtype: ObservableField<String> = ObservableField()
    val note: ObservableField<String> = ObservableField()
    val strFormatter : SimpleDateFormat
    val strTimeFormatter : SimpleDateFormat
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
            bglevelinmmol.set(deviceReadings!!.dread1)
            bglevelinmgdl.set(deviceReadings!!.dread5)
            testtype.set(deviceReadings!!.dread2)
            note.set(deviceReadings!!.note)
            strDate.set(strFormatter.format(deviceReadings.datetime))
            strtime.set(strTimeFormatter.format(deviceReadings.datetime))
            btnsave.set("UPDATE")
        }
    }

    fun changeDate(dateTime: Date){

        datetime.time = dateTime
        strDate.set(strFormatter.format(datetime.time))
    }

    fun changeTime(dateTime: Date){
        datetime.time = dateTime
        strtime.set(strTimeFormatter.format(datetime.time))
    }

    fun insertdevicereadings(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        val rowId = deviceReadingsRepository.insertdevicereadings(deviceReadings)
        ApiManager.insertdata(rowId , getApplication())
    }

    fun updatedevicereadings(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        deviceReadingsRepository.updatedevicereadings(deviceReadings)
    }
}