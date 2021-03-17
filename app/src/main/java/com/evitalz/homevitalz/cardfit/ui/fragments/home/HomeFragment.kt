package com.evitalz.homevitalz.cardfit.ui.fragments.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evitalz.homevitalz.cardfit.*
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.databinding.*

import com.evitalz.homevitalz.cardfit.ui.activities.SplashActivity
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.ConnectDeviceActivity
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.GraphActivity
import com.evitalz.homevitalz.cardfit.ui.activities.exercise.ExerciseActivity

import com.evitalz.homevitalz.cardfit.ui.activities.manualentry.DiabetesActivity
import com.evitalz.homevitalz.cardfit.ui.activities.manualentry.Spo2Activity
import com.evitalz.homevitalz.cardfit.ui.activities.meal.LiquorActivity

import com.evitalz.homevitalz.cardfit.ui.activities.meal.MealActivity
import com.evitalz.homevitalz.cardfit.ui.activities.pilltake.PillActivity
import com.evitalz.homevitalz.cardfit.ui.activities.sleep.SleepActivity

import java.util.*


class HomeFragment : Fragment(), HandlerHome, HandlerShowTimeline, HandlerMeasure {

    lateinit var binding: FragmentHomeBinding
    var itemSelected = 0
   lateinit var dialog :Dialog
   lateinit var dialogbinding: ChooseDevManualBinding
    var alertDialog: androidx.appcompat.app.AlertDialog? = null
    private lateinit var preferences: SharedPreferences
    private val viewModel : HomeViewModel by lazy{ ViewModelProvider(this).get(
        HomeViewModel::class.java
    ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = requireContext()!!.getSharedPreferences(
            SplashActivity.PREF,
            AppCompatActivity.MODE_PRIVATE
        )


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.handler=this

        binding.setVariable(BR.viewmodel, viewModel)
        val adapter = ShowTimelineAdapter(requireActivity(), this)
        binding.rvhomelist.adapter = adapter
        binding.rvhomelist.layoutManager = LinearLayoutManager(context)

        viewModel.deviceReadings.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.isEmpty()) {
                binding.rvhomelist.visibility = View.GONE
                binding.ivnodataavailable.visibility = View.VISIBLE
            } else {
                binding.rvhomelist.visibility = View.VISIBLE
                binding.ivnodataavailable.visibility = View.GONE
                adapter.devicereadings = it
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(receiver1, IntentFilter("UserChanged"))
    }

    val receiver1 =object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
           Log.d("broadcast" , "onreceived")
            viewModel.changeDate(viewModel.currentCal.time, viewModel.currentCal.time)
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.homemenu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.btnsort -> {
                if (viewModel.type == 1) {
                    viewModel.type = 2
                } else {
                    viewModel.type = 1
                }
                viewModel.changeDate(viewModel.currentCal.time, viewModel.currentCal.time)
                Toast.makeText(requireActivity(), "Sorted", Toast.LENGTH_LONG).show()
                true
            }
            R.id.btnfilter -> {
                Toast.makeText(requireActivity(), "filter clicked", Toast.LENGTH_LONG).show()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onSpo2Clicked(view: View) {

        val singleChoiceItems =
            resources.getStringArray(R.array.test_array)
        alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
            .setTitle("Select Test Type")
            .setSingleChoiceItems(
                singleChoiceItems,
                itemSelected
            ) { _, selectedIndex -> itemSelected = selectedIndex }
            .setPositiveButton("Ok") { _, _ ->
                when(itemSelected){
                    0 -> {
                        val intent = Intent(context, Spo2Activity::class.java)
                        intent.putExtra("datetime", viewModel.datetime.time.time)
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(context, ConnectDeviceActivity::class.java)
                        intent.putExtra("datetime", viewModel.datetime.time.time)
                        startActivity(intent)
                    }
                }

            }
            .setNegativeButton("Cancel") { _, _ -> alertDialog!!.dismiss() }
            .show()

    }

    override fun onECGClicked(view: View) {
        val intent = Intent(context, ConnectDeviceActivity::class.java)
        intent.putExtra("datetime", viewModel.datetime.time.time)
        startActivity(intent)
    }

    override fun onBGClicked(view: View) {
        showdialog()
    }

    override fun onMealClicked(view: View) {

        val singleChoiceItems =
            resources.getStringArray(R.array.meal_array)
        alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
            .setTitle("Select Your Meal type")
            .setSingleChoiceItems(
                singleChoiceItems,
                itemSelected
            ) { _, selectedIndex -> itemSelected = selectedIndex }
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(context, MealActivity::class.java)
                intent.putExtra("datetime", viewModel.datetime.time.time)
                intent.putExtra("mealtype", singleChoiceItems.get(itemSelected))
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ -> alertDialog!!.dismiss() }
            .show()


    }

    override fun onExcersiceClicked(view: View) {
        val singleChoiceItems =
            resources.getStringArray(R.array.Excersice_Array)
        alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
            .setTitle("Select Your Exercise type")
            .setSingleChoiceItems(
                singleChoiceItems,
                itemSelected
            ) { _, selectedIndex -> itemSelected = selectedIndex }
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(context, ExerciseActivity::class.java)
                intent.putExtra("datetime", viewModel.datetime.time.time)
                intent.putExtra("exercisetype", singleChoiceItems.get(itemSelected))
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ -> alertDialog!!.dismiss() }
            .show()
    }

