package com.evitalz.homevitalz.cardfit.ui.activities.home

import androidx.room.ColumnInfo

class Patientnameid(@ColumnInfo(name = "P_Name") var pname: String,
                    @ColumnInfo(name = "Preg_id") val pregid : Int
) {
}