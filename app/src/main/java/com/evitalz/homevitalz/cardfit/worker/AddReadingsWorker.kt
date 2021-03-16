package com.evitalz.homevitalz.cardfit.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.Utility.BLOODGROUP
import com.evitalz.homevitalz.cardfit.Utility.BP
import com.evitalz.homevitalz.cardfit.Utility.DATE_TIME
import com.evitalz.homevitalz.cardfit.Utility.DIABETIC
import com.evitalz.homevitalz.cardfit.Utility.DOB
import com.evitalz.homevitalz.cardfit.Utility.DREAD1
import com.evitalz.homevitalz.cardfit.Utility.DREAD2
import com.evitalz.homevitalz.cardfit.Utility.DREAD3
import com.evitalz.homevitalz.cardfit.Utility.DREAD4
import com.evitalz.homevitalz.cardfit.Utility.DREAD5
import com.evitalz.homevitalz.cardfit.Utility.DREADID
import com.evitalz.homevitalz.cardfit.Utility.HBA1C
import com.evitalz.homevitalz.cardfit.Utility.HEIGHT
import com.evitalz.homevitalz.cardfit.Utility.HIP
import com.evitalz.homevitalz.cardfit.Utility.NOTE
import com.evitalz.homevitalz.cardfit.Utility.PAGE
import com.evitalz.homevitalz.cardfit.Utility.PGENDER
import com.evitalz.homevitalz.cardfit.Utility.PIMAGE
import com.evitalz.homevitalz.cardfit.Utility.PNAME
import com.evitalz.homevitalz.cardfit.Utility.PREGID
import com.evitalz.homevitalz.cardfit.Utility.WAIST
import com.evitalz.homevitalz.cardfit.Utility.WEIGHT
import com.evitalz.homevitalz.cardfit.Utility.getpregid
import com.evitalz.homevitalz.cardfit.api.RestApi
import com.evitalz.homevitalz.cardfit.database.DeviceReadingsRepository
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.database.Spo2Database
import org.json.JSONObject
import java.util.*

class AddReadingsWorker(val appContext : Context, workerParameters: WorkerParameters) : Worker(appContext , workerParameters){
    val deviceRepository : DeviceReadingsRepository by lazy {
        val dao = Spo2Database.getDatabase(appContext).devicereadingdao()
        DeviceReadingsRepository(dao)
    }

    override fun doWork(): Result {
        val api = RestApi()

        inputData.let {
            val rowId : Long = it.getLong(Utility.ROW_ID , 0)
            val readings : Device_Readings = deviceRepository.getDeviceReadings(rowId)
            readings.apply {
                val result : JSONObject = api.add_devicereadings(
                    getpregid(appContext),
                    Utility.simpleDateFormat.format(datetime),dread1,dread2,dread3,dread4,dread5,"",note,
                    dtype,1,1,
                    Utility.simpleDateFormat.format(Calendar.getInstance().timeInMillis))

                return if(result.getBoolean("Successful")){

                    Log.d("insertdata", "doWork: ${result.getInt("Value")}")

                    deviceRepository.updatedevicereadingslocal(Device_Readings(
                        rowId.toInt(),result.getInt("Value"), getpregid(appContext),dread1,dread2,dread3,dread4,
                        Calendar.getInstance().timeInMillis,1,dtype,0,1,"",
                        dread5,note,datetime))

                    Log.d("insertdata", "doWork:success ")
                    Result.success()
                }else{
                    Log.d("insertdata", "doWork:retry ")
                    Result.retry()
                }
            }

        }
    }
}

class UpdateReadingsWorker(appContext : Context, workerParameters: WorkerParameters) : Worker(appContext , workerParameters){

    override fun doWork(): Result {
        val api = RestApi()

        inputData.let {

            val dread1 : String = it.getString(DREAD1)!!
            val dread2 : String = it.getString(DREAD2)!!
            val dread3 : String = it.getString(DREAD3)!!
            val dread4 : String = it.getString(DREAD4)!!
            val dread5 : String = it.getString(DREAD5)!!
            val note : String = it.getString(NOTE)!!
            val datetime : String = it.getString(DATE_TIME)!!
            val dreadid : Int = it.getInt(DREADID,0)!!
            Log.d("worker_test" , "updating data")

            val result : JSONObject = api.update_devicereadings(dreadid,datetime,dread1,dread2,dread3,dread4,dread5,note)

            return if(result.getBoolean("Successful")){
                Log.d("updatedata", "doWork:success ")
                Result.success()
            }else{
                Log.d("updatedata", "doWork:retry ")
                Result.retry()
            }

        }
    }
}


class InsertPatientdetailsWorker(appContext : Context, workerParameters: WorkerParameters) : Worker(appContext , workerParameters){

    override fun doWork(): Result {
        val api = RestApi()

        inputData.let {
//
//            val height : String = it.getString(HEIGHT)!!
//            val weight : String = it.getString(WEIGHT)!!
            val diabetic : String = it.getString(DIABETIC)!!
            val hba1c : String = it.getString(HBA1C)!!
            val waist : String = it.getString(WAIST)!!
            val hip : String = it.getString(HIP)!!
            val bp : String = it.getString(BP)!!
            val pregid : Int = it.getInt(PREGID,0)!!

            Log.d("worker_test" , "updating patient")


            val result1 : JSONObject = api.insertPatientDetails1(pregid,diabetic,0,0,
                0,0, hba1c,0, waist,hip ,bp )

            return if( result1.getBoolean("Successful")){
                Log.d("updatepatient", "doWork:success ${result1.getString("Value")}")
                Result.success()
            }else{
                Log.d("updatepatient", "doWork:retry ")
                Result.retry()
            }

        }
    }
}

class UpdatePatientWorker(appContext : Context, workerParameters: WorkerParameters) : Worker(appContext , workerParameters){

    override fun doWork(): Result {
        val api = RestApi()

        inputData.let {

            val pname : String = it.getString(PNAME)!!
            val page : Int = it.getInt(PAGE,0)!!
            val pgender : Int = it.getInt(PGENDER,3)!!
            val pimage : String = it.getString(PIMAGE)!!
            val pdob : String = it.getString(DOB)!!
            val pbldgrp : String = it.getString(BLOODGROUP)!!
            val height : String = it.getString(HEIGHT)!!
            val weight : String = it.getString(WEIGHT)!!
            val diabetic : String = it.getString(DIABETIC)!!
            val hba1c : String = it.getString(HBA1C)!!
            val waist : String = it.getString(WAIST)!!
            val hip : String = it.getString(HIP)!!
            val bp : String = it.getString(BP)!!
            val pregid : Int = it.getInt(PREGID,0)!!
            Log.d("worker_test" , "updating patient")

            val result : JSONObject = api.update_patient4(pregid,pname,pdob,pgender,page,pimage,"",
                45.toString(),pbldgrp,"", 0.toString(),height,weight,"","",
                Utility.simpleDateFormat.format(Calendar.getInstance().timeInMillis))

            val result1 : JSONObject = api.updatePatientDetails(pregid,diabetic,0,0,
            0,0,hba1c,0,waist,hip,bp )

            return if(result.getBoolean("Successful") && result1.getBoolean("Successful")){
                Log.d("updatepatient", "doWork:success ${result.getString("Value")}")
                Log.d("updatepatient", "doWork:success ${result1.getString("Value")}")
                Result.success()
            }else{
                Log.d("updatepatient", "doWork:retry ")
                Result.retry()
            }

        }
    }
}