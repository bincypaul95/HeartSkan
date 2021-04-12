package com.evitalz.homevitalz.heartskan.database

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*


@Parcelize
@Entity(tableName = "Device_Readings")
class Device_Readings(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "D_Read_Id") val dreadid: Int,
    @ColumnInfo(name = "P_id") val pid: Int,
    @ColumnInfo(name = "D_Read1") val dread1: String,
    @ColumnInfo(name = "D_Read2") val dread2: String,
    @ColumnInfo(name = "D_Read3") val dread3: String,
    @ColumnInfo(name = "D_Read4") val dread4: String,
    @ColumnInfo(name = "Last_Updated_Date") val lastupdateddate: Long,
    @ColumnInfo(name = "Flag") val flag: Int,
    @ColumnInfo(name = "TYPE") val dtype: String,
    @ColumnInfo(name = "D_Reg_ID") val dregid: Int,
    @ColumnInfo(name = "C_ID") val cid: Int,
    @ColumnInfo(name = "D_Photo") val dphoto: String,
    @ColumnInfo(name = "D_Read5") val dread5: String,
    @ColumnInfo(name = "NOTE") val note: String,
    @ColumnInfo(name = "Date_time") val datetime: Long
): BaseObservable(),Parcelable {
    @Ignore
    var type: Int = -1

    companion object {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formatter1 = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        val dateformatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formatterMonth = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    fun getHour(): Float {
        val date = Date(datetime)
        val cal = Calendar.getInstance()
        cal.time = date
        val hour = cal.get(Calendar.HOUR)
        return hour.toFloat()
    }

    fun getFormattedTime(): String {
        val date = Date(datetime)
        return formatter.format(date)
    }

    fun getFormattedTimesec(): String {
        val date = Date(datetime)
        return formatter1.format(date)
    }

    fun getFormattedDate(): String {
        val date = Date(datetime)
        return dateformatter.format(date)
    }

    fun getFormattedsleeptime(): String {
        val strs = dread1.split(",").toTypedArray()
        val strs2 = dread2.split(",").toTypedArray()
        return strs[1] + "-" + strs2[1]
    }

}

class DeviceList(
    @ColumnInfo(name = "D_ID_NO") val didno: Int,
    @ColumnInfo(name = "D_Name") val dname: String,
    @ColumnInfo(name = "D_Model") val dmodel: String,
    @ColumnInfo(name = "M_Name") val mname: String,
    @ColumnInfo(name = "Mac_addr") val macaddr: String

)

class ProfileDetails(
    @ColumnInfo(name = "P_Name") var pname: String,
    @ColumnInfo(name = "P_Age") val page: Int,
    @ColumnInfo(name = "P_Gender") val pgender: Int,
    @ColumnInfo(name = "P_Dob") val pdob: String,
    @ColumnInfo(name = "height") val pheight: String,
    @ColumnInfo(name = "weight") val pweight: String,
    @ColumnInfo(name = "Bld_Grp") val pbldgrp: String,
    @ColumnInfo(name = "BP") val pbp: String,
    @ColumnInfo(name = "BMI") val diabtype: String,
    @ColumnInfo(name = "P_image") val pimage: String,
    @ColumnInfo(name = "Waist") val waist: String,
    @ColumnInfo(name = "Hip") val hip: String,
    @ColumnInfo(name = "HbA1c") val hba1c: String
){
    fun getAgeAndGender() : String{
        return "$page years, " + if(pgender == 0) "Male" else if(pgender == 1) "Female" else "Others"
    }
}

@Parcelize
@Entity(tableName = "Device_Reg")
class Device_Reg(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "D_ID_NO") val didno: Int,
    @ColumnInfo(name = "D_Reg") val dreg: Int,
    @ColumnInfo(name = "D_Name") val dname: String,
    @ColumnInfo(name = "D_UUID") val duuid: String,
    @ColumnInfo(name = "D_Model") val dmodel: String,
    @ColumnInfo(name = "D_Mid") val dmid: Int,
    @ColumnInfo(name = "Last_Updated_Date") val lastupdateddate: Long,
    @ColumnInfo(name = "Flag") val flag: Int,
    @ColumnInfo(name = "Mac_addr") val macaddr: String,
    @ColumnInfo(name = "U_ID") val uid: Int
):BaseObservable(),Parcelable

@Entity(tableName = "Cycle_Det")
class Cycle_Det(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id") val id: Int,
    @ColumnInfo(name = "C_id") val cid: Int,
    @ColumnInfo(name = "C_Name") val cname: String,
    @ColumnInfo(name = "U_Id") val uid: Int,
    @ColumnInfo(name = "P_Id") val pid: String,
    @ColumnInfo(name = "Date_Time") val datetime: Long,
    @ColumnInfo(name = "Ldate") val ldate: Long,
    @ColumnInfo(name = "Flag") val flag: Int
)

@Entity(tableName = "Manufrg_Details")
class Manufrg_Details(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "D_Mid_No") val dmidno: Int,
    @ColumnInfo(name = "Mreg_id") val mregid: Int,
    @ColumnInfo(name = "M_Name") val mname: String,
    @ColumnInfo(name = "Ldate") val ldate: Long,
    @ColumnInfo(name = "Flag") val flag: Int
)

