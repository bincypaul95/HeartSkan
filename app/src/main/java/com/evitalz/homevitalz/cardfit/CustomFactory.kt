package com.evitalz.homevitalz.cardfit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.ui.viewmodels.*

class CustomFactorySpO2ViewModel(private val application: Application, private val dateinlong : Long,
                                 private val  deviceReadings: Device_Readings?,
                                 private val  update: Boolean) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Spo2Viewmodel(application,dateinlong ,deviceReadings,update ) as T
    }

}
class CustomFactoryMealViewModel(private val application: Application, private val dateinlong : Long,
                                 private val  deviceReadings: Device_Readings?,
                                 private val  update: Boolean) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MealViewmodel(application,dateinlong,deviceReadings,update ) as T
    }

}

class CustomFactoryLiquorViewModel(private val application: Application, private val dateinlong : Long,
                                 private val  deviceReadings: Device_Readings?,
                                 private val  update: Boolean) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LiquorViewmodel(application,dateinlong,deviceReadings,update ) as T
    }

}

class CustomFactoryExcersiceViewModel(private val application: Application,
                                      private val dateinlong : Long,
                                      private val  deviceReadings: Device_Readings?,
                                      private val  update: Boolean) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExcersiceViewModel(application,dateinlong,deviceReadings,update) as T
    }

}
class CustomFactorySleepViewModel(private val application: Application, private val dateinlong : Long,
                                  private val  deviceReadings: Device_Readings?,
                                  private val  update: Boolean) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SleepViewmodel(application,dateinlong, deviceReadings,update ) as T
    }

}
class CustomFactoryPillViewModel(private val application: Application, private val dateinlong : Long,
                                 private val  deviceReadings: Device_Readings?,
                                 private val  update: Boolean) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PilltakenViewmodel(application,dateinlong,deviceReadings,update ) as T
    }

}

class CustomFactoryBGViewModel(private val application: Application, private val dateinlong : Long,
                                 private val  deviceReadings: Device_Readings?,
                                 private val  update: Boolean) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GlucoseViewmodel(application,dateinlong,deviceReadings,update ) as T
    }

}

class CustomFactoryheartratebyidViewModel(private val application: Application, private val rowid : Long) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GraphViewmodel(application) as T
    }

}