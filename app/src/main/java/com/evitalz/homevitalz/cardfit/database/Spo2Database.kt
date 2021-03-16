package com.evitalz.homevitalz.cardfit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Device_Readings::class, Device_Reg::class, User_Reg::class, Patient_Reg::class,
    Cycle_Det::class, Manufrg_Details::class, Patient_details::class, ECG_Readings::class],
            version = 6, exportSchema = false)
public abstract class Spo2Database : RoomDatabase(){
        abstract fun  devicereadingdao(): DeviceReadingdao
        abstract fun  userdao(): Userdao
        abstract fun  patientdao(): Patientdao
        abstract fun  deviceRegDao(): DeviceRegDao
        abstract fun  manufactureDao(): ManufactureDao
        abstract fun  ecgreadingdao() : ECGReadingsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: Spo2Database? = null

        fun getDatabase(context: Context): Spo2Database {
            val Spo2Instance = INSTANCE
            if (Spo2Instance != null) {
                return Spo2Instance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Spo2Database::class.java,
                    "Spo2_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}