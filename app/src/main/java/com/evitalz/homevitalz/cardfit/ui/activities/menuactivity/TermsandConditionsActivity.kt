package com.evitalz.homevitalz.cardfit.ui.activities.menuactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.evitalz.homevitalz.cardfit.R


class TermsandConditionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termsand_conditions)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}