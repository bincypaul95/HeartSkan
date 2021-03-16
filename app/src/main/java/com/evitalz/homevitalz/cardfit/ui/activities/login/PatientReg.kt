package com.evitalz.homevitalz.cardfit.ui.activities.login

import com.google.gson.annotations.SerializedName

data class PatientReg (@SerializedName("Preg_id") val pregid: Int,
     @SerializedName("U_id") val uregid: Int,
     @SerializedName("P_Name") val pname: String,
     @SerializedName("P_Dob") val pdob: String,
     @SerializedName("P_Gender") val pgender: Int,
     @SerializedName("P_Age") val page: Int,
     @SerializedName("Mobile_Number") val mobilenumber: String,
     @SerializedName("blood_group") val bloodgroup: String,
     @SerializedName("email_ID") val email: String,
     @SerializedName("Height") val height: String,
     @SerializedName("Weight") val weight: String

)