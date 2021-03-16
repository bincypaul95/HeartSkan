package com.evitalz.homevitalz.cardfit.ui.activities.userinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.database.PatientRepository
import com.evitalz.homevitalz.cardfit.database.ProfileDetails
import com.evitalz.homevitalz.cardfit.database.Spo2Database



class UserInfoViewmodel(application: Application):AndroidViewModel(application) {
    private val patientRepository: PatientRepository
    var patientdetails: LiveData<ProfileDetails>
    init {
        val patientregdao = Spo2Database.getDatabase(application).patientdao()
        patientRepository = PatientRepository(patientregdao)
        patientdetails = patientRepository.getprofiledetails1(Utility.getpregid(getApplication()))
    }
}