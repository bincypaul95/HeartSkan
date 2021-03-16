package com.evitalz.homevitalz.cardfit.ui.activities.menuactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.evitalz.homevitalz.cardfit.R

class AboutusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}