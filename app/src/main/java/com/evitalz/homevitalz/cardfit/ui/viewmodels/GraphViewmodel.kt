package com.evitalz.homevitalz.cardfit.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.evitalz.homevitalz.cardfit.database.*
import com.evitalz.homevitalz.cardfit.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphViewmodel(application: Application) :AndroidViewModel(application){
    val repository: DeviceReadingsRepository
    private val patientRepository: PatientRepository
    var patientdetails: LiveData<Patient_Reg>
    init {
        val dao = Spo2Database.getDatabase(application).devicereadingdao()
        repository = DeviceReadingsRepository(dao)
        val patientregdao = Spo2Database.getDatabase(application).patientdao()
        patientRepository = PatientRepository(patientregdao)
        patientdetails = patientRepository.getprofiledetails(Utility.getpregid(getApplication()))
    }

    suspend fun getdata(rowid:Long) : Device_Readings {
         return withContext(Dispatchers.IO){
             repository.devicereadingheartratebyid(rowid)
         }
    }
}