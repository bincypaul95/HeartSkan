package com.evitalz.homevitalz.heartskan.ui.activities.connect_device

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.evitalz.homevitalz.heartskan.BR

import com.evitalz.homevitalz.heartskan.PairHandler
import com.evitalz.homevitalz.heartskan.R

import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.databinding.RowShowpaireddevicesBinding
import com.evitalz.homevitalz.heartskan.databinding.RowdevicelistBinding

import com.evitalz.homevitalz.heartskan.ui.model.MyDeviceModel


class DeviceAdapter internal constructor(private val context:Activity):
    RecyclerView.Adapter<DeviceListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var devicelist = emptyList<MyDeviceModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val binding: RowdevicelistBinding =DataBindingUtil.inflate(inflater, R.layout.rowdevicelist,parent,false)
        return DeviceListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        Log.d("listsize", "onBindViewHolder: ${devicelist.size}")
        holder.binding.setVariable(BR.devicemodel,devicelist[position])
//        holder.binding.setVariable(BR.devicename, devicelist[position].devicename)
//        holder.binding.setVariable(BR.devicemac, devicelist[position].devicemac.toString())
        holder.binding.handler= context as PairHandler

        when(devicelist[position].devicename) {
            "TNG SPO2" -> {
                holder.binding.ivecg.setImageResource(R.drawable.tng_spo2)
            }
            else -> {
                val name=devicelist[position].devicename
                if(name.length>3 && name.substring(0,4) == "PM10"){
                    holder.binding.ivecg.setImageResource(R.drawable.pm10)
                }

            }
        }
        if (Utility.getSavedBluetoothAddress(context) == devicelist[position].devicemac.toString()){
            holder.binding.ivcheck.visibility=View.VISIBLE
            holder.binding.btnselect.visibility=View.GONE
        }
        holder.binding.executePendingBindings()
    }

    fun setData(list : List<MyDeviceModel>) {
        devicelist = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return  devicelist.size
    }


}

class PairDeviceAdapter internal constructor(private val context: ConnectDeviceActivity):
    RecyclerView.Adapter<PairedDeviceListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var devicelist = emptyList<MyDeviceModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairedDeviceListViewHolder {
        val binding: RowShowpaireddevicesBinding =DataBindingUtil.inflate(inflater, R.layout.row_showpaireddevices,parent,false)
        return PairedDeviceListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PairedDeviceListViewHolder, position: Int) {
        Log.d("listsize", "onBindViewHolder: ${devicelist.size}")
        holder.binding.setVariable(BR.devicemodel,devicelist[position])
        holder.binding.handler=context
        holder.binding.executePendingBindings()

    }

    override fun getItemCount(): Int {
        return  devicelist.size
    }


}


