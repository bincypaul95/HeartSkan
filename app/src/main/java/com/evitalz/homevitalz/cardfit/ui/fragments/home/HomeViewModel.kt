package com.evitalz.homevitalz.cardfit.ui.fragments.home

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.evitalz.homevitalz.cardfit.database.DeviceReadingsRepository
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.api.RestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val strDate: ObservableField<String> = ObservableField()
    val datetime : Calendar = Calendar.getInstance()

    private val deviceReadingsRepository: DeviceReadingsRepository
    private val format1: SimpleDateFormat = SimpleDateFormat("EEE,MMMM dd" , Locale.getDefault())
    private val dates : MutableLiveData<RequiredTime>
    private val pregid : MutableLiveData<Int> = MutableLiveData()
    val currentCal : Calendar = Calendar.getInstance()
    val deviceReadings : LiveData<List<Device_Readings>>
    var type  = 1
    val today: Calendar = Calendar.getInstance()

    init {

        strDate.set(format1.format(datetime.time))
        dates = MutableLiveData(
            RequiredTime(
                datetime.timeInMillis,
                datetime.timeInMillis
            )
        )
        val devicereadingdao = Spo2Database.getDatabase(application).devicereadingdao()
        deviceReadingsRepository = DeviceReadingsRepository(devicereadingdao)
        deviceReadings = Transformations.switchMap(dates){ time ->
            if(type == 1)
                deviceReadingsRepository.devicereadings(time.startTime , time.endTime, Utility.getpregid(getApplication()))
            else
                deviceReadingsRepository.devicereadingsdesc(time.startTime , time.endTime, Utility.getpregid(getApplication()))
        }
//        pregid.value = Utility.getpregid(getApplication()).toInt()
//        deviceReadings = Transformations.switchMap(dates,pregid){ time , id ->
//            if(type == 1)
//                deviceReadingsRepository.devicereadings(time.startTime ,time.endTime, it)
//            else
//                deviceReadingsRepository.devicereadingsdesc(time.startTime , time.endTime,it)
//        }

    }
    class RequiredTime(val startTime : Long , val endTime : Long)

    fun changeDate(dateTime: Date, endTime : Date){
        datetime.time = dateTime
        dates.value = RequiredTime(dateTime.time, endTime.time)
        strDate.set(format1.format(datetime.time))
        currentCal.time =dateTime
    }

    fun deletedevicereading(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        val jsonObject= RestApi().delete_Device_Reading(deviceReadings.dreadid)
        Log.d("Delete", "deletedevicereading: ${jsonObject}")
        if(jsonObject.getBoolean("Successful")){
            deviceReadingsRepository.deletedevicereading(deviceReadings.id)
        }
    }

    fun isNextDayAvailable(time: String): Boolean{
        return format1.parse(time).date != today.get(Calendar.DATE)
    }


}