package com.evitalz.homevitalz.cardfit.ui.fragments.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.evitalz.homevitalz.cardfit.database.PatientRepository
import com.evitalz.homevitalz.cardfit.database.Patient_Reg
import com.evitalz.homevitalz.cardfit.database.Patient_details
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import com.evitalz.homevitalz.cardfit.Utility

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