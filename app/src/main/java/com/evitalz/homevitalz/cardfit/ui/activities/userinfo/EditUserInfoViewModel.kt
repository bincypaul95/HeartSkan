package com.evitalz.homevitalz.cardfit.ui.activities.userinfo

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.api.ApiManager
import com.evitalz.homevitalz.cardfit.database.PatientRepository
import com.evitalz.homevitalz.cardfit.database.Patient_Reg
import com.evitalz.homevitalz.cardfit.database.Patient_details
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EditUserInfoViewModel(application: Application) : AndroidViewModel(application){
    val strDateTime : ObservableField<String> = ObservableField()
    val strgender : ObservableField<String> = ObservableField()
    val strbloodgrp : ObservableField<String> = ObservableField()
    val strbp : ObservableField<String> = ObservableField()
    val strdiab : ObservableField<String> = ObservableField()
    val datetime : Calendar = Calendar.getInstance()
    val gender : String = "NA"
    val bloodgrp : String = "NA"
    val bp : String = "NA"
    val daibtype : String = "NA"
     val patientdetails: LiveData<Patient_Reg>
     val patientdetails1: LiveData<Patient_details>
    private val patientRepository: PatientRepository

    init {
        val patientregdao = Spo2Database.getDatabase(application).patientdao()
        patientRepository = PatientRepository(patientregdao)
        patientdetails = patientRepository.getprofiledetails(Utility.getpregid(getApplication()))
        patientdetails1 = patientRepository.getpatientdetails1(Utility.getpregid(getApplication()))
        strDateTime.set(Utility.alarmdateformat.format(datetime.time))
    }

    fun setdobDateTime(dateTime: Date){
        datetime.time = dateTime
        strDateTime.set(Utility.alarmdateformat.format(datetime.time))
    }

    fun updatepatientprofile() = viewModelScope.launch(Dispatchers.IO){

        ApiManager.updatepatient(getApplication(),patientdetails.value!!.page,
            patientdetails.value!!.pname,patientdetails.value!!.pimage,
            patientdetails.value!!.pregid,patientdetails.value!!.pdob,
            patientdetails.value!!.pgender, patientdetails.value!!.bldgrp,
            patientdetails.value!!.height, patientdetails.value!!.weight,
            patientdetails1.value!!.bp, patientdetails1.value!!.bmi,
            patientdetails1.value!!.waist, patientdetails1.value!!.hip, patientdetails1.value!!.hba1c
        )
        patientRepository.updatepatientprofile(patientdetails.value!!)
        patientRepository.updatepatientdetails(patientdetails1.value!!)
    }
}