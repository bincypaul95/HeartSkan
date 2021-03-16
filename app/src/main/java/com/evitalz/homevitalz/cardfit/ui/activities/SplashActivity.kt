package com.evitalz.homevitalz.cardfit.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.evitalz.homevitalz.cardfit.ui.activities.home.HomeActivity
import com.evitalz.homevitalz.cardfit.ui.activities.login.LoginActivity

  class SplashActivity : AppCompatActivity() {

    val sharedPreferences : SharedPreferences by lazy {
        getSharedPreferences(PREF , Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(sharedPreferences.getBoolean(STATUS_LOGIN, false)){
            startActivity(Intent(this , HomeActivity::class.java))
            finish()
//            val intent = Intent(this, ECGgraphActivity::class.java)
//            intent.putExtra("result1", 3L)
//            intent.putExtra("result2", 4L)
////            intent.putExtra("result3", viewmodel.result3)
////            intent.putExtra("result4", viewmodel.result4)
//            startActivity(intent)
//            finish()
        }
        else{
            startActivity(Intent(this , LoginActivity::class.java))
            finish()
        }


    }

    companion object{
        const val PREF = "my_pref"
        const val STATUS_LOGIN = "status_login"
    }

}