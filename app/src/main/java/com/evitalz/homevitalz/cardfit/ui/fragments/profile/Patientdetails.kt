package com.evitalz.homevitalz.cardfit.ui.fragments.profile

import com.google.gson.annotations.SerializedName

data class Patientdetails (@SerializedName("pid") val pregid: Int,
                       @SerializedName("bmi") val diabetic: String,
                       @SerializedName("diabetic") val bmi: Int,
                       @SerializedName("kidneydisease") val kidneydisease: Int,
                       @SerializedName("angina") val angina: Int,
                       @SerializedName("smoker") val smoker: Int,
                       @SerializedName("HbA1C") val HbA1C: String,
                       @SerializedName("steroid_medication") val steroidmedication: Int,
                       @SerializedName("waistsize") val waistsize: String,
                       @SerializedName("hipsize") val hipsize: String,
                       @SerializedName("BP") val bp: String
)