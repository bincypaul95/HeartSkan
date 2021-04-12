package com.evitalz.homevitalz.heartskan.ui.activities.login

import com.google.gson.annotations.SerializedName

data class UserReg(@SerializedName("Ureg_id") val uregid: Int,
                   @SerializedName("U_Name") val uname: String,
                   @SerializedName("U_Dob") val udob: String,
                   @SerializedName("U_Gender") val ugender: Int,
                   @SerializedName("U_Eamil") val uemail: String,
                   @SerializedName("U_Phone") val uphone: String,
                   @SerializedName("U_Password") val upassword: String,
                   @SerializedName("U_image") val uimage: String,
                   @SerializedName("U_Imei") val uimei: String,
                   @SerializedName("Ldate") val Ldate: String,
                   @SerializedName("Account_status") val accstatus: Int,
                   @SerializedName("Lstatus") val lstatus: Int,
                   @SerializedName("Lstatus_time") val lstatustime: String,
                   @SerializedName("user_IDP_ID") val userIDPID: Int,
                   @SerializedName("org_ID") val orgID: Int,
                   @SerializedName("Vessel_id") val Vesselid: Int,
                   @SerializedName("IsEncrypt") val Isencrypt: Int)
