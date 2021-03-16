package com.evitalz.homevitalz.cardfit.ui.activities.manualentry

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.CustomFactorySpO2ViewModel
import com.evitalz.homevitalz.cardfit.HandlerSpo2
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.databinding.ActivitySpo2Binding
import com.evitalz.homevitalz.cardfit.ui.viewmodels.Spo2Viewmodel
import java.util.*

class Spo2Activity : AppCompatActivity(), HandlerSpo2 {
    lateinit var binding: ActivitySpo2Binding

    private val viewModel : Spo2Viewmodel by lazy {
        ViewModelProvider(this , CustomFactorySpO2ViewModel(application ,  intent.extras!!.getLong("datetime"),
                intent.getParcelableExtra("devicereading"),intent.extras!!.getBoolean("update"))
        ).get(
            Spo2Viewmodel::class.java)
    }
    companion object{
        const val Spo2 = "Spo2"
        const val Pulse = "Pulse"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpo2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.setVariable(BR.viewmodel,viewModel )
        binding.handler= this
    }

    override fun onSaveClicked(view: View) {

        if(intent.extras!!.getBoolean("update")){
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etspo2.text) && TextUtils.isEmpty(binding.etpulse.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val deviceReadingsparcel: Device_Readings?= intent.getParcelableExtra("devicereading")
//                val deviceReadings = deviceReadingsparcel?.id?.let {
//                    Device_Readings(it,1,1,
//                            binding.etspo2.text.toString(),
//                            binding.etpulse.text.toString(),
//                            "","", Calendar.getInstance().time.time,0,5,1,"",
//                            "","",
//                            viewModel.datetime.time.time
//                    )
//                }

//                if (deviceReadings != null) {
//                    viewModel.updatedevicereadings(deviceReadings)
//                }
                Toast.makeText(this,"Data updated", Toast.LENGTH_LONG).show()
            }
            finish()
            return
        }
        else{
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etspo2.text) && TextUtils.isEmpty(binding.etpulse.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {

//                val deviceReadings = Device_Readings(0,1,1,
//                        binding.etspo2.text.toString(),
//                        binding.etpulse.text.toString(),
//                        "","", Calendar.getInstance().time.time,0,5,1,"",
//                        "","",
//                        viewModel.datetime.time.time
//                )
//                Toast.makeText(this,"Data Inserted", Toast.LENGTH_LONG).show()
//
//                viewModel.insertdevicereadings(deviceReadings)
            }
            finish()
            return
        }

    }

    override fun oncancelclicked(view: View) {
        finish()
    }

    override fun onDateClicked(view: View) {
        val today = Calendar.getInstance()
        DatePickerDialog(this ,object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                Log.d("datetimeSet" , "year $year month $month , dayofmonth $dayOfMonth")
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR , year)
                    set(Calendar.MONTH , month)
                    set(Calendar.DAY_OF_MONTH , dayOfMonth)
                }
                Log.d("datetimeSet" , "year ${cal.get(Calendar.YEAR)} month ${cal.get(Calendar.MONTH)} , dayofmonth ${cal.get(
                        Calendar.DAY_OF_MONTH)}")
                viewModel.changeDate(cal.time)
            }
        } , today.get(Calendar.YEAR) , today.get(Calendar.MONTH) , today.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onTimeClicked(view: View) {
        val today = Calendar.getInstance()
        TimePickerDialog(this , object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY , hourOfDay)
                    set(Calendar.MINUTE , minute)
                }
                viewModel.changeTime(cal.time)
            }
        } , today.get(Calendar.HOUR_OF_DAY) , today.get(Calendar.MINUTE)  , false ).show()
    }
}