    override fun onPillTakenClicked(view: View) {
        val intent = Intent(context, PillActivity::class.java)
        intent.putExtra("datetime", viewModel.datetime.time.time)
        startActivity(intent)
    }

    override fun onSleepClicked(view: View) {
        val intent = Intent(context, SleepActivity::class.java)
        intent.putExtra("datetime", viewModel.datetime.time.time)
        startActivity(intent)
    }

    override fun onDateClicked(view: View) {
        val today = viewModel.currentCal
        DatePickerDialog(
            requireActivity(),
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    Log.d("datetimeSet", "year $year month $month , dayofmonth $dayOfMonth")
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    }
                    viewModel.changeDate(cal.time, cal.time)
                    Log.d(
                        "datetimeSet",
                        "year ${cal.get(Calendar.YEAR)} month ${cal.get(Calendar.MONTH)} , dayofmonth ${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }"
                    )
                }
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onPrevClicked(view: View) {
        viewModel.currentCal.let {
            onHandleDate(it.apply { add(Calendar.DAY_OF_MONTH, -1) })
        }
    }

    override fun onNextClicked(view: View) {
        viewModel.currentCal.let {
            onHandleDate(it.apply { add(Calendar.DAY_OF_MONTH, 1) })
        }
    }

    override fun onFilterClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onSortClicked(view: View) {
        Toast.makeText(requireActivity(), "Sortclicked", Toast.LENGTH_LONG).show()
    }

    override fun onLiquorClicked(view: View) {
        val intent = Intent(context, LiquorActivity::class.java)
        intent.putExtra("datetime", viewModel.datetime.time.time)
        startActivity(intent)
    }

    fun onHandleDate(cal: Calendar) {
        viewModel.changeDate(cal.time, cal.time)
    }

    private inner class ShowTimelineAdapter internal constructor(
        private val context: Context,
        val fragment: HomeFragment
    ):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val TYPE_FOOD = 1
        private val TYPE_EXCERSICE = 2
        private val TYPE_PILL = 3
        private val TYPE_SLEEP = 4
        private val TYPE_HEARTRATE = 5
        private val TYPE_SPO2 = 6
        private val TYPE_LIQUOR = 7

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        var devicereadings = emptyList<Device_Readings>()

          inner class ShowTimelineViewHolder(val binding: RowShowtimelineBinding) : RecyclerView.ViewHolder(
              binding.root
          )

          inner class ShowFoodViewHolder(val bindingfood: RowShowfoodBinding) : RecyclerView.ViewHolder(
              bindingfood.root
          )
        inner  class ShowExcersiceViewHolder(val bindingexcersice: RowShowexerciseBinding) : RecyclerView.ViewHolder(
            bindingexcersice.root
        )
        inner  class ShowPillViewHolder(val bindingpill: RowShowpilltakenBinding) : RecyclerView.ViewHolder(
            bindingpill.root
        )
        inner  class ShowSleepViewHolder(val bindingsleep: RowShowsleepBinding) : RecyclerView.ViewHolder(
            bindingsleep.root
        )
        inner  class ShowReadingviewholder(val bindngreading: RowShowreadingsBinding) : RecyclerView.ViewHolder(
            bindngreading.root
        )
        inner  class ShowSpo2Readingviewholder(val bindngspreading: RowSpo2Binding) : RecyclerView.ViewHolder(
                bindngspreading.root
        )
        inner  class ShowLiquorviewholder(val bindngliquor: RowLiquorBinding) : RecyclerView.ViewHolder(
            bindngliquor.root
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            when (viewType){
                TYPE_FOOD -> {
                    val bindingfood: RowShowfoodBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_showfood,
                        parent,
                        false
                    )
                    return ShowFoodViewHolder(bindingfood)
                }
                TYPE_EXCERSICE -> {
                    val bindingexcersice: RowShowexerciseBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_showexercise,
                        parent,
                        false
                    )
                    return ShowExcersiceViewHolder(bindingexcersice)
                }
                TYPE_PILL -> {
                    val bindingpill: RowShowpilltakenBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_showpilltaken,
                        parent,
                        false
                    )
                    return ShowPillViewHolder(bindingpill)
                }
                TYPE_SLEEP -> {
                    val bindingsleep: RowShowsleepBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_showsleep,
                        parent,
                        false
                    )
                    return ShowSleepViewHolder(bindingsleep)
                }
                TYPE_HEARTRATE -> {
                    val bindingreading: RowShowreadingsBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_showreadings,
                        parent,
                        false
                    )
                    return ShowReadingviewholder(bindingreading)
                }
                TYPE_SPO2 -> {
                    val bindingspo2reading: RowSpo2Binding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_spo2,
                        parent,
                        false
                    )
                    return ShowSpo2Readingviewholder(bindingspo2reading)
                }
                TYPE_LIQUOR -> {
                    val bindingliquor: RowLiquorBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_liquor,
                        parent,
                        false
                    )
                    return ShowLiquorviewholder(bindingliquor)
                }
                else ->{
                    val binding : RowShowtimelineBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.row_showtimeline,
                        parent,
                        false
                    )
                    return ShowTimelineViewHolder(binding)
                }
            }

        }

        override fun getItemCount(): Int {
            return  devicereadings.size
        }

        override fun getItemViewType(position: Int): Int {
            when(devicereadings[position].dtype) {
                "Food" -> return TYPE_FOOD
                "Exercise" -> return TYPE_EXCERSICE
                "PillTaken" -> return TYPE_PILL
                "Sleep" -> return TYPE_SLEEP
                "ECG" -> return TYPE_HEARTRATE
                "SpO2" -> return TYPE_SPO2
                "Liquor" -> return TYPE_LIQUOR
            }
            return super.getItemViewType(position)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) ==TYPE_FOOD) {
                (holder as ShowFoodViewHolder)

                holder.bindingfood.linshowtimeline.setOnLongClickListener {
                    onDeleteClicked(deviceReadings = devicereadings[position])
                    true
                }
                holder.bindingfood.devicereadings=devicereadings[position]
                holder.bindingfood.setVariable(BR.handler, fragment)
                holder.bindingfood.executePendingBindings()
            }
            if (getItemViewType(position) == TYPE_LIQUOR) {
                (holder as ShowLiquorviewholder)
                holder.bindngliquor.linshowtimeline.setOnLongClickListener {
                    onDeleteClicked(deviceReadings = devicereadings[position])
                    true
                }
                holder.bindngliquor.devicereadings=devicereadings[position]
                holder.bindngliquor.setVariable(BR.handler, fragment)
                holder.bindngliquor.executePendingBindings()
            }
            if (getItemViewType(position) == TYPE_EXCERSICE) {
                (holder as ShowExcersiceViewHolder)
                holder.bindingexcersice.linshowtimeline.setOnLongClickListener {
                    onDeleteClicked(deviceReadings = devicereadings[position])
                    true
                }
                holder.bindingexcersice.devicereadings=devicereadings[position]
                holder.bindingexcersice.setVariable(BR.handler, fragment)
                holder.bindingexcersice.executePendingBindings()
            }
            if (getItemViewType(position) == TYPE_PILL) {
                (holder as ShowPillViewHolder)
                holder.bindingpill.linshowtimeline.setOnLongClickListener {
                    onDeleteClicked(deviceReadings = devicereadings[position])
                    true
                }
                holder.bindingpill.devicereadings=devicereadings[position]
                holder.bindingpill.setVariable(BR.handler, fragment)
                holder.bindingpill.executePendingBindings()
            }
            if (getItemViewType(position) == TYPE_SLEEP) {
                (holder as ShowSleepViewHolder)
                holder.bindingsleep.linshowtimeline.setOnLongClickListener {
                    onDeleteClicked(deviceReadings = devicereadings[position])
                    true
                }
                holder.bindingsleep.devicereadings=devicereadings[position]
                holder.bindingsleep.setVariable(BR.handler, fragment)
                holder.bindingsleep.executePendingBindings()
            }
            if (getItemViewType(position) == TYPE_HEARTRATE) {
                (holder as ShowReadingviewholder)
                holder.bindngreading.linshowtimeline.setOnLongClickListener {
                    onDeleteClicked(deviceReadings = devicereadings[position])
                    true
                }
                holder.bindngreading.devicereadings=devicereadings[position]
                holder.bindngreading.setVariable(BR.handlerreading, fragment)
                holder.bindngreading.executePendingBindings()
            }
            if (getItemViewType(position) ==TYPE_SPO2) {
                (holder as ShowSpo2Readingviewholder)
                holder.bindngspreading.linshowtimeline.setOnLongClickListener {
                    onDeleteClicked(deviceReadings = devicereadings[position])
                    true
                }
                holder.bindngspreading.devicereadings=devicereadings[position]
                holder.bindngspreading.setVariable(BR.handlerreading, fragment)
                holder.bindngspreading.executePendingBindings()
            }
        }


        fun onDeleteClicked(deviceReadings: Device_Readings) {

//            val swipeHelper= object : SwipeHelper(context, binding.rvhomelist) {
//                override fun instantiateUnderlayButton(
//                    viewHolder: RecyclerView.ViewHolder?,
//                    underlayButtons: MutableList<UnderlayButton>?
//                ) {
//                    underlayButtons?.add( SwipeHelper.UnderlayButton(
//                        "Delete",0,Color.parseColor("#FF3C30"),
//                        SwipeHelper.UnderlayButtonClickListener {
//                            viewModel.deletedevicereading(deviceReadings)
//                            Toast.makeText(context,"Deleted", Toast.LENGTH_LONG).show()
//                        }
//                    ))
//                }
//
//            }
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you want to Delete?")
            val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                viewModel.deletedevicereading(deviceReadings)
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                dialog.dismiss()
                            }
                        }
                    }
            builder.setPositiveButton("Yes", dialogClickListener)
            builder.setNegativeButton("No", dialogClickListener)
            val dialog = builder.create()
            dialog.show()
        }

    }

    override fun onUpdateClicked(deviceReadings: Device_Readings) {

        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to Edit?")
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        when (deviceReadings.dtype) {
                            "Food" -> {
                                val intent = Intent(requireActivity(), MealActivity::class.java)
                                intent.putExtra("devicereading", deviceReadings)
                                intent.putExtra("datetime", deviceReadings.datetime)
                                intent.putExtra("update", true)
                                startActivity(intent)
                            }
                            "Exercise" -> {
                                val intent = Intent(requireActivity(), ExerciseActivity::class.java)
                                intent.putExtra("devicereading", deviceReadings)
                                intent.putExtra("datetime", deviceReadings.datetime)
                                intent.putExtra("update", true)
                                startActivity(intent)
                            }
                            "PillTaken" -> {
                                val intent = Intent(requireActivity(), PillActivity::class.java)
                                intent.putExtra("devicereading", deviceReadings)
                                intent.putExtra("datetime", deviceReadings.datetime)
                                intent.putExtra("update", true)
                                startActivity(intent)
                            }
                            "Sleep" -> {
                                val intent = Intent(requireActivity(), SleepActivity::class.java)
                                intent.putExtra("devicereading", deviceReadings)
                                intent.putExtra("datetime", deviceReadings.datetime)
                                intent.putExtra("update", true)
                                startActivity(intent)
                            }
                            "BloodGlucose" -> {
                                val intent = Intent(requireActivity(), DiabetesActivity::class.java)
                                intent.putExtra("devicereading", deviceReadings)
                                intent.putExtra("datetime", deviceReadings.datetime)
                                intent.putExtra("update", true)
                                startActivity(intent)
                            }
                            "Liquor" -> {
                                val intent = Intent(requireActivity(), LiquorActivity::class.java)
                                intent.putExtra("devicereading", deviceReadings)
                                intent.putExtra("datetime", deviceReadings.datetime)
                                intent.putExtra("update", true)
                                startActivity(intent)
                            }
                        }
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }
        builder.setPositiveButton("Yes", dialogClickListener)
        builder.setNegativeButton("No", dialogClickListener)
        val dialog = builder.create()
        dialog.show()

    }

    override fun onNoteClicked(deviceReadings: Device_Readings) {

        val intent= Intent(requireContext(), GraphActivity::class.java)
        intent.putExtra("ECG_graphvalues", deviceReadings.dread5)
        intent.putExtra("ECG_case", deviceReadings.dread1)
        intent.putExtra("ECG_value", deviceReadings.dread2)
        intent.putExtra("ECG_deviceplacement", deviceReadings.dread3)
        intent.putExtra("type", 1)
        startActivity(intent)

    }

    fun showdialog(){
         dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialogbinding = ChooseDevManualBinding.inflate(layoutInflater)
        dialog.setContentView(dialogbinding.root)
        dialogbinding.handler= this

        val window: Window? = dialog.window
        val display = window?.windowManager?.defaultDisplay
        if (display != null) {
            dialog.window?.setLayout(
                (display.width * 0.95).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.show()
    }

    override fun onManualClicked(view: View) {
        val intent = Intent(context, DiabetesActivity::class.java)
        intent.putExtra("datetime", viewModel.datetime.time.time)
        startActivity(intent)
        dialog.dismiss()
    }

    override fun onWirelessClicked(view: View) {
//        val intent : Intent = if(Utility.getGlucoseBluetoothAddress(requireContext()).isEmpty()){
//            Intent(context, GlucosePairActivity::class.java).apply {
//                putExtra("is_first" , true)
//            }
//        }else{
//            Intent(context, ConnectGlukoActivity::class.java)
//
//        }
//        startActivity(intent)
//        dialog.dismiss()
    }

}

