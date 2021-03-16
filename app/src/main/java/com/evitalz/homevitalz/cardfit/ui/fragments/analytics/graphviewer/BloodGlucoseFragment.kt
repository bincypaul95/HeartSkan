package com.evitalz.homevitalz.cardfit.ui.fragments.analytics.graphviewer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.BR
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.databinding.BloodGlucoseFragmentBinding
import com.evitalz.homevitalz.cardfit.ui.fragments.analytics.AnalyticsViewModel
import com.evitalz.homevitalz.cardfit.ui.fragments.analytics.MyAxisValueFormatter
import com.evitalz.homevitalz.cardfit.ui.fragments.analytics.MyValueFormatter
import java.util.*

class BloodGlucoseFragment : Fragment(){
    var entries = ArrayList<Entry>()
    lateinit var binding : BloodGlucoseFragmentBinding
    val viewModel : AnalyticsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(
            AnalyticsViewModel::class.java
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BloodGlucoseFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(BR.viewModel,viewModel)
        viewModel.testResult.observe(requireActivity(), androidx.lifecycle.Observer { it ->
            it.forEach {
                Log.d("chart_test", it.dread2)
                it.type = viewModel.selecteddays
            }
            drawGraph(it)
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
    fun drawGraph(result: List<Device_Readings>) {

        Log.d("listsize", "drawGraph: ${result.size} ")
        binding.chart.clear()
        var reference =0L
        val colors : ArrayList<Int> = ArrayList<Int>()

        if (result.isEmpty()){
//            viewModel.heartrate.set("NA")
//            binding.rlheart.visibility=View.GONE
//            if (loopMediaPlayer != null) loopMediaPlayer!!.stop()
//            stopMediaPlayer()
            return
        }

        var referenceval: Long = result[0].datetime
        entries.clear()
        result.forEach { item ->
            Log.d("testTime", "${item.getHour()}")
            reference= item.datetime
            Log.d("referenceval", "${((item.datetime - referenceval)/100).toFloat()}")
                    entries.add(
                        Entry(
                            ((item.datetime - referenceval)/100).toFloat(),
                            item.dread5.toFloat()
                        )
                    )
                    colors.add(get_color("Blood Glucose", item.dread2, item.dread5.toFloat()))
        }

        val dataSet = LineDataSet(entries, "")
        dataSet.circleColors = colors
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet!!.color = Color.BLUE

        val linedata = LineData(dataSet)
        linedata.setValueTextSize(8f)
        linedata.setDrawValues(true)
        linedata.setValueFormatter(MyValueFormatter())
        dataSet.setDrawFilled(true);
        dataSet.cubicIntensity = 1.0f
        dataSet.setDrawIcons(false)
        dataSet.setDrawCircleHole(true)
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 5f
        dataSet.setDrawFilled(true)
        dataSet.formLineWidth = 1f
        dataSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        dataSet.formSize = 15f
        dataSet.fillColor = Color.WHITE
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.color = Color.rgb(23, 107, 239)

        val l: Legend = binding.chart.legend
        l.formSize = 12f
        l.form = Legend.LegendForm.CIRCLE
        l.textSize = 12f
        l.textColor = Color.BLACK
        l.xEntrySpace = 10f
        l.yEntrySpace = 5f
        val legendEntryA = LegendEntry()
        legendEntryA.form = Legend.LegendForm.SQUARE
        legendEntryA.formColor = Color.rgb(23, 107, 239)
        val legendEntryB = LegendEntry()
        legendEntryB.label = "Normal"
        legendEntryB.formColor = Color.rgb(23, 156, 82)
        val legendEntryC = LegendEntry()
        legendEntryC.label = "Diabetic"
        legendEntryC.formColor = Color.rgb(255, 62, 48)
        val legendEntryD = LegendEntry()
        legendEntryD.label = "Impaired Glucose"
        legendEntryD.formColor = Color.rgb(247, 181, 41)
        l.setCustom(Arrays.asList(legendEntryA, legendEntryB, legendEntryC, legendEntryD))

//                legendEntryA.label = "Blood Glucose"
//                l.setCustom(Arrays.asList(legendEntryA))

        binding.chart.let {

            when (viewModel.selecteddays) {
                0 -> {
                    it.xAxis.valueFormatter = MyAxisValueFormatter(referenceval)
//                    it.xAxis.labelCount = 7
                    it.xAxis.setDrawGridLines(false)
                    it.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    it.xAxis.axisLineColor = Color.blue(200)
                    it.xAxis.isEnabled = true
                    it.xAxis.granularity = 4f // only intervals of 1 day
                    it.xAxis.textSize = 8f
                }
                1 -> {
//                    it.xAxis.setValueFormatter(MyAxisValueFormatter(referenceval))
                    it.xAxis.labelCount = 7
                    it.xAxis.setDrawGridLines(false)
                    it.xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
                    it.xAxis.axisLineColor = Color.blue(200)
                    it.xAxis.isEnabled = false
                    it.xAxis.granularity = 4f // only intervals of 1 day
                    it.xAxis.textSize = 8f
                }
                2 -> {
                    it.xAxis.isEnabled = false
                    it.xAxis.labelCount = 7
                    it.xAxis.setDrawGridLines(false)
                    it.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    it.xAxis.axisLineColor = Color.blue(200)
                    it.xAxis.granularity = 4f // only intervals of 1 day
                    it.xAxis.textSize = 8f
                }
            }

            binding.chart.axisRight.isEnabled = false
            binding.chart.description.isEnabled = false
            binding.chart.setTouchEnabled(true)
            binding.chart.isDragEnabled = true
            binding.chart.setScaleEnabled(true)
            binding.chart.setPinchZoom(false)
            binding.chart.setDrawGridBackground(false)
            binding.chart.maxHighlightDistance = 300f
            binding.chart.legend.isEnabled=false

            val y: YAxis = binding.chart.axisLeft
//            y.axisMinimum = 0f
            y.setDrawLabels(true)
//            y.setLabelCount(4, true)
            y.textColor = Color.parseColor("#00574B")
            y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            y.setDrawGridLines(true)
            y.axisLineColor = Color.TRANSPARENT

        }
//        binding.chart.setVisibleXRangeMaximum(dataSet.xMax + 30)
        binding.chart.data = linedata
        binding.chart.animateY(500)
        binding.chart.invalidate()
        binding.chart.requestLayout()
    }

    private fun get_color(selectedname: String, type: String, parseFloat: Float): Int {
        when (selectedname) {
            "Blood Glucose" -> when (type) {
                "Before Breakfast",
                    "Before Lunch",
                    "Before Dinner",
                "Fasting" -> {
                    return when {
                        parseFloat in 101.0..125.0 -> {
                            Color.rgb(255, 184, 5)
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
                else-> {

                    return when {
                        parseFloat in 140.0..160.0 -> {
                            Color.rgb(255, 184, 5)
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
//               else -> {
//                    return when {
//                        parseFloat in 190.0..230.0 -> {
//                            Color.rgb(247, 181, 41)
//                        }
//                        parseFloat in 170.0..190.0 -> {
//                            Color.rgb(0, 187, 0)
//                        }
//                        parseFloat > 230 -> {
//                            Color.RED
//                        }
//                        else -> {
//                            Color.GRAY
//                        }
//                    }
//                }

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


}