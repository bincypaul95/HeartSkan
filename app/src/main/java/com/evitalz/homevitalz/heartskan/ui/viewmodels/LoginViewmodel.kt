package com.evitalz.homevitalz.heartskan.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.heartskan.database.*
import com.google.gson.GsonBuilder
import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.api.RestApi
import com.evitalz.homevitalz.heartskan.ui.activities.home.DeviceReadings
import com.evitalz.homevitalz.heartskan.ui.activities.login.PatientReg
import com.evitalz.homevitalz.heartskan.ui.activities.login.UserReg
import com.evitalz.homevitalz.heartskan.ui.fragments.profile.Patientdetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class LoginViewmodel(application: Application): AndroidViewModel(application) {

    private val userRepository: UserRepository
    var uregid: MutableLiveData<Int> = MutableLiveData()
    var pregid: MutableLiveData<Int> = MutableLiveData()
    val userdata: MutableLiveData<List<UserReg>> = MutableLiveData()
    val patientdata: MutableLiveData<List<PatientReg>> = MutableLiveData()
    private val patientRepository: PatientRepository
    val patientdetails: MutableLiveData<List<Patientdetails>> = MutableLiveData()
    var validatemail: MutableLiveData<Int> = MutableLiveData()
    lateinit var otp: String

    private val deviceReadingsRepository: DeviceReadingsRepository
    init {
            val deviceReadingdao = Spo2Database.getDatabase(application).devicereadingdao()
            deviceReadingsRepository = DeviceReadingsRepository(deviceReadingdao)
        val userdao = Spo2Database.getDatabase(application).userdao()
        userRepository = UserRepository(userdao)
        val patientdao = Spo2Database.getDatabase(application).patientdao()
        patientRepository = PatientRepository(patientdao)
    }

    fun syncdevicereadings(pregid: Int, lastsync: String )= viewModelScope.launch(Dispatchers.IO){
        val gson = GsonBuilder().create()
        val list:ArrayList<DeviceReadings> =ArrayList()


        var jsonObject : JSONObject = RestApi().synch_devicereadings(pregid, "")

//        Utility.saveCurrentTime(context)

        if (jsonObject.getBoolean("Successful")) {
            val jsonArr: JSONArray = jsonObject.getJSONArray("Value")
            for (i in 0 until jsonArr.length()) {
                val obj1 = jsonArr.getJSONObject(i)
                Log.d("devicereadings", obj1.toString())
                val data = gson.fromJson(obj1.toString(), DeviceReadings::class.java)
                val deviceReadings = Device_Readings(
                    0,
                    data.did,
                    data.pregid,
                    data.dread1,
                    data.dread2,
                    data.dread3,
                    data.dread4,
                    Calendar.getInstance().timeInMillis,
                    1,
                    data.dtype,
                    0,
                    1,
                    "",
                    data.dread5,
                    data.notes,
                    (Utility.simpleDateFormat1.parse(data.dateTime.replace("T", " "))).time
                )
                deviceReadingsRepository.insertdevicereadings(deviceReadings)
                list.add(data)
            }
        }

    }

    fun getuserdetails(email: String, password: String)= viewModelScope.launch(Dispatchers.IO) {

        val gson = GsonBuilder().create()
        val list:ArrayList<UserReg> =ArrayList()
        val list2:ArrayList<Patientdetails> =ArrayList()
        val list1: java.util.ArrayList<PatientReg> = java.util.ArrayList()
        var jsonObject : JSONObject = RestApi().getUserDetails(email, password)

        if (jsonObject.getBoolean("Successful")) {
            val jsonArray: JSONArray = jsonObject.getJSONArray("Value")
            if(jsonArray.length()>0){
                for (i in 0 until  jsonArray.length()){

                    val obj = jsonArray.getJSONObject(i)
                    Log.d("gsonvalue", obj.toString())
                    val data = gson.fromJson(obj.toString(), UserReg::class.java)
                    list.add(data)
                    userdata.postValue(list)
                    uregid.postValue(obj.getInt("Ureg_id"))
                    Log.d("uregid", uregid.value.toString())

                    var jsonObject1 : JSONObject = RestApi().synch_patient(obj.getInt("Ureg_id"),"")
                    if(jsonObject1.getBoolean("Successful")) {
                        val jsonArray1: JSONArray = jsonObject1.getJSONArray("Value")
                        if (jsonArray1.length() > 0) {
                            for (i in 0 until jsonArray1.length()) {
                                val obj1 = jsonArray1.getJSONObject(i)
                                Log.d("PatientReg", obj1.toString())
                                val data1 = gson.fromJson(obj1.toString(), PatientReg::class.java)
                                list1.add(data1)
                                pregid.postValue(obj1.getInt("Preg_id"))

                                var jsonObject2 : JSONObject = RestApi().getPatientDetails(obj1.getInt("Preg_id"))
                                if (jsonObject2.getBoolean("Successful")) {
                                    val jsonArr: JSONArray = jsonObject2.getJSONArray("Value")
                                    if(jsonArr.length()>0){
                                        for (i in 0 until jsonArr.length()) {
                                            val obj2 = jsonArr.getJSONObject(i)
                                            Log.d("patientdetails", obj2.toString())
                                            val data2 = gson.fromJson(obj2.toString(), Patientdetails::class.java)
                                            list2.add(data2)
                                        }
                                    }
                                }
                                Log.d("pregid", pregid.toString())
                            }
                        }
                        patientdata.postValue(list1)
                        patientdetails.postValue(list2)
                    }else{
                        pregid.postValue(-1)
                    }

                }
            }else{
                uregid.postValue(-1)
            }
        }else{
            uregid.postValue(-1)
            pregid.postValue(-1)
        }
    }


    fun insertuserreg(userReg: User_Reg) = viewModelScope.launch(Dispatchers.IO){
        userRepository.insertuser(userReg)
    }

    fun checkmail(mail: String) = viewModelScope.launch(Dispatchers.IO){
        var jsonObject : JSONObject = RestApi().validate_Email(mail)
        if (jsonObject.getBoolean("Successful")) {
            validatemail.postValue(jsonObject.getInt("Value"))
            Log.d("checkmail", "checkmail:${jsonObject.getInt("Value")} ")
        }
    }

    fun sendotp(mail: String)= viewModelScope.launch(Dispatchers.IO) {
        var jsonObject : JSONObject = RestApi().verifyDetailsByOtp(mail)
        if (jsonObject.getBoolean("Successful")) {
            val jsonArray = jsonObject.getJSONObject("Value")
            otp = jsonArray.getString("Result")
            Log.d("checkmail", "checkmail:${otp} ")
        }
    }

    fun insertpatientreg(patientReg: Patient_Reg) = viewModelScope.launch(Dispatchers.IO){
        patientRepository.insertuser(patientReg)
    }

    fun updatepatientreg(patientReg: Patient_Reg) = viewModelScope.launch(Dispatchers.IO){
        patientRepository.updatepatientprofile(patientReg)
    }
    fun insertpatientdetails(patient_Details: Patient_details) = viewModelScope.launch(Dispatchers.IO){

        patientRepository.insertpatientdetails(patient_Details)
    }
}