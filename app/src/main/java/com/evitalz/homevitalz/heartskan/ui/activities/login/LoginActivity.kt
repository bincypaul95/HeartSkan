package com.evitalz.homevitalz.heartskan.ui.activities.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.evitalz.homevitalz.heartskan.LoginHandler
import com.evitalz.homevitalz.heartskan.SignupHandler
import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.api.ApiManager
import com.evitalz.homevitalz.heartskan.database.Patient_Reg
import com.evitalz.homevitalz.heartskan.database.Patient_details
import com.evitalz.homevitalz.heartskan.database.User_Reg
import com.evitalz.homevitalz.heartskan.databinding.ActivityLoginBinding
import com.evitalz.homevitalz.heartskan.databinding.EnteremailBinding
import com.evitalz.homevitalz.heartskan.databinding.EnterotpBinding

import com.evitalz.homevitalz.heartskan.ui.activities.SplashActivity
import com.evitalz.homevitalz.heartskan.ui.activities.home.HomeActivity
import com.evitalz.homevitalz.heartskan.ui.viewmodels.LoginViewmodel

import kotlinx.android.synthetic.main.activity_data_reading.view.*
import kotlinx.android.synthetic.main.connect_dialog.*
import kotlinx.android.synthetic.main.enteremail.view.*
import java.util.*

class LoginActivity : AppCompatActivity(), LoginHandler, SignupHandler {

    lateinit var binding: ActivityLoginBinding
    private lateinit var preferences: SharedPreferences
    lateinit var dialog:Dialog
    lateinit var dialogemailbinding: EnteremailBinding
    lateinit var dialogBinding: EnterotpBinding
    private val viewmodel : LoginViewmodel by lazy {
        ViewModelProvider(this).get(LoginViewmodel::class.java)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler = this
//        if(BuildConfig.IS_DEV){
//            binding.etusername.setText("bincypaul.elab4@gmail.com")
//            binding.etpassword.setText("bincy123")
//
//        }

        viewmodel.validatemail.observe(this , Observer {
            if(it == 1){
                viewmodel.sendotp(dialogemailbinding.etemail.text.toString())
                showotp()
            }else{
                dialogemailbinding.etemail.error= "Email address doesn't exist!"
            }

        })
        preferences = getSharedPreferences(SplashActivity.PREF, MODE_PRIVATE)

        viewmodel.userdata.observe(this, Observer {
            Log.d("data_check", "onCreate: $it")

            if (it.isNotEmpty()) {

                preferences.edit().putInt(Utility.UREGID, it[0].uregid).apply()
                preferences.edit().putString(Utility.USER_NAME, it[0].uname).apply()
                preferences.edit().putString(Utility.USER_EMAIL, it[0].uemail).apply()
                Log.d("data_check", "onCreate: "+ it[0].uregid)

                val userreg = User_Reg(0,it[0].uregid,it[0].uname,it[0].udob,it[0].ugender,
                it[0].uemail, it[0].uphone,it[0].upassword,it[0].uimage, it[0].uimei,
                    Calendar.getInstance().timeInMillis, Calendar.getInstance().timeInMillis,
                    0,1,1,45,1 )
                viewmodel.insertuserreg(userreg)

            }
            else{
                Toast.makeText(this,"Email address or password incorrect", Toast.LENGTH_LONG).show()
            }
        })


        viewmodel.patientdata.observe(this, androidx.lifecycle.Observer {
            Log.d("listsize", "onCreate: ${it.size}")
            if (it.isNotEmpty()) {
                viewmodel.syncdevicereadings(it[0].pregid , "")
                preferences.edit().putInt(Utility.PREGID,it[0].pregid).apply()
                preferences.edit().putString(Utility.PNAME,it[0].pname).apply()

                for (i in it.indices) {
                    val patientReg = Patient_Reg(
                        0,
                        it[i].uregid,
                        it[i].pregid,
                        it[i].pname,
                        it[i].pdob,
                        it[i].pgender,
                        it[i].page,
                        it[i].mobilenumber,
                        "",
                        Calendar.getInstance().timeInMillis,
                        0,
                        "",
                        1,
                        45,
                        it[i].bloodgroup,
                        it[i].email,
                        it[i].height,
                        it[i].weight,
                        "","",
                        "",
                        "",
                        "","","","",
                        Calendar.getInstance().timeInMillis
                    )
                    viewmodel.insertpatientreg(patientReg)
                }

            } else {
                Toast.makeText(this, "No patients Found", Toast.LENGTH_LONG).show()
            }
        })
        
        viewmodel.patientdetails.observe(this, Observer {
            Log.d("profiledetails", "onViewCreated: ${it.size}")
            if(it.isNotEmpty()){
                for(i in it.indices){
                    val patientdetails = Patient_details(0,it[i].pregid,it[i].diabetic,it[i].bmi,it[i].kidneydisease,
                        it[i].angina,it[i].smoker,it[i].HbA1C,it[i].steroidmedication,it[i].waistsize,it[i].hipsize,0,Calendar.getInstance().timeInMillis,it[i].bp)
                    viewmodel.insertpatientdetails(patientdetails)
                }
            }else{
                val patientdetails = Patient_details(0, Utility.getpregid(this),"",0,0,
                    0,0,"",0,"","",0,Calendar.getInstance().timeInMillis,"")
                ApiManager.insertpatientdetails(
                    application,"", Utility.getpregid(this),
                    "", "","", "")
                viewmodel.insertpatientdetails(patientdetails)
            }
            preferences.edit().putBoolean(SplashActivity.STATUS_LOGIN, true).apply()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        })



    }

