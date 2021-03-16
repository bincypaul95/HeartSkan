package com.evitalz.homevitalz.cardfit.ui.fragments.analytics.graphviewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.database.DeviceReadingsRepository
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import com.evitalz.homevitalz.cardfit.ui.fragments.analytics.AnalyticsViewModel
import java.util.*

class ViewModelBloodGlucose(application: Application) : AndroidViewModel(application){
    var selecteddays : Int=0
    private val repository: DeviceReadingsRepository
    init {
        val cal = Calendar.getInstance()
        val dao = Spo2Database.getDatabase(application).devicereadingdao()
        repository = DeviceReadingsRepository(dao)
        val requiredTime = AnalyticsViewModel.RequiredTime(cal.timeInMillis , cal.timeInMillis)
        repository.devicereadingBG(requiredTime.startTime, requiredTime.endTime, Utility.getpregid(getApplication()))
    }



}