package com.evitalz.homevitalz.cardfit.ui.activities.manualentry

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.CustomFactoryBGViewModel
import com.evitalz.homevitalz.cardfit.HandlerBG
import com.evitalz.homevitalz.cardfit.R
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.api.ApiManager
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.databinding.ActivityDiabetesBinding
import com.evitalz.homevitalz.cardfit.ui.viewmodels.GlucoseViewmodel
import kotlinx.android.synthetic.main.activity_diabetes.*
import java.util.*


class DiabetesActivity : AppCompatActivity() , HandlerBG {

    lateinit var binding : ActivityDiabetesBinding
    var unit =  ArrayList<String>()
    lateinit var dataAdapter : ArrayAdapter<String>
    var selectedItem : String="mg/dl"
    var testtype : String=""
    var flag = false

    private val viewModel : GlucoseViewmodel by lazy {
        ViewModelProvider(this, CustomFactoryBGViewModel(application, intent.extras!!.getLong("datetime"),
                intent.getParcelableExtra("devicereading"), intent.extras!!.getBoolean("update"))
        ).get(GlucoseViewmodel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDiabetesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setVariable(BR.viewmodel, viewModel)
        binding.handler= this

        unit.add("Before Breakfast")
        unit.add("After Breakfast")
        unit.add("Before Lunch")
        unit.add("After Lunch")
        unit.add("Before Dinner")
        unit.add("After Dinner")
        unit.add("Before Sleep")
        unit.add("After Sleep")
        unit.add("Fasting")
        unit.add("Other")


        dataAdapter = ArrayAdapter(this, R.layout.simple_drop_down_item, unit)
        binding.spinnerUnitlist.setAdapter(dataAdapter)

        addText()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.appbarmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnsave -> {
                onSaveClicked()
            }
            R.id.btncancel -> {
                finish()
            }

        }
        return true
    }

