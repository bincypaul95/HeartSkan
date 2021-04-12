package com.evitalz.homevitalz.heartskan.ui.fragments.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.evitalz.homevitalz.heartskan.database.PatientRepository
import com.evitalz.homevitalz.heartskan.database.Patient_Reg
import com.evitalz.homevitalz.heartskan.database.Patient_details
import com.evitalz.homevitalz.heartskan.database.Spo2Database
import com.evitalz.homevitalz.heartskan.Utility

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

        private val patientRepository: PatientRepository
        var patientdetails: LiveData<List<Patient_details>>
        var patientreg: LiveData<List<Patient_Reg>>


        init {
                val patientdao = Spo2Database.getDatabase(application).patientdao()
                patientRepository = PatientRepository(patientdao)
                Log.d("pregid", "${Utility.getpregid(getApplication())}")
                patientdetails = patientRepository.getpatientdetails(Utility.getpregid(getApplication()))
                patientreg = patientRepository.getpatientreg(Utility.getpregid(getApplication()))
        }


}