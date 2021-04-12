package com.evitalz.homevitalz.heartskan.ui.model

class DeviceData(val pulseval:String, val spo2val:String , val time : Long, val pi : Float){

    fun getPi(): String{
        return "${pi}%"
    }

    fun getSpo2(): String{
        return "${spo2val}%"
    }

    fun getPulse(): String{
        return "${pulseval}bpm"
    }
}