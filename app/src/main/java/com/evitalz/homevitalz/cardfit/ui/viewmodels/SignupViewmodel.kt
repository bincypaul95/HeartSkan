package com.evitalz.homevitalz.cardfit.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.cardfit.api.RestApi
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import com.evitalz.homevitalz.cardfit.database.UserRepository
import com.evitalz.homevitalz.cardfit.database.User_Reg

import com.evitalz.homevitalz.cardfit.ui.activities.login.insertmanualuser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignupViewmodel(application: Application): AndroidViewModel(application) {
    var validatemail: MutableLiveData<Int> = MutableLiveData()
    lateinit var otp: String
    var uregid: MutableLiveData<Int> = MutableLiveData()
    var pregid: MutableLiveData<Int> = MutableLiveData()
    private val userRepository: UserRepository

    init {
        val userDao = Spo2Database.getDatabase(application).userdao()
        userRepository = UserRepository(userDao)
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

    fun geturegid(insertmanualuser: insertmanualuser)= viewModelScope.launch(Dispatchers.IO) {

        var jsonObject : JSONObject = RestApi().insert_manual_user(insertmanualuser.User_Name,
                                        insertmanualuser.User_Dob,insertmanualuser.User_Gender,
                                        insertmanualuser.User_Eamil,insertmanualuser.User_Phone,
                                        insertmanualuser.User_Password,insertmanualuser.User_image,
                                        insertmanualuser.imei,insertmanualuser.account_status,
                                        insertmanualuser.Lstatus,insertmanualuser.Lstatus_time,
                                        insertmanualuser.IDP_uid,insertmanualuser.IsEncrypt)

        if (jsonObject.getBoolean("Successful")) {
            uregid.postValue(jsonObject.getInt("Value"))
            Log.d("uregid", "uregid:${uregid} ")

            val jsonobject1 : JSONObject = RestApi().add_patient4(
                jsonObject.getInt("Value"),insertmanualuser.User_Name,insertmanualuser.User_Dob,insertmanualuser.User_Gender,
                insertmanualuser.age,insertmanualuser.User_image,insertmanualuser.User_Phone,0,"45","",insertmanualuser.User_Eamil,
                "","","","","","","")

            if (jsonobject1.getBoolean("Successful")) {
                pregid.postValue(jsonobject1.getInt("Value"))
                Log.d("pregid", "pregid:${pregid} ")
            }else{
                pregid.postValue(-1)
            }

        }else{
            uregid.postValue(-1)
        }
    }

    fun insertuser(userReg: User_Reg) = viewModelScope.launch(Dispatchers.IO){
        userRepository.insertuser(userReg)
    }
}