@Entity(tableName = "Patient_Reg")
class Patient_Reg(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "P_id") val pid: Int,
    @ColumnInfo(name = "U_id") val uid: Int,
    @ColumnInfo(name = "Preg_id") val pregid: Int,
    @ColumnInfo(name = "P_Name") var pname: String,
    @ColumnInfo(name = "P_Dob") var pdob: String,
    @ColumnInfo(name = "P_Gender") var pgender: Int,
    @ColumnInfo(name = "P_Age") var page: Int,
    @ColumnInfo(name = "P_Mobile") val pmobile: String,
    @ColumnInfo(name = "P_image") var pimage: String,
    @ColumnInfo(name = "Ldate") val ldate: Long,
    @ColumnInfo(name = "Flag") val flag: Int,
    @ColumnInfo(name = "Empp_id") val emppid: String,
    @ColumnInfo(name = "pidp") val pidp: Int,
    @ColumnInfo(name = "Vessel_Id") val vesselid: Int,
    @ColumnInfo(name = "Bld_Grp") var bldgrp: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "height") var height: String,
    @ColumnInfo(name = "weight") var weight: String,
    @ColumnInfo(name = "BloodPressure") var bp: String,
    @ColumnInfo(name = "DiabeticType") var diabetic: String,
    @ColumnInfo(name = "Waist") var waist: String,
    @ColumnInfo(name = "Hip") var hip: String,
    @ColumnInfo(name = "HbA1c") var hba1c: String,
    @ColumnInfo(name = "nid") val nid: String,
    @ColumnInfo(name = "Emp_Id") val empid: String,
    @ColumnInfo(name = "Pass_no") val passno: String,
    @ColumnInfo(name = "Reg_date") val regdate: Long
){
    fun getAgeAndGender() : String{
        return "$page years, " + if(pgender == 0) "Male" else if(pgender == 1) "Female" else if(pgender == 2)"Others" else ""
    }

    fun getGender() : String{
        return  if(pgender == 0) "Male" else if(pgender == 1) "Female" else "Others"
    }

    fun getsys(): String{
        val arr = bp.split("/".toRegex()).toTypedArray()
        var sys=""
        if (arr.size > 1) {
             sys = arr[0]
        }
        return sys
    }

    fun getdia(): String{
        val arr = bp.split("/".toRegex()).toTypedArray()
        var dia=""
        if (arr.size > 1) {
            dia = arr[1]
        }
        return dia
    }
}

@Parcelize
@Entity(tableName = "User_Reg")
class User_Reg(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "U_id") val uid: Int,
    @ColumnInfo(name = "Ureg_id") val uregid: Int,
    @ColumnInfo(name = "U_Name") val uname: String,
    @ColumnInfo(name = "U_Dob") val udob: String,
    @ColumnInfo(name = "U_Gender") val ugender: Int,
    @ColumnInfo(name = "U_Email") val uemail: String,
    @ColumnInfo(name = "U_Phone") val uphone: String,
    @ColumnInfo(name = "U_Password") val upassword: String,
    @ColumnInfo(name = "U_image") val uimage: String,
    @ColumnInfo(name = "U_Imei") val uimei: String,
    @ColumnInfo(name = "Reg_date") val regdate: Long,
    @ColumnInfo(name = "Ldate") val ldate: Long,
    @ColumnInfo(name = "flag") val flag: Int,
    @ColumnInfo(name = "uidp") val uidp: Int,
    @ColumnInfo(name = "org_id") val orgid: Int,
    @ColumnInfo(name = "Vessel_Id") val vesselid: Int,
    @ColumnInfo(name = "isEncrypt") val isencrypt: Int

): BaseObservable(), Parcelable{}

@Parcelize
@Entity(tableName = "Patient_details")
class Patient_details(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "P_id") val pid: Int,
    @ColumnInfo(name = "BMI") var bmi: String,
    @ColumnInfo(name = "Diabetic") var diabetic: Int,
    @ColumnInfo(name = "KidneyDisease") val kidneydisease: Int,
    @ColumnInfo(name = "Angina") val angina: Int,
    @ColumnInfo(name = "Smoker") val smoker: Int,
    @ColumnInfo(name = "HbA1c") var hba1c: String,
    @ColumnInfo(name = "Steroid") val steroid: Int,
    @ColumnInfo(name = "Waist") var waist: String,
    @ColumnInfo(name = "Hip") var hip: String,
    @ColumnInfo(name = "flag") val flag: Int,
    @ColumnInfo(name = "Last_Updated_Date") val ldate: Long,
    @ColumnInfo(name = "BP") var bp: String


): BaseObservable(), Parcelable{}

@Parcelize
@Entity(tableName = "ECG_Readings")
class ECG_Readings(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID_No") val id: Int,
    @ColumnInfo(name = "Ecg_id") val ecgid: Int,
    @ColumnInfo(name = "D_Id") val did: Int,
    @ColumnInfo(name = "ECG_Values") val ecgvalues: String,
    @ColumnInfo(name = "Last_Updated_Date") val ldate: Long,
    @ColumnInfo(name = "Flag") val flag: Int
): BaseObservable(), Parcelable{}