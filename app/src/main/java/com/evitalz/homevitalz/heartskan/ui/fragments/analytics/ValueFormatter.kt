package com.evitalz.homevitalz.heartskan.ui.fragments.analytics

import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MyValueFormatter : IValueFormatter {
    override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
    ): String {
        return String.format("%.1f", value)
    }
}

class FormatterXAxis(private val referenceval: Long) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val long: Long = value.toLong()
        val date = Date(referenceval)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val str = formatter.format(date)
        return str
    }

}

class FormatterXAxisWeek : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val long: Long = value.toLong()
        val date = Date(long)
        val formatter = SimpleDateFormat("EEE dd", Locale.getDefault())
        val str = formatter.format(date)
        return str
    }

}

class MyAxisValueFormatter(private val referenceTimestamp: Long) : IAxisValueFormatter {
    private val mDataFormat: DateFormat
    private val mDate: Date
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        var setvalue = ""
        // convertedTimestamp = originalTimestamp - referenceTimestamp
        val convertedTimestamp = value.toLong()

        // Retrieve original timestamp
        val originalTimestamp = referenceTimestamp + convertedTimestamp
        Log.d("originaltimestamp", originalTimestamp.toString() + "")
        setvalue = getHour(originalTimestamp)
        return setvalue
    }

    private fun getHour(timestamp: Long): String {
        return try {
            mDate.time = timestamp
            mDataFormat.format(mDate)
        } catch (ex: Exception) {
            "xx"
        }
    }

    private fun getDayofweek(timestamp: Long): String {
        return try {
            mDate.time = timestamp
            val cal = Calendar.getInstance()
            cal.time = mDate
            val dayofweek = cal[Calendar.DATE]
            val Hour = cal[Calendar.HOUR].toString() + ":" + cal[Calendar.MINUTE]
            "$dayofweek/ $Hour"
        } catch (ex: Exception) {
            "xx"
        }
    }

    private fun getDayofmonth(timestamp: Long): String {
        return try {
            mDate.time = timestamp
            val cal = Calendar.getInstance()
            cal.time = mDate
            val dayofmonth = cal[Calendar.DATE]
            val month = cal[Calendar.MONTH] + 1
            //                SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.ENGLISH);
//                String mon = sdf.format(month);
//                DateFormat fmt = new SimpleDateFormat("MMM dd", Locale.US);
//                Date d = fmt.parse(dayofmonth+month+"");
//                Log.d("format" , d);
            "$dayofmonth/ $month"
        } catch (ex: Exception) {
            Log.d("errorrrrr", ex.toString() + "")
            "xx"
        }
    }

    init {
        mDataFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        mDate = Date()
    }
}


