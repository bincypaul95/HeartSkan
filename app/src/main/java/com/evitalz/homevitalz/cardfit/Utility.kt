package com.evitalz.homevitalz.cardfit

import android.content.Context
import com.evitalz.homevitalz.cardfit.ui.activities.SplashActivity
import java.text.SimpleDateFormat
import java.util.*

object  Utility {
    val simpleDateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US)
    val simpleDateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var syncFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    var format = SimpleDateFormat("dd-MMM-yyyy")
    var alarmdateformat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    var alarmtimeformat = SimpleDateFormat("hh:mm a", Locale.US)

    const val DEVICE_NAME = "device_name"
    const val DEVICE_MAC = "device_mac"
    const val GLUCOSE_DEVICE_MAC = "glucose_device_mac"
    const val SYNC_TIME = "current_time"
    const val USER_NAME= "user_name"
    const val USER_EMAIL = "user_email"
    const val UREGID = "uregid"
    const val ROW_ID = "row_id"
    const val DREAD1 = "dread1"
    const val DREAD2 = "dread2"
    const val DREAD3 = "dread3"
    const val DREAD4 = "dread4"
    const val DREAD5 = "dread5"
    const val DREADID = "dreadid"
    const val TYPE = "type"
    const val DREGID = "dregid"
    const val PREGID = "pregid"
    const val PNAME = "pname"
    const val PIMAGE = "pimage"
    const val NOTE = "note"
    const val DATE_TIME = "datetime"
    const val PAGE = "page"
    const val DOB = "dob"
    const val PGENDER = "pgender"
    const val BLOODGROUP = "bldgrp"
    const val HEIGHT = "height"
    const val WEIGHT = "weight"
    const val WAIST = "waist"
    const val HIP = "hip"
    const val BP = "bp"
    const val DIABETIC = "diabetic"
    const val HBA1C = "hba1c"

    fun check_next_date(date: String): Boolean {
        return date != format.format(Calendar.getInstance().time)
    }

    fun saveSavedBluetoothAddress(address: String, context: Context){
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        sp.edit().putString(DEVICE_MAC, address).apply()
    }

    fun saveGlucoseBluetoothAddress(address: String, context: Context){
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        sp.edit().putString(GLUCOSE_DEVICE_MAC, address).apply()
    }

    fun getSavedBluetoothAddress(context: Context) : String{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        return sp.getString(DEVICE_MAC, "")!!
    }

 fun getGlucoseBluetoothAddress(context: Context) : String{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        return sp.getString(GLUCOSE_DEVICE_MAC, "")!!
    }

    fun getusername(context: Context):String{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        return sp.getString(USER_NAME, "")!!
    }

    fun getuseremail(context: Context):String{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        return sp.getString(USER_EMAIL, "")!!
    }

    fun geturegid(context: Context):Int{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        return sp.getInt(UREGID, 0)!!
    }
    fun getpregid(context: Context):Int{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        return sp.getInt(PREGID, 0)!!
    }

    fun getpname(context: Context):String{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        return sp.getString(PNAME, "")!!

    }

    fun saveCurrentTime(context: Context){
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        sp.edit().putLong(SYNC_TIME, Calendar.getInstance().time.time).apply()
    }

    fun getLastSync(context: Context): String{
        val sp = context.getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
        val syncTime = sp.getLong(SYNC_TIME , 0L)
        if(syncTime  == 0L){
            return ""
        }
        return syncFormat.format(syncTime)
    }



}