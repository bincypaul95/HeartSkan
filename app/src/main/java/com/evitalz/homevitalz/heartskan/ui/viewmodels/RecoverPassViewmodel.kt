package com.evitalz.homevitalz.heartskan.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evitalz.homevitalz.heartskan.api.RestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class RecoverPassViewmodel(application: Application):AndroidViewModel(application) {
    var update: MutableLiveData<Int> = MutableLiveData()
    init {

    }
    fun updatepassword(mailid: String, password: String)=viewModelScope.launch(Dispatchers.IO) {
        var jsonObject : JSONObject = RestApi().updatePassword(mailid, password)
        if (jsonObject.getBoolean("Successful")) {
            Log.d("verifyotp", "sucess")
            update.postValue(1)
            Log.d("verifyotp", "updatepassword: ${update.value}")
        }

    }
}