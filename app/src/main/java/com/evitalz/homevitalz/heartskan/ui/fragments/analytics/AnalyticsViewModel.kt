package com.evitalz.homevitalz.heartskan.ui.fragments.analytics

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.evitalz.homevitalz.heartskan.Utility

import com.evitalz.homevitalz.heartskan.database.DeviceReadingsRepository
import com.evitalz.homevitalz.heartskan.database.Device_Readings
import com.evitalz.homevitalz.heartskan.database.Spo2Database
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DeviceReadingsRepository
    var testResult : LiveData<List<Device_Readings>>
    var ecgResult : LiveData<List<Device_Readings>>
    val strDate: ObservableField<String> = ObservableField()
    val heartrate: ObservableField<String> = ObservableField()
    val datetime : Calendar
    val dates : MutableLiveData<RequiredTime>
    val currentCal : Calendar = Calendar.getInstance()
    val format1: SimpleDateFormat = SimpleDateFormat("EEE,MMMM dd" , Locale.getDefault())
    val weekformat: SimpleDateFormat = SimpleDateFormat("MMMM dd" , Locale.getDefault())
    val monthFormat: SimpleDateFormat = SimpleDateFormat("MMMM" , Locale.getDefault())
    var selecteddays : Int=0
    val selecteditem: ObservableField<Int> = ObservableField()
    val today = Calendar.getInstance()
    init {
        val dao = Spo2Database.getDatabase(application).devicereadingdao()
        repository = DeviceReadingsRepository(dao)
        datetime = Calendar.getInstance()
        Log.d("setdte", ":${datetime.time} ")
        strDate.set(format1.format(datetime.time))
        dates = MutableLiveData(
            RequiredTime(datetime.timeInMillis,
            datetime.timeInMillis)
        )

        selecteditem.set(0)
        testResult = Transformations.switchMap(dates){ time ->
                repository.devicereadingSpo2(time.startTime, time.endTime, Utility.getpregid(getApplication()))
        }
        ecgResult = Transformations.switchMap(dates){ time ->
            repository.devicereadingheartrate(time.startTime, time.endTime, Utility.getpregid(getApplication()))
        }

    }
    class RequiredTime(val startTime : Long , val endTime : Long)


    fun changeDate(dateTime: Date , endTime : Date){
        //val cal = Calendar.getInstance().apply { this.time.time = time }
        datetime.time = dateTime
        dates.value = RequiredTime(dateTime.time , endTime.time)
        //Log.d("datetimeSet" , "year ${cal.get(Calendar.YEAR)} month ${cal.get(Calendar.MONTH)} , dayofmonth ${cal.get(Calendar.DAY_OF_MONTH)}")
        Log.d("datetimeSet" , "millin seconds ${datetime.time.time} year ${datetime.get(Calendar.YEAR)} month ${datetime.get(Calendar.MONTH)} , dayofmonth ${datetime.get(Calendar.DAY_OF_MONTH)}")

        when(selecteddays){
            0 -> {
                strDate.set(format1.format(datetime.time))
            }
            1 -> {
                strDate.set("${weekformat.format(dateTime.time)} - ${weekformat.format(endTime.time)}")
            }
            2 ->{
                strDate.set(monthFormat.format(dateTime.time))
            }
        }
        currentCal.time =dateTime
    }

    fun isNextDayAvailable(time: String): Boolean{
        return format1.parse(time).date != today.get(Calendar.DATE)
    }


}


