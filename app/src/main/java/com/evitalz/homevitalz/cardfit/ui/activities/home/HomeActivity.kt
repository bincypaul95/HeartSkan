package com.evitalz.homevitalz.cardfit.ui.activities.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.evitalz.homevitalz.cardfit.HandlerAddnew
import com.evitalz.homevitalz.cardfit.R

import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import com.evitalz.homevitalz.cardfit.databinding.ActivityAddNewPatientBinding
import com.evitalz.homevitalz.cardfit.databinding.ActivityHomeBinding
import com.evitalz.homevitalz.cardfit.ui.activities.SplashActivity
import com.evitalz.homevitalz.cardfit.ui.activities.SplashActivity.Companion.STATUS_LOGIN
import com.evitalz.homevitalz.cardfit.ui.activities.login.LoginActivity
import com.evitalz.homevitalz.cardfit.ui.activities.login.addnewpatient
import com.evitalz.homevitalz.cardfit.ui.activities.menuactivity.AboutusActivity
import com.evitalz.homevitalz.cardfit.ui.activities.menuactivity.TermsandConditionsActivity

import com.evitalz.homevitalz.cardfit.ui.activities.userinfo.UserInfoActivity
import kotlinx.android.synthetic.main.navheader.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class HomeActivity : AppCompatActivity(), HandlerAddnew {
    lateinit var binding : ActivityHomeBinding
    lateinit var dialobinding : ActivityAddNewPatientBinding
    private var mSpinnerItem1: MenuItem? = null
    var patientname =  ArrayList<String>()
    var patientid =  ArrayList<Int>()
    var age :Int =0
    var gender :Int =5
    lateinit var dialog:Dialog
    private lateinit var preferences: SharedPreferences
    lateinit var patientdataAdapter : ArrayAdapter<String>
    private val viewmodel : homeactivityviewmodel by lazy {
        ViewModelProvider(this).get(homeactivityviewmodel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences(SplashActivity.PREF, MODE_PRIVATE)

        viewmodel.patientnames.observe(this, Observer {
            Log.d("Patientnames", "onCreate: $it")
            patientname.clear()
            patientid.clear()

            if (it.isNotEmpty()) {
                for (i in it.indices) {
                    patientname.add(it[i].pname)
                    patientid.add(it[i].pregid)
                    Log.d("Patientnames", "onCreate: $it")
                }
            }
        })


        viewmodel.deviceReadings.observe(this, Observer {
            if (it.isNotEmpty()) {

                for (i in it.indices) {
                    Log.d("datetime", it[i].dateTime)
                    Log.d(
                        "datetime", "onCreate:${
                            (Utility.simpleDateFormat1.parse(
                                it[i].dateTime.replace(
                                    "T",
                                    " "
                                )
                            )).time
                        }"
                    )
                    val deviceReadings = Device_Readings(
                        0,
                        it[i].did,
                        it[i].pregid,
                        it[i].dread1,
                        it[i].dread2,
                        it[i].dread3,
                        it[i].dread4,
                        Calendar.getInstance().timeInMillis,
                        1,
                        it[i].dtype,
                        0,
                        1,
                        "",
                        it[i].dread5,
                        it[i].notes,
                        (Utility.simpleDateFormat1.parse(it[i].dateTime.replace("T", " "))).time
                    )
                    viewmodel.insertdevicereadings(deviceReadings)
                }
            }
        })

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        val navigationView = findViewById<NavigationView>(R.id.nav_sideview)
        val hView = navigationView.getHeaderView(0)
        val navhead = hView.findViewById<View>(R.id.navhead) as TextView
        val navmail = hView.findViewById<View>(R.id.navmail) as TextView
        Log.d("user", "onCreate: "+ Utility.getusername(this))
        Log.d("user", "onCreate: "+ Utility.getuseremail(this))
        navhead.text= Utility.getusername(this)
        navmail.text=  Utility.getuseremail(this)
        binding.navSideview.setNavigationItemSelectedListener(
            NavigationView.OnNavigationItemSelectedListener() {
                when (it.itemId) {

                    R.id.logout -> {
                        GlobalScope.launch(Dispatchers.IO) {
                            val db = Spo2Database.getDatabase(applicationContext)
                            db.clearAllTables()
                        }

                        preferences = getSharedPreferences(SplashActivity.PREF, MODE_PRIVATE)
                        preferences.edit().putBoolean(STATUS_LOGIN, false).apply()
                        val i = Intent(this@HomeActivity, LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                    R.id.nav_patientdetails -> {
                        startActivity(Intent(this, UserInfoActivity::class.java))
                    }
                    R.id.nav_order -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse("https://shop.evitalz.com"))
                        startActivity(intent)
                    }
                    R.id.nav_support -> {
                        startActivity(Intent(this, AboutusActivity::class.java))
                    }
                    R.id.nav_addnew -> {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                        if (patientname.size > 2) {
                            Toast.makeText(
                                this,
                                "Cannot add more than two patients",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                             dialog = Dialog(this)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(true)
                            dialobinding = ActivityAddNewPatientBinding.inflate(layoutInflater)
                            dialog.setContentView(dialobinding.root)
                            val display = windowManager.defaultDisplay
                            dialog.window?.setLayout(
                                (display.width * 0.95).toInt(),
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            dialobinding.handler = this
                            dialog.show()
                        }


                    }
                    R.id.nav_termsandpolicy -> {
                        startActivity(Intent(this, TermsandConditionsActivity::class.java))
                    }
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            })

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.home_mennu, menu)
        mSpinnerItem1 = menu!!.findItem(R.id.menuSpinner)
        val view1: View = mSpinnerItem1!!.actionView
        if (view1 is Spinner){
            patientdataAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                patientname
            )
            patientdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            view1.adapter = patientdataAdapter
            view1.setSelection(viewmodel.selectedpatient)
            view1.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View,
                    arg2: Int, arg3: Long
                ) {
                    val pregid = patientid[view1.selectedItemPosition]
                    Log.d("pregid", "onItemSelected: $pregid")
                    preferences.edit().putInt(Utility.PREGID, pregid).apply()
                    sendBroadcast(Intent().apply {
                        action = "UserChanged"
                    })
                    viewmodel.selectedpatient=arg2
                    if (arg0 != null) {
                        (arg0.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    }

                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {
                }
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onAddNewClicked(view: View) {
        if(!TextUtils.isEmpty(dialobinding.etusername.text.toString()) &&
            !TextUtils.isEmpty(dialobinding.etdob.text.toString()) &&
                gender != 5){
            viewmodel.addpatient(addnewpatient(dialobinding.etusername.text.toString(), dialobinding.etdob.text.toString(), gender, age,dialobinding.etphonenumber.text.toString() ))
            Toast.makeText(this, "Patient added Successfully", Toast.LENGTH_LONG).show()
            dialog.dismiss()

        }else{
            Toast.makeText(this, "All the * fields are mandatory", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDobClicked(view: View) {
        val today = Calendar.getInstance()
        DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                Log.d("datetimeSet", "year $year month $month , dayofmonth $dayOfMonth")
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                age =getAge(year,month,dayOfMonth)
                dialobinding.etdob.setText(Utility.alarmdateformat.format(cal.time))
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onMaleClicked(view: View) {
        gender = 0
    }

    override fun onFemaleClicked(view: View) {
        gender = 1
    }

    override fun onOtherClicked(view: View) {
        gender = 2
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {

        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.set(year, month, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        val ageInt = age
        return ageInt
    }





}