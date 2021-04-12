package com.evitalz.homevitalz.heartskan.ui.fragments.analytics

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.evitalz.homevitalz.heartskan.BR
import com.github.mikephil.charting.data.Entry
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.evitalz.homevitalz.heartskan.HandlerAnalytics
import com.evitalz.homevitalz.heartskan.R
import com.evitalz.homevitalz.heartskan.databinding.AnalyticsFragmentBinding
import java.util.*

@RequiresApi(Build.VERSION_CODES.M)
class AnalyticsFragment : Fragment(), HandlerAnalytics {

    lateinit var binding: AnalyticsFragmentBinding

    companion object {
        fun newInstance() = AnalyticsFragment()
    }

    var entries = ArrayList<Entry>()
    var loopMediaPlayer: LoopMediaPlayer? = null
    var heartbeatAnimation: Animation? = null
    var averagelist = ArrayList<Int>()

    private val viewModel: AnalyticsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(
            AnalyticsViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AnalyticsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        heartbeatAnimation= AnimationUtils.loadAnimation(activity, R.anim.bounce)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPagerAdapter = MyPagerAdapter(this)

        binding.viewPager.adapter = viewPagerAdapter
//        val pageOffset = resources.getDimensionPixelOffset(R.dimen.page_margin).toFloat()
//        val pageMargin = resources.getDimensionPixelOffset(R.dimen.page_margin).toFloat()
//        binding.viewPager.setPageTransformer { page: View, position: Float ->
//            val myOffset = position * -(2 * pageOffset + pageMargin)
//            if (binding.viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
//                if (ViewCompat.getLayoutDirection(binding.viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
//                    page.translationX = -myOffset
//                } else {
//                    page.translationX = myOffset
//                }
//            } else {
//                page.translationY = myOffset
//            }
//        }

        viewModel.selecteddays = 0
        onHandleDate(Calendar.getInstance())
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
        binding.handler = this
        Log.d("setdte", "onViewCreated: ${viewModel.strDate}")
        binding.setVariable(BR.viewModel, viewModel)


        val adapter = AnalyticsAdapter(requireActivity(), this)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
//                Toast.makeText(context, "Selected position: ${position}",
//                    Toast.LENGTH_SHORT).show()
                if(position == 0 && viewModel.testResult.value != null){
                    adapter.devicereadings = viewModel.testResult.value!!
                }else if(viewModel.ecgResult.value != null){
                    adapter.devicereadings = viewModel.ecgResult.value!!
                }
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.testResult.observe(requireActivity(), androidx.lifecycle.Observer {
            it.forEach {
                Log.d("chart_test", "${it.dread2}")
                it.type = viewModel.selecteddays
            }
            Log.d("chart_test", "${it.size}")
            if(binding.viewPager.currentItem == 1) {
                adapter.devicereadings = it
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.ecgResult.observe(requireActivity(), androidx.lifecycle.Observer {
            it.forEach {
                Log.d("chart_test", "${it.dread2}")
                it.type = viewModel.selecteddays
            }
            Log.d("chart_test", "${it.size}")
            if(binding.viewPager.currentItem == 0) {
                adapter.devicereadings = it
                adapter.notifyDataSetChanged()
            }
        })
        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.currentCal.let {
                    when (tab?.text) {
                        getString(R.string.day) -> {
                            viewModel.selecteddays = 0
                            onHandleDate(it)
                        }
                        getString(R.string.week) -> {

                            viewModel.selecteddays = 1
                            onHandlerWeek(it)
                            //Log.d("timeCheck" , "start $startDate end $endDate")
                        }
                        getString(R.string.month) -> {

                            viewModel.selecteddays = 2
                            onHandleMonth(it)
                            //Log.d("timeCheck" , "start $startDate end $endDate")
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

        })

    }


    override fun onDateClicked(view: View) {
        if (viewModel.selecteddays != 0)
            return
        val today = viewModel.currentCal
        DatePickerDialog(
            requireActivity(),
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker?,
                    year: Int,
                    month: Int,
                    dayOfMonth: Int
                ) {

                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    }
                    viewModel.changeDate(cal.time, cal.time)
                }
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onbgclicked(view: View) {
        viewModel.selecteditem.set(0)
        viewModel.changeDate(viewModel.currentCal.time, viewModel.currentCal.time)
        entries.clear()
//        binding.ivbloodglucose.isEnabled=false
//        binding.ivecg.isEnabled=true
    }

    override fun onecgclicked(view: View) {
        viewModel.selecteditem.set(1)
        viewModel.changeDate(viewModel.currentCal.time, viewModel.currentCal.time)
        entries.clear()
//        binding.ivecg.isEnabled=false
//        binding.ivbloodglucose.isEnabled=true

    }


    override fun ondayclicked(view: View) {
        binding.tvday.background = resources.getDrawable(R.drawable.button)
        binding.tvday.setTextColor(resources.getColor(R.color.white))
        binding.tvweek.background = resources.getDrawable(R.color.white)
        binding.tvweek.setTextColor(resources.getColor(R.color.black))
        binding.tvmonth.background = resources.getDrawable(R.color.white)
        binding.tvmonth.setTextColor(resources.getColor(R.color.black))

        viewModel.selecteddays = 0
        onHandleDate(Calendar.getInstance())
    }

    override fun onweekclicked(view: View) {
        binding.tvweek.background = resources.getDrawable(R.drawable.button)
        binding.tvweek.setTextColor(resources.getColor(R.color.white))
        binding.tvday.background = resources.getDrawable(R.color.white)
        binding.tvday.setTextColor(resources.getColor(R.color.black))
        binding.tvmonth.background = resources.getDrawable(R.color.white)
        binding.tvmonth.setTextColor(resources.getColor(R.color.black))
        viewModel.selecteddays = 1
        onHandleweek(Calendar.getInstance())
    }

    override fun onmonthclicked(view: View) {
        binding.tvmonth.background = resources.getDrawable(R.drawable.button)
        binding.tvmonth.setTextColor(resources.getColor(R.color.white))
        binding.tvweek.background = resources.getDrawable(R.color.white)
        binding.tvweek.setTextColor(resources.getColor(R.color.black))
        binding.tvday.background = resources.getDrawable(R.color.white)
        binding.tvday.setTextColor(resources.getColor(R.color.black))
        viewModel.selecteddays = 2
        onHandlemonth(Calendar.getInstance())
    }

    override fun onNext(view: View) {

        viewModel.currentCal.let {
            when (viewModel.selecteddays) {
                0 -> {
                    onHandleDate(it.apply { add(Calendar.DAY_OF_MONTH, 1) })
                }
                1 -> {
                    onHandlerWeek(it.apply { add(Calendar.WEEK_OF_MONTH, 1) })
                }
                2 -> {
                    onHandleMonth(it.apply { add(Calendar.MONTH, 1) })
                }
            }
        }
    }



    override fun onPrevious(view: View) {

        viewModel.currentCal.let {
            when (viewModel.selecteddays) {
                0 -> {
                    onHandleDate(it.apply { add(Calendar.DAY_OF_MONTH, -1) })
                }
                1 -> {
                    onHandlerWeek(it.apply { add(Calendar.WEEK_OF_MONTH, -1) })
                }
                2 -> {
                    onHandleMonth(it.apply { add(Calendar.MONTH, -1) })
                }
            }
        }

    }

    fun onHandleDate(cal: Calendar) {

        viewModel.changeDate(cal.time, cal.time)
    }

    fun onHandlerWeek(cal: Calendar) {
        cal.firstDayOfWeek = Calendar.SUNDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = cal.time
        cal.set(Calendar.DAY_OF_WEEK, 7)
        val endDate = cal.time
        viewModel.changeDate(startDate, endDate)
    }

    fun onHandlemonth(cal: Calendar) {
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = cal.time
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = cal.time
        viewModel.changeDate(startDate, endDate)
    }

    fun onHandleweek(cal: Calendar) {
        cal.firstDayOfWeek = Calendar.SUNDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = cal.time
        cal.set(Calendar.DAY_OF_WEEK, 7)
        val endDate = cal.time
        viewModel.changeDate(startDate, endDate)
    }

    fun onHandleMonth(cal: Calendar) {
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = cal.time
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = cal.time
        viewModel.changeDate(startDate, endDate)
    }


    private fun get_color(selectedname: String, type: String, parseFloat: Float): Int {
        when (selectedname) {
            "Blood Glucose" -> when (type) {

                "Fasting" -> {
                    return when {
                        parseFloat in 101.0..125.0 -> {
                            Color.rgb(247, 181, 41)
                        }
                        parseFloat in 80.0..100.0 -> {
                            Color.rgb(0, 187, 0)
                        }
                        parseFloat > 125 -> {
                            Color.RED
                        }
                        else -> {
                            Color.GRAY
                        }
                    }

                }
                "PostPrandial" -> {

                    return when {
                        parseFloat in 140.0..160.0 -> {
                            Color.rgb(247, 181, 41)
                        }
                        parseFloat in 120.0..140.0 -> {
                            Color.rgb(0, 187, 0)
                        }
                        parseFloat > 160 -> {
                            Color.RED
                        }
                        else -> {
                            Color.GRAY
                        }
                    }
                }
                "Random" -> {
                    return when {
                        parseFloat in 190.0..230.0 -> {
                            Color.rgb(247, 181, 41)
                        }
                        parseFloat in 170.0..190.0 -> {
                            Color.rgb(0, 187, 0)
                        }
                        parseFloat > 230 -> {
                            Color.RED
                        }
                        else -> {
                            Color.GRAY
                        }
                    }
                }

            }
            "HeartRate" -> when {
                parseFloat in 60.0..100.0 -> {
                    return Color.rgb(23, 156, 82)
                }
                parseFloat < 60.0 -> {
                    return Color.rgb(247, 181, 41)
                }
                parseFloat > 100.0 -> {
                    return Color.rgb(255, 64, 48)
                }
            }
        }
        return Color.rgb(168, 22, 171)
    }

    fun changeheartspeed(sec: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            var beat = 0

            try {
                if (sec != "NA") {
                    beat = sec.toInt()
                }
                if (loopMediaPlayer != null && loopMediaPlayer!!.mCurrentPlayer != null) {
                    if (loopMediaPlayer!!.mCurrentPlayer?.isPlaying()!!) {
                        loopMediaPlayer!!.stop()
                        loopMediaPlayer!!.release()
                        loopMediaPlayer = null
                    }
                }
                if (beat > 0) {
                    loopMediaPlayer = context?.let {
                        LoopMediaPlayer.create(
                            it, R.raw.beatone, beatcount(beat)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("mediaplayer", e.toString() + "")
                if (loopMediaPlayer != null) {
                    try {
                        loopMediaPlayer!!.stop()
                        loopMediaPlayer!!.release()
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                        loopMediaPlayer = null
                    }
                }
            }
        }
    }

    fun EcgDuration(bpm: Int): Long {
        return (60000 / bpm / 2).toLong()
    }

    private fun beatcount(beat: Int): Float {
        return beat.toFloat() / 78
    }

    private fun calculateAverage(marks: List<Int>): Double {
        var sum = 0
        if (!marks.isEmpty()) {
            for (mark in marks) {
                sum += mark
            }
            return sum.toDouble() / marks.size
        }
        return sum.toDouble()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDestroy() {
        super.onDestroy()
//       stopMediaPlayer()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
//        if (loopMediaPlayer != null) loopMediaPlayer!!.stop()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
//        if (loopMediaPlayer != null) loopMediaPlayer!!.start()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun stopMediaPlayer() {
        if (loopMediaPlayer != null && loopMediaPlayer!!.mCurrentPlayer != null &&
            loopMediaPlayer!!.mCurrentPlayer!!.isPlaying
        )
            loopMediaPlayer!!.release()
    }


}