     fun onSaveClicked() {

        var value1:String =binding.etbglevelinmmol.text.toString()
        var value2:String =binding.etbglevelinmgdl.text.toString()
        testtype = binding.spinnerUnitlist.text.toString()
        if(intent.extras!!.getBoolean("update")){
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etbglevelinmgdl.text) &&
                    TextUtils.isEmpty(binding.etbglevelinmmol.text)
                    && TextUtils.isEmpty(testtype) ) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }
            else {
                val deviceReadingsparcel: Device_Readings?= intent.getParcelableExtra("devicereading")
                val deviceReadings = deviceReadingsparcel?.id?.let {
                    Device_Readings(it, deviceReadingsparcel.dreadid, Utility.getpregid(this),
                            value1,
                            testtype,
                            "", "", Calendar.getInstance().time.time, 0, "BloodGlucose",6, 1, "",
                            value2, binding.etnotes.text.toString(),
                            viewModel.datetime.time.time
                    )
                }


                if (deviceReadings != null) {
                    ApiManager.updatedata(this,deviceReadingsparcel.dreadid,value1,
                        testtype,"",
                        "",value2,binding.etnotes.text.toString(), viewModel.datetime.time.time)
                    viewModel.updatedevicereadings(deviceReadings)
                }
                Toast.makeText(this, "Data updated", Toast.LENGTH_LONG).show()
            }
            finish()
            return
        }
        else{
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etbglevelinmgdl.text) && TextUtils.isEmpty(binding.etbglevelinmmol.text)
                    && TextUtils.isEmpty(testtype)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {

                val deviceReadings = Device_Readings(0, 1, Utility.getpregid(this),
                        value1,
                        testtype,
                        "", "", Calendar.getInstance().time.time, 0, "BloodGlucose",6, 1, "",
                        value2, binding.etnotes.text.toString(),
                        viewModel.datetime.time.time
                )
                when(testtype){
                    "Fasting" -> {
                        AlertDialog.Builder(this@DiabetesActivity)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage("Do You want to set reminder for PostPrandial Test after 2 hrs?")
                                .setPositiveButton("Yes") { _, _ -> createalarm() }
                                .setNegativeButton("No") { _, _ -> finish() }
                                .show()
                        viewModel.insertdevicereadings(deviceReadings)
                    }
                    else ->{
                        viewModel.insertdevicereadings(deviceReadings)
                        finish()
                    }
                }

//                insertdata(this, "BloodGlucose", Utility.getpregid(this), value1, testtype, "", "", value2, binding.etnotes.text.toString(), Utility.simpleDateFormat.format(viewModel.datetime.time.time))
            }
            return
        }

    }


    override fun onDateClicked(view: View) {
        val today = Calendar.getInstance()
        DatePickerDialog(this,
                { view, year, month, dayOfMonth ->
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    viewModel.changeDate(cal.time)
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onTimeClicked(view: View) {
        val today = Calendar.getInstance()
        TimePickerDialog(this,
                { view, hourOfDay, minute ->
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                    viewModel.changeTime(cal.time)
                }, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), false).show()
    }

    override fun onimage1Clicked(view: View) {
        binding.image1.setBackgroundColor(Color.BLUE)
        binding.image2.setBackgroundColor(resources.getColor(R.color.gray))
        binding.image3.setBackgroundColor(resources.getColor(R.color.gray))
        testtype="Fasting"

    }

    override fun onimage2Clicked(view: View) {
        binding.image1.setBackgroundColor(resources.getColor(R.color.gray))
        binding.image2.setBackgroundColor(Color.BLUE)
        binding.image3.setBackgroundColor(resources.getColor(R.color.gray))
        testtype="PostPrandial"
    }

    override fun onimage3Clicked(view: View) {
        binding.image1.setBackgroundColor(resources.getColor(R.color.gray))
        binding.image2.setBackgroundColor(resources.getColor(R.color.gray))
        binding.image3.setBackgroundColor(Color.BLUE)
        testtype="Random"
    }

    fun addText(){
        binding.etbglevelinmmol.setFilters(arrayOf<InputFilter>(CustomRangeInputFilter(0.0, 100.0), DecimalDigitsInputFilter(1)))
        binding.etbglevelinmgdl.setFilters(arrayOf<InputFilter>(CustomRangeInputFilter(0.0, 400.0), DecimalDigitsInputFilter(0)))

        binding.etbglevelinmmol.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int) {
                if (!flag) {
                    flag = true
                    val bginmmol = etbglevelinmmol.getText().toString()
                    if (bginmmol != "") {
                        etbglevelinmgdl.setText(String.format("%.0f", bginmmol.toFloat() * 18))
                    } else {
                        etbglevelinmgdl.setText("")
                    }
                    flag = false
                }
            }

            override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.etbglevelinmgdl.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int) {
                if (!flag) {
                    flag = true
                    val bginmgdl = etbglevelinmgdl.getText().toString()
                    if (bginmgdl != "") {
                        etbglevelinmmol.setText(String.format("%.1f", bginmgdl.toFloat() / 18))
                    } else {
                        etbglevelinmmol.setText("")
                    }
                    flag = false
                }
            }

            override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun createalarm(){
        val dateinlong = viewModel.datetime.time.time
        val currenttimeinlong = Calendar.getInstance().time.time
        Log.d("datetime", "createalarm: ${currenttimeinlong-(dateinlong+7200000)} ")
        val intent = Intent(this, StartAlarmActivity::class.java)
        intent.action = "alarm_triggered"
        intent.putExtra("data", "sample")

        val pendingIntent = PendingIntent.getActivity(
                this.applicationContext, 234324243, intent, 0)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                +(currenttimeinlong-(dateinlong+7200000)) ] = pendingIntent

        Toast.makeText(this, "Alarm set for postprandial", Toast.LENGTH_SHORT).show()
        finish()
    }


}