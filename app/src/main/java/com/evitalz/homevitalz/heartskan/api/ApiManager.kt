package com.evitalz.homevitalz.heartskan.api

import android.content.Context
import androidx.work.*
import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.worker.AddReadingsWorker
import com.evitalz.homevitalz.heartskan.worker.InsertPatientdetailsWorker
import com.evitalz.homevitalz.heartskan.worker.UpdatePatientWorker
import com.evitalz.homevitalz.heartskan.worker.UpdateReadingsWorker

object ApiManager{

    fun insertdata(rowId: Long , context: Context){

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val request = OneTimeWorkRequestBuilder<AddReadingsWorker>()
                .setConstraints(constraints)
                .setInputData(
                        workDataOf(
                            Utility.ROW_ID to  rowId
                        )
                )
                .build()
        WorkManager.getInstance(context).enqueue(request)

    }

    fun updatedata(context: Context,  dreadid:Int, value1:String,
                   value2:String,value3:String,value4:String,value5:String, note:String, datetime:Long){

        val datetime1 = Utility.simpleDateFormat.format(datetime)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UpdateReadingsWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    Utility.DREADID to dreadid,
                    Utility.DREAD1 to value1 ,
                    Utility.DREAD2 to value2,
                    Utility.DREAD3 to value3,
                    Utility.DREAD4 to value4,
                    Utility.DREAD5 to value5,
                    Utility.NOTE to note,
                    Utility.DATE_TIME to datetime1
                )
            )
            .build()
        WorkManager.getInstance(context).enqueue(request)

    }

    fun updatepatient(
        context: Context,
        age: Int,
        name: String,
        image: String,
        pregid: Int,
        dob: String,
        gender: Int,
        bldgrp: String,
        height: String,
        weight: String,
        bp: String,
        diabetic: String,
        waist: String,
        hip: String,
        hba1c: String
    ){

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UpdatePatientWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    Utility.PNAME to name,
                    Utility.PREGID to pregid,
                    Utility.PIMAGE to image,
                    Utility.PGENDER to gender,
                    Utility.PAGE to age ,
                    Utility.DOB to dob,
                    Utility.BLOODGROUP to bldgrp,
                    Utility.HEIGHT to height,
                    Utility.WEIGHT to weight,
                    Utility.WAIST to waist,
                    Utility.HIP to hip,
                    Utility.DIABETIC to diabetic,
                    Utility.BP to bp,
                    Utility.HBA1C to hba1c
                )
            )
            .build()
        WorkManager.getInstance(context).enqueue(request)

    }

    fun insertpatientdetails(
        context: Context,
        diabetic: String,
        pregid: Int,
        waist: String,
        hip: String,
        bp: String,
        hba1c: String

    ){

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<InsertPatientdetailsWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    Utility.DIABETIC to diabetic,
                    Utility.PREGID to pregid,
                    Utility.WAIST to waist,
                    Utility.HIP to hip,
                    Utility.HBA1C to hba1c,
                    Utility.BP to bp

                )
            )
            .build()
        WorkManager.getInstance(context).enqueue(request)

    }
}

