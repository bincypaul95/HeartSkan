package com.evitalz.homevitalz.cardfit.ui.activities.exercise

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.*

import com.evitalz.homevitalz.cardfit.api.ApiManager
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.databinding.ActivityExerciseBinding
import com.evitalz.homevitalz.cardfit.ui.viewmodels.ExcersiceViewModel
import java.util.*

class ExerciseActivity : AppCompatActivity(), HandlerExcersice {
    lateinit var binding: ActivityExerciseBinding
    var unit =  ArrayList<String>()
    lateinit var dataAdapter : ArrayAdapter<String>
    var selectedItem : String=""
    private val viewmodel: ExcersiceViewModel by lazy {
        ViewModelProvider( this, CustomFactoryExcersiceViewModel(application , intent.extras!!.getLong("datetime"),intent.getParcelableExtra("devicereading"),intent.extras!!.getBoolean("update") )).get(
            ExcersiceViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding= ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler=this
        binding.setVariable(BR.viewmodel,viewmodel)


        unit.add("hr")
        unit.add("hrs")
        unit.add("mins")
        unit.sort()
        
        dataAdapter = ArrayAdapter(this, R.layout.simple_drop_down_item, unit)
        binding.spinnerUnitlist.setAdapter(dataAdapter)
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
            val type =intent.extras!!.getString("exercisetype").toString()
            val duration =binding.etduration.text.toString()
            selectedItem = binding.spinnerUnitlist.text.toString()
            val notes =binding.etnotes.text.toString()

        if(intent.extras!!.getBoolean("update")){
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etduration.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val deviceReadingsparcel: Device_Readings? = intent.getParcelableExtra("devicereading")
                val deviceReadings = deviceReadingsparcel?.dread1?.let {
                    Device_Readings(deviceReadingsparcel.id,1, Utility.getpregid(this),
                            it,
                            duration,
                        selectedItem,"",viewmodel.datetime.time.time,0,"Exercise",2,1,"",
                            "",notes,
                            viewmodel.datetime.time.time
                    )

                }

                if (deviceReadings != null) {
                    ApiManager.updatedata(this,deviceReadingsparcel.dreadid,deviceReadingsparcel.dread1,duration,selectedItem,"","",notes, viewmodel.datetime.time.time)
                    viewmodel.updatedevicereadings(deviceReadings)
                }
                Toast.makeText(this,"Data updated", Toast.LENGTH_LONG).show()
            }
            finish()
            return
        }

        val replyIntent = Intent()
        if (TextUtils.isEmpty(binding.etduration.text)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        } else {

            val deviceReadings = Device_Readings(0,0, Utility.getpregid(this),
                    type,
                    duration,
                selectedItem,"",viewmodel.datetime.time.time,0,"Exercise",2,1,"",
                    "",notes,
                    viewmodel.datetime.time.time
            )
            Toast.makeText(this,"Data Inserted", Toast.LENGTH_LONG).show()
            viewmodel.insertdevicereadings(deviceReadings)
        }
        finish()
        return
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
                viewmodel.changeDate(cal.time)
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
                viewmodel.changeTime(cal.time)
            }
        } , today.get(Calendar.HOUR_OF_DAY) , today.get(Calendar.MINUTE)  , false ).show()
    }
}