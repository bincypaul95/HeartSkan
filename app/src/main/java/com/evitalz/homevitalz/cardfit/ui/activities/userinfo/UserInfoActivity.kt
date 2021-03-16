 package com.evitalz.homevitalz.cardfit.ui.activities.userinfo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.BR
import com.evitalz.homevitalz.cardfit.UserInfoHandler
import com.evitalz.homevitalz.cardfit.databinding.ActivityUserInfoBinding


 class UserInfoActivity : AppCompatActivity(), UserInfoHandler {

    lateinit var binding : ActivityUserInfoBinding
    private val viewmodel : UserInfoViewmodel by lazy {
        ViewModelProvider(this).get(UserInfoViewmodel::class.java)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler=this
        binding.setVariable(BR.viewmodel, viewmodel)

        viewmodel.patientdetails.observe(this, Observer {
            Log.d("patientdetails", "onCreate: ${it.diabtype}")
            binding.patient = it
        })
    }

    override fun onEditClicked(view: View) {
         startActivity(Intent(this, EditUserInfo::class.java))
    }

    override fun onbloodgroupclicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onDobClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onGenderClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onDiabetictypeClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onImagePickClicked(view: View) {
        TODO("Not yet implemented")
    }
}