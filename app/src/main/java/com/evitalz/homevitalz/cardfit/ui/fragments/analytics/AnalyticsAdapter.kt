package com.evitalz.homevitalz.cardfit.ui.fragments.analytics

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.evitalz.homevitalz.cardfit.BR
import com.evitalz.homevitalz.cardfit.R
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.databinding.RowAnalyticsbgBinding
import com.evitalz.homevitalz.cardfit.databinding.RowAnalyticshrBinding
import com.evitalz.homevitalz.cardfit.databinding.RowAnalyticsspo2Binding


class AnalyticsAdapter internal constructor(
    private  val context: Context,
    analyticsFragment: AnalyticsFragment
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEARTRATE = 5
    private val TYPE_GLUCOSE = 6
    private val TYPE_SPO2 = 7

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var devicereadings = emptyList<Device_Readings>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_HEARTRATE -> {
                val binding: RowAnalyticshrBinding = DataBindingUtil.inflate(inflater, R.layout.row_analyticshr, parent, false)
                AnalyticsViewholderHeartrate(binding)
            }
            TYPE_GLUCOSE ->{
                val binding: RowAnalyticsbgBinding = DataBindingUtil.inflate(inflater, R.layout.row_analyticsbg, parent, false)
                AnalyticsViewholderBG(binding)
            }
            TYPE_SPO2 ->{
                val binding: RowAnalyticsspo2Binding = DataBindingUtil.inflate(inflater, R.layout.row_analyticsspo2, parent, false)
                AnalyticsViewholderspo2(binding)
            }
            else -> {
                val binding: RowAnalyticsbgBinding = DataBindingUtil.inflate(inflater, R.layout.row_analyticsbg, parent, false)
                AnalyticsViewholderBG(binding)
            }

        }
    }

    override fun getItemCount(): Int {
        Log.d("Listsize", "getItemCount: $devicereadings.size")
        return  devicereadings.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == TYPE_GLUCOSE) {
            (holder as AnalyticsViewholderBG)
            Log.d("bloodglucosedata", "onBindViewHolder:" +devicereadings[position])
            holder.binding.setVariable(BR.devicereadings,devicereadings[position])

            when(devicereadings[position].dread2){
                "Before Breakfast",
                "Before Lunch",
                "Before Dinner",
                "Fasting" ->{
                    devicereadings[position].dread5.toFloat().let {
                        when {
                            it in 80.0..100.0 -> holder.binding.ivCircle.setImageResource(
                                    R.drawable.greencircle
                            )
                            it in 101.0..125.0 -> holder.binding.ivCircle.setImageResource(
                                    R.drawable.yellowcircle
                            )
                            it > 125 -> holder.binding.ivCircle.setImageResource(
                                    R.drawable.circle
                            )
                        }
                    }
                }
               else ->{
                    devicereadings[position].dread5.toFloat().let {
                        when {
                            it in 120.0..140.0 -> holder.binding.ivCircle.setImageResource(
                                    R.drawable.greencircle
                            )
                            it in 140.0..160.0 -> holder.binding.ivCircle.setImageResource(
                                    R.drawable.yellowcircle
                            )
                            it > 160 -> holder.binding.ivCircle.setImageResource(
                                    R.drawable.circle
                            )
                        }
                    }
                }

//               else ->{
//                    devicereadings[position].dread5.toFloat().let {
//                        when {
//                            it in 170.0..190.0 -> holder.binding.ivCircle.setImageResource(
//                                    R.drawable.greencircle
//                            )
//                            it in 190.0..230.0 -> holder.binding.ivCircle.setImageResource(
//                                    R.drawable.yellowcircle
//                            )
//                            it > 230 -> holder.binding.ivCircle.setImageResource(
//                                    R.drawable.circle
//                            )
//                        }
//                    }
//                }
            }

            holder.binding.executePendingBindings()
        }

        if (getItemViewType(position) == TYPE_HEARTRATE) {
            (holder as AnalyticsViewholderHeartrate)
            holder.binding.setVariable(BR.devicereadings,devicereadings[position])
            devicereadings[position].dread2.toFloat().let {
                when {
                    it in 60.0..100.0 -> holder.binding.ivCircle.setImageResource(
                        R.drawable.greencircle
                    )
                    it < 60-> holder.binding.ivCircle.setImageResource(
                        R.drawable.yellowcircle
                    )
                    it > 100 -> holder.binding.ivCircle.setImageResource(
                        R.drawable.circle
                    )
                }
            }
            holder.binding.executePendingBindings()
        }

        if (getItemViewType(position) == TYPE_SPO2) {
            (holder as AnalyticsViewholderspo2)
            holder.binding.setVariable(BR.devicereadings,devicereadings[position])
//            devicereadings[position].dread2.toFloat().let {
//                when {
//                    it in 60.0..100.0 -> holder.binding.ivCircle.setImageResource(
//                            R.drawable.greencircle
//                    )
//                    it < 60-> holder.binding.ivCircle.setImageResource(
//                            R.drawable.yellowcircle
//                    )
//                    it > 100 -> holder.binding.ivCircle.setImageResource(
//                            R.drawable.circle
//                    )
//                }
//            }
            holder.binding.executePendingBindings()
        }


    }

    override fun getItemViewType(position: Int): Int {
        when(devicereadings[position].dtype) {
            "ECG" ->return TYPE_HEARTRATE
            "BloodGlucose" ->return TYPE_GLUCOSE
            "SpO2" ->return  TYPE_SPO2
        }

        return super.getItemViewType(position)
    }

}

