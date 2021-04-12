package com.evitalz.homevitalz.heartskan.ui.activities.sleep

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.heartskan.CustomFactorySleepViewModel
import com.evitalz.homevitalz.heartskan.HandlerSleep
import com.evitalz.homevitalz.heartskan.R
import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.api.ApiManager
import com.evitalz.homevitalz.heartskan.database.Device_Readings
import com.evitalz.homevitalz.heartskan.databinding.ActivitySleepBinding
import com.evitalz.homevitalz.heartskan.ui.viewmodels.SleepViewmodel
import java.util.*

class SleepActivity : AppCompatActivity() , HandlerSleep {
    lateinit var binding: ActivitySleepBinding
    lateinit var totalhr : String

    private val viewmodel: SleepViewmodel by lazy{
        ViewModelProvider( this,
                CustomFactorySleepViewModel(application , intent.extras!!.getLong("datetime"),intent.getParcelableExtra("devicereading"),intent.extras!!.getBoolean("update"))
        ).get(SleepViewmodel::class.java)
    }

    var starttime : Long= 0
    var endtime : Long= 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySleepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler=this
        binding.setVariable(BR.viewmodel, viewmodel)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbarmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.btnsave ->{
                onSaveClicked()
                true
            }
            R.id.btncancel -> {
                finish()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onstarttimeClicked(view: View) {
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance()
        DatePickerDialog(this@SleepActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            TimePickerDialog(this@SleepActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                viewmodel.setStartDateTime(cal.time)
                starttime = cal.time.time
            }, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), false
            ).show()
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE)
        ).show()
    }

    override fun onendtimeClicked(view: View) {
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance()
        DatePickerDialog(this@SleepActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

            TimePickerDialog(this@SleepActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                viewmodel.setEndDateTime(cal.time)
                endtime = cal.time.time
            }, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), false
            ).show()
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE)
        ).show()
    }

    fun onSaveClicked() {

        if(intent.extras!!.getBoolean("update")){
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etstarttime.text) && TextUtils.isEmpty(binding.etendtime.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                checktime()
                if(starttime != 0L && endtime != 0L && endtime>starttime){
                    totalhr = totalhrs(starttime, endtime)
                }else{
                    totalhr =""
                }

                val deviceReadingsparcel: Device_Readings? = intent.getParcelableExtra("devicereading")
                val deviceReadings = deviceReadingsparcel?.id?.let {
                    Device_Readings(
                            it, deviceReadingsparcel.dreadid, Utility.getpregid(this),
                            binding.etstarttime.text.toString(),
                            binding.etendtime.text.toString(),
                            "", "", Calendar.getInstance().time.time, 0, "Sleep",4, 1, "",
                            totalhr, binding.etnotes.text.toString(),
                            viewmodel.datetime.time.time
                    )
                }

                if(totalhr.equals("0 hrs 0 mins")){
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                }else{
                    if (deviceReadings != null) {
                        ApiManager.updatedata(this,deviceReadingsparcel.dreadid,binding.etstarttime.text.toString(),
                            binding.etendtime.text.toString(),"",
                            "","",binding.etnotes.text.toString(), viewmodel.datetime.time.time)
                        viewmodel.updatedevicereadings(deviceReadings)
                    }
                }
                Toast.makeText(this, "Data updated", Toast.LENGTH_LONG).show()
            }
            finish()
        }
        else{
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etstarttime.text) && TextUtils.isEmpty(binding.etendtime.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                checktime()
                if(starttime != 0L && endtime != 0L && endtime> starttime){
                    totalhr = totalhrs(starttime, endtime)
                }

                val deviceReadings = Device_Readings(
                        0, 1, Utility.getpregid(this),
                        binding.etstarttime.text.toString(),
                        binding.etendtime.text.toString(),
                        "", "", Calendar.getInstance().time.time, 0,"Sleep", 4, 1, "",
                        totalhr, binding.etnotes.text.toString(),
                        viewmodel.datetime.time.time
                )
                if(totalhr.equals("0 hrs 0 mins")){
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                }else{
                    viewmodel.insertdevicereadings(deviceReadings)
                    Toast.makeText(this, "Data Inserted", Toast.LENGTH_LONG).show()
                }

            }
            finish()
        }

    }

    override fun onDateClicked(view: View) {
        val today = Calendar.getInstance()
        DatePickerDialog(this , { view, year, month, dayOfMonth ->
            Log.d("datetimeSet" , "year $year month $month , dayofmonth $dayOfMonth")
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR , year)
                set(Calendar.MONTH , month)
                set(Calendar.DAY_OF_MONTH , dayOfMonth)
            }
            Log.d("datetimeSet" , "year ${cal.get(Calendar.YEAR)} month ${cal.get(Calendar.MONTH)} , dayofmonth ${cal.get(
                    Calendar.DAY_OF_MONTH)}")
            viewmodel.changeDate(cal.time)
        }, today.get(Calendar.YEAR) , today.get(Calendar.MONTH) , today.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onTimeClicked(view: View) {
        val today = Calendar.getInstance()
        TimePickerDialog(this , { view, hourOfDay, minute ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY , hourOfDay)
                set(Calendar.MINUTE , minute)
            }
            viewmodel.changeTime(cal.time)
        }, today.get(Calendar.HOUR_OF_DAY) , today.get(Calendar.MINUTE)  , false ).show()
    }

    fun totalhrs(starttime: Long, endtime: Long) : String {
        Log.d("totalhours", "totalhrs: $endtime $starttime")
            val mills: Long = endtime - starttime
            val hours = (mills / (1000 * 60 * 60)).toInt()
            val mins = (mills  / (1000 * 60)).toInt() / 60
            val diff = "$hours hrs $mins mins"
        Log.d("totalhours", "totalhrs: $diff ")
            return diff
    }

    fun checktime(){
        if(endtime ==0L){
            endtime= Calendar.getInstance().timeInMillis
        }
        if(starttime == 0L){
            starttime = Calendar.getInstance().timeInMillis
        }
    }
}