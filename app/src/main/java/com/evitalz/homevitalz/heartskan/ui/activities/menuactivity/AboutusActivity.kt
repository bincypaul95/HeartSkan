package com.evitalz.homevitalz.heartskan.ui.activities.menuactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.evitalz.homevitalz.heartskan.R

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