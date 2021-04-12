package com.evitalz.homevitalz.heartskan.ui.activities.home

import com.google.gson.annotations.SerializedName

data class DeviceReadings(@SerializedName("Rid") val did: Int,
                          @SerializedName("Pid") val pregid: Int,
                          @SerializedName("D_Read1") val dread1: String,
                          @SerializedName("D_Read2") val dread2: String,
                          @SerializedName("D_Read3") val dread3: String,
                          @SerializedName("D_Read4") val dread4: String,
                          @SerializedName("D_Read5") val dread5: String,
                          @SerializedName("Notes") val notes: String,
                          @SerializedName("Type") val dtype: String,
                          @SerializedName("Date_Time") val dateTime: String
                          )