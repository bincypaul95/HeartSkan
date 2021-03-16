package com.evitalz.homevitalz.cardfit.ui.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.evitalz.homevitalz.cardfit.HandlerRecoverpass
import com.evitalz.homevitalz.cardfit.databinding.ActivityRecoverPasswordBinding
import com.evitalz.homevitalz.cardfit.ui.viewmodels.RecoverPassViewmodel

class RecoverPasswordActivity : AppCompatActivity(), HandlerRecoverpass {
    lateinit var binding: ActivityRecoverPasswordBinding
    private val viewmodel : RecoverPassViewmodel by lazy {
        ViewModelProvider(this).get(RecoverPassViewmodel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler= this
        binding.etnewpass.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword(binding.etnewpass.text.toString())
            }
        }

        viewmodel.update.observe(this, Observer {
            if(it==1){
                Log.d("password recovery", "onChangePassword: true")
                Toast.makeText(this, "Your Password Changed", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else
            {
                Log.d("password recovery", "onChangePassword: false")
                Toast.makeText(this, "Something went wrong..", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun validatePassword(password: String): Boolean? {

        return if (password.isEmpty()) {
            binding.etnewpass.error = "Field cannot be empty"
            false
        }
        else if (password.length <= 6) {
            binding.tvnew.error ="Password is too weak"
            false
        }
        else {
            binding.etnewpass.error=null
            binding.tvnew.error = null
            binding.tvnew.isErrorEnabled = false
            true
        }
    }
    override fun onChangePassword(view: View) {

        if((binding.etnewpass.text.toString()==binding.etconfirmpassword.text.toString())
            && !TextUtils.isEmpty(binding.etnewpass.text.toString())
            && !TextUtils.isEmpty(binding.etconfirmpassword.text.toString())){
//            viewmodel.updatepassword("bincypaul.elab@gmail.com",binding.etconfirmpassword.text.toString())
            intent.extras?.getString("email")?.let { viewmodel.updatepassword(it,binding.etconfirmpassword.text.toString()) }

        }else{
            Snackbar.make(binding.root,"Password Mismatch", Snackbar.LENGTH_LONG).show()
        }


    }
}