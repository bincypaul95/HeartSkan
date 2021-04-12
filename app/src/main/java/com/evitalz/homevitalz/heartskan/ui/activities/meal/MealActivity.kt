package com.evitalz.homevitalz.heartskan.ui.activities.meal

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.heartskan.*
import com.evitalz.homevitalz.heartskan.api.ApiManager
import com.evitalz.homevitalz.heartskan.database.Device_Readings
import com.evitalz.homevitalz.heartskan.databinding.ActivityMealBinding
import com.evitalz.homevitalz.heartskan.ui.viewmodels.MealViewmodel
import java.util.*


class MealActivity : AppCompatActivity(), HandlerMeal {

    lateinit var binding: ActivityMealBinding
    var unit =  ArrayList<String>()
    lateinit var dataAdapter : ArrayAdapter<String>
    var selectedItem : String=""

    private val viewmodel : MealViewmodel by lazy {
        ViewModelProvider(
            this, CustomFactoryMealViewModel(
                application, intent.extras!!.getLong("datetime"),
                intent.getParcelableExtra("devicereading"), intent.extras!!.getBoolean("update")
            )
        ).get(MealViewmodel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler= this
        binding.setVariable(BR.viewmodel, viewmodel)

        unit.add("Lite")
        unit.add("Medium")
        unit.add("Heavy")
        unit.add("Pieces")
        unit.add("ml")
        unit.add("gm")
        unit.add("cup")
        unit.add("glass")
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
         val mealtype =intent.extras!!.getString("mealtype").toString()
         val foodname = binding.etfoodname.text.toString()
         val quantity =binding.etquantity.text.toString()
         selectedItem= binding.spinnerUnitlist.text.toString()
        if(intent.extras!!.getBoolean("update")){
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etfoodname.text) && TextUtils.isEmpty(binding.etquantity.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val deviceReadingsparcel: Device_Readings? = intent.getParcelableExtra("devicereading")
                val deviceReadings = deviceReadingsparcel?.dread1?.let {
                    Device_Readings(
                        deviceReadingsparcel?.id,
                        deviceReadingsparcel.dreadid,
                        Utility.getpregid(this),
                        it,
                        binding.etfoodname.text.toString(),
                        binding.etquantity.text.toString(),
                        selectedItem,
                        viewmodel.datetime.time.time,
                        0,"Food",
                        1,
                        1,
                        "",
                        "",
                        binding.etnotes.text.toString(),
                        viewmodel.datetime.time.time
                    )
                }
                Toast.makeText(this, "Data updated", Toast.LENGTH_LONG).show()

                if (deviceReadings != null) {
                    ApiManager.updatedata(this,deviceReadingsparcel.dreadid,deviceReadingsparcel.dread1,
                        binding.etfoodname.text.toString(), binding.etquantity.text.toString(),
                        selectedItem,"",binding.etnotes.text.toString(), viewmodel.datetime.time.time)
                    viewmodel.updatedevicereadings(deviceReadings)
                }
            }
            finish()
            return
        }
        else{
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etfoodname.text) && TextUtils.isEmpty(binding.etquantity.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {

                val deviceReadings = Device_Readings(
                    0,
                    1,
                    Utility.getpregid(this),
                    mealtype,
                    binding.etfoodname.text.toString(),
                    binding.etquantity.text.toString(),
                    selectedItem,
                    viewmodel.datetime.time.time,
                    0,"Food",
                    1,
                    1,
                    "",
                    "",
                    binding.etnotes.text.toString(),
                    viewmodel.datetime.time.time
                )
                Toast.makeText(this, "Data Inserted", Toast.LENGTH_LONG).show()
                    viewmodel.insertdevicereadings(deviceReadings)
            }
            finish()
            return
        }

    }

    override fun onDateClicked(view: View) {
        val today = Calendar.getInstance()
        DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                Log.d("datetimeSet", "year $year month $month , dayofmonth $dayOfMonth")
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                Log.d(
                    "datetimeSet",
                    "year ${cal.get(Calendar.YEAR)} month ${cal.get(Calendar.MONTH)} , dayofmonth ${
                        cal.get(
                            Calendar.DAY_OF_MONTH
                        )
                    }"
                )
                viewmodel.changeDate(cal.time)
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onTimeClicked(view: View) {
        val today = Calendar.getInstance()
        TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                viewmodel.changeTime(cal.time)
            }
        }, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), false).show()
    }

}