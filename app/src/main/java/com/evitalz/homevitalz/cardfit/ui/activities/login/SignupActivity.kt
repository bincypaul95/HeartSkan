package com.evitalz.homevitalz.cardfit.ui.activities.login

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.SignupHandler
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.database.User_Reg
import com.evitalz.homevitalz.cardfit.databinding.ActivitySignupBinding
import com.evitalz.homevitalz.cardfit.databinding.EnterotpBinding

import com.evitalz.homevitalz.cardfit.ui.activities.home.HomeActivity
import com.evitalz.homevitalz.cardfit.ui.activities.SplashActivity
import com.evitalz.homevitalz.cardfit.ui.viewmodels.SignupViewmodel

import java.util.*


class SignupActivity : AppCompatActivity(), SignupHandler {

    lateinit var binding : ActivitySignupBinding
    lateinit var dialog:Dialog
    lateinit var dialogBinding: EnterotpBinding
    private lateinit var preferences: SharedPreferences
    var gender :Int =5
    var verify= false
    private val viewmodel : SignupViewmodel by lazy {
        ViewModelProvider(this).get(SignupViewmodel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler= this
        preferences = getSharedPreferences(SplashActivity.PREF, MODE_PRIVATE)

        viewmodel.pregid.observe(this, Observer {
            if(it != -1){
                preferences.edit().putInt(Utility.PREGID, viewmodel.pregid.value!!).apply()
            }
        })
//        binding.etemail.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) {
//                    validateEmail(binding.etemail.text.toString())
//            }
//        }

        binding.etphonenumber.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if(binding.etphonenumber.text.toString().length!=10){
                    binding.etphonenumber.error= "Enter 10 digit phone number"
                }

            }
        }

        binding.etusername.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateUsername(binding.etusername.text.toString())
            }
        }

        binding.etpassword.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword(binding.etpassword.text.toString())
            }
        }

        viewmodel.validatemail.observe(this , Observer {
            if(it == 0){
                verify= true
                viewmodel.sendotp(binding.etemail.text.toString())
                showotp()
            }else{
                binding.textinput2.error= "Email Already Exist!"
            }

        })

        viewmodel.uregid.observe(this, Observer {

            if(it != -1){
                preferences.edit().putInt(Utility.UREGID, viewmodel.uregid.value!!).apply()
                val userReg=  User_Reg(0, viewmodel.uregid.value!!,binding.etusername.text.toString(),
                    "",gender,binding.etemail.text.toString(),binding.etphonenumber.text.toString(),
                    binding.etpassword.text.toString(),"","",Calendar.getInstance().timeInMillis,
                    Calendar.getInstance().timeInMillis,0,1,1,46,0)

                viewmodel.insertuser(userReg)

                preferences.edit().putBoolean(SplashActivity.STATUS_LOGIN, true).apply()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }

        })
    }

    override fun onLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCreateAccClicked(view: View) {

         val username =binding.etusername.text.toString()
         val email = binding.etemail.text.toString()
         val phonenumber= binding.etphonenumber.text.toString()
         val password= binding.etpassword.text.toString()
         val conpassword= binding.etconfirmpassword.text.toString()

        if( validateUsername(username) == true &&
            validateEmail(email) == true &&
            validatePhoneNo(phonenumber) == true &&
            validatePassword(password) == true &&
            validateconfirmPassword(password , conpassword) == true
        ) {
            if(verify){
                Toast.makeText(this,"Account Created Successfully", Toast.LENGTH_LONG).show()
            }

        }


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
        dialogBinding.tvemail.text =binding.etemail.text.toString()
        initView()
        dialog.show()
    }

    override fun onMaleClicked(view: View) {
        gender = 0
    }

    override fun onFemaleClicked(view: View) {
        gender= 1
    }

    override fun onOtherClicked(view: View) {
       gender = 2
    }

    override fun onVerifyotpClicked(view: View) {

        dialog.dismiss()
    }

    private fun validateEmail(emailaddress: String): Boolean? {

        val emailPattern =  "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+".toRegex()
        return if (emailaddress.isEmpty()) {
            binding.etemail.error = "Field cannot be empty"
            false
        } else if (!emailaddress.matches(emailPattern)) {
            binding.textinput2.error = "Invalid email address"
            false
        } else{
            binding.textinput2.error = null
            viewmodel.checkmail(emailaddress)
            true
        }
    }

    private fun validateUsername(username: String): Boolean? {

        return if (username.isEmpty()) {
            binding.etusername.error= "Field cannot be empty"
            false
        } else if (username.length >= 15) {
            binding.textinput1.error="Username too long"
            false
        } else {
            binding.textinput1.error = null
            binding.textinput1.isErrorEnabled = false
            true
        }
    }

    private fun validatePhoneNo(phonenumber: String): Boolean? {
        return if (phonenumber.isEmpty()) {
            binding.etphonenumber.error = "Field cannot be empty"
            false
        } else {
            binding.etphonenumber.error = null
            binding.textinput3.isErrorEnabled = false
            true
        }
    }

    private fun validatePassword(password: String): Boolean? {

        return if (password.isEmpty()) {
            binding.etpassword.error = "Field cannot be empty"
            false
        }
        else if (password.length <= 6) {
            binding.textinput4.error ="Password is too weak"
            false
        }
        else {
            binding.etpassword.error=null
            binding.textinput4.error = null
            binding.textinput4.isErrorEnabled = false
            true
        }
    }

    private fun validateconfirmPassword(password: String , conpassword : String): Boolean? {

        return if (conpassword.isEmpty()) {
            binding.etconfirmpassword.error = "Field cannot be empty"
            false
        }
        else if (password!=conpassword) {
            binding.textinput5.error ="Password mismatch"
            false
        }
        else {
            binding.etconfirmpassword.error=null
            binding.textinput5.error = null
            binding.textinput5.isErrorEnabled = false
            true
        }
    }

    private fun initView() {

        val progressDialog = ProgressDialog(this)
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
                           viewmodel.geturegid(
                               insertmanualuser(binding.etusername.text.toString(),"",gender,
                               binding.etemail.text.toString(),
                               binding.etphonenumber.text.toString(),binding.etpassword.text.toString(),"","",0,1,"",0,0,0)
                           )
        }
        else{
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_LONG).show()
        }
    }

}