    override fun onLoginClicked(view: View) {
        if(!TextUtils.isEmpty(binding.etusername.text) &&
            !TextUtils.isEmpty(binding.etpassword.text))
        {
            viewmodel.getuserdetails(
                binding.etusername.text.toString(),
                binding.etpassword.text.toString()
            )
        }
    }

    override fun onCreateAccClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onMaleClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onFemaleClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onOtherClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onVerifyotpClicked(view: View) {
        dialog.dismiss()
    }

    override fun onForgotPasswordClicked(view: View) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialogemailbinding= EnteremailBinding.inflate(layoutInflater)
        dialog.setContentView(dialogemailbinding.root)
        val display = windowManager.defaultDisplay
        dialog.window?.setLayout(
            (display.width * 0.95).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
        dialogemailbinding.btnsubmit.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
            viewmodel.checkmail(dialogemailbinding.etemail.text.toString())
        }

    }

    override fun onSignUpClicked(view: View) {
        startActivity(Intent(this, SignupActivity::class.java))
        finish()
    }
    fun showotp(){
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialogBinding= EnterotpBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val display = windowManager.defaultDisplay
        dialog.window?.setLayout(
            (display.width * 0.95).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogBinding.handler=this
        dialogBinding.con1.visibility= View.VISIBLE
        dialogBinding.con2.visibility= View.GONE
        dialogBinding.tvemail.text =dialogemailbinding.etemail.text.toString()
        initView()
        dialog.show()
    }
    private fun initView() {

        ProgressDialog(this)
        dialogBinding.et1.addTextChangedListener(GenericTextWatcher(dialogBinding.et1, dialogBinding.et2))
        dialogBinding.et2.addTextChangedListener(GenericTextWatcher(dialogBinding.et2, dialogBinding.et3))
        dialogBinding.et3.addTextChangedListener(GenericTextWatcher(dialogBinding.et3, dialogBinding.et4))
        dialogBinding.et4.addTextChangedListener(GenericTextWatcher(dialogBinding.et4, dialogBinding.et5))
        dialogBinding.et5.addTextChangedListener(GenericTextWatcher(dialogBinding.et5, dialogBinding.et6))
        dialogBinding.et6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNotEmpty()){
                    hideKeyboard()
                    checkOTP()
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        dialogBinding.et1.setOnKeyListener(GenericKeyEvent(dialogBinding.et1, null))
        dialogBinding.et2.setOnKeyListener(GenericKeyEvent(dialogBinding.et2, dialogBinding.et1))
        dialogBinding.et3.setOnKeyListener(GenericKeyEvent(dialogBinding.et3, dialogBinding.et2))
        dialogBinding.et4.setOnKeyListener(GenericKeyEvent(dialogBinding.et4, dialogBinding.et3))
        dialogBinding.et5.setOnKeyListener(GenericKeyEvent(dialogBinding.et5, dialogBinding.et4))
        dialogBinding.et6.setOnKeyListener(GenericKeyEvent(dialogBinding.et6,dialogBinding.et5))

    }

    fun hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkOTP() {
        val enteredOTP= dialogBinding.et1.text.toString()+dialogBinding.et2.text.toString()+
                dialogBinding.et3.text.toString()+dialogBinding.et4.text.toString()+
                dialogBinding.et5.text.toString()+dialogBinding.et6.text.toString()

        if(viewmodel.otp == enteredOTP){
            dialogBinding.con1.visibility= View.GONE
            dialogBinding.con2.visibility= View.VISIBLE
            dialog.setCancelable(true)
            Toast.makeText(this, "Verfied Email Address", Toast.LENGTH_LONG).show()
            val i = Intent(this, RecoverPasswordActivity::class.java)
            i.putExtra("email", dialogemailbinding.etemail.text.toString())
            startActivity(i)
            finish()
        }
        else{
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_LONG).show()
        }
    }



}