package com.evitalz.homevitalz.cardfit.ui.activities.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.cardfit.database.*
import com.google.gson.GsonBuilder
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.api.RestApi
import com.evitalz.homevitalz.cardfit.ui.activities.login.addnewpatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class homeactivityviewmodel(application: Application):AndroidViewModel(application) {

    val deviceReadings: MutableLiveData<List<DeviceReadings>> = MutableLiveData()
    val patientnames: LiveData<List<Patientnameid>>
    private val deviceReadingsRepository: DeviceReadingsRepository
    private val patientRepository: PatientRepository
    var selectedpatient =0
    init {

        val deviceReadingdao = Spo2Database.getDatabase(application).devicereadingdao()
        deviceReadingsRepository = DeviceReadingsRepository(deviceReadingdao)

        val patientdao = Spo2Database.getDatabase(application).patientdao()
        patientRepository = PatientRepository(patientdao)
        patientnames=patientRepository.getpatientnames(Utility.geturegid(getApplication()))

    }

    fun syncdevicereadings(pregid: Int, lastsync: String , context : Context)= viewModelScope.launch(Dispatchers.IO){
        val gson = GsonBuilder().create()
        val list:ArrayList<DeviceReadings> =ArrayList()


        var jsonObject : JSONObject = RestApi().synch_devicereadings(pregid, Utility.getLastSync(context))

        Utility.saveCurrentTime(context)

        if (jsonObject.getBoolean("Successful")) {
            val jsonArr: JSONArray = jsonObject.getJSONArray("Value")
            for (i in 0 until jsonArr.length()) {
                val obj1 = jsonArr.getJSONObject(i)
                Log.d("devicereadings", obj1.toString())
                val data = gson.fromJson(obj1.toString(), DeviceReadings::class.java)
                list.add(data)
            }
            deviceReadings.postValue(list)
        }

    }

    fun addpatient(addnewpatient: addnewpatient)= viewModelScope.launch(Dispatchers.IO){

        val jsonobject1 : JSONObject = RestApi().add_patient4(
            Utility.geturegid(getApplication()),addnewpatient.panme,addnewpatient.pdob,addnewpatient.pgender,
            addnewpatient.page,"",addnewpatient.phone,0,"45","","",
            "","","","","","","")


        if (jsonobject1.getBoolean("Successful")) {
            val jsonArr = jsonobject1.getInt("Value")
            Log.d("ADdPatient", "addpatient: ${jsonobject1.getInt("Value")}")
            patientRepository.insertuser(
                Patient_Reg(0,
                    Utility.geturegid(getApplication()),jsonobject1.getInt("Value"),
                addnewpatient.panme,addnewpatient.pdob,addnewpatient.pgender,  addnewpatient.page,addnewpatient.phone,
                "",Calendar.getInstance().timeInMillis,0,"",1,45,"","",
                "","","","","","","","","","",Calendar.getInstance().timeInMillis
            )
            )
        }

    }

    fun insertdevicereadings(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        deviceReadingsRepository.insertdevicereadings(deviceReadings)
    }

    fun updatedevicereadings(deviceReadings: Device_Readings) = viewModelScope.launch(Dispatchers.IO){
        deviceReadingsRepository.updatedevicereadingslocal(deviceReadings)
    }



}