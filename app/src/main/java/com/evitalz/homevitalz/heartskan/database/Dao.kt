package com.evitalz.homevitalz.heartskan.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.evitalz.homevitalz.heartskan.ui.activities.home.Patientnameid


@Dao
interface DeviceReadingdao{

    @Query("select * from Device_Readings where date(Date_time/1000 , 'unixepoch') >= date(:ms / 1000 , 'unixepoch') and date(Date_time/1000 , 'unixepoch') <= date(:endMs / 1000 , 'unixepoch') and P_id=:pregid and TYPE !='BloodGlucose' and  TYPE !='SpO2' ORDER BY Date_time ASC")
    fun getDeviceReadings( ms : Long , endMs : Long,pregid :Int) : LiveData<List<Device_Readings>>

    @Query("select * from Device_Readings where date(Date_time/1000 , 'unixepoch') >= date(:ms / 1000 , 'unixepoch') and date(Date_time/1000 , 'unixepoch') <= date(:endMs / 1000 , 'unixepoch') and P_id=:pregid and TYPE !='BloodGlucose' and  TYPE !='SpO2' ORDER BY Date_time DESC")
    fun getDeviceReadingsdesc( ms : Long , endMs : Long,pregid :Int) : LiveData<List<Device_Readings>>

    @Query("select * from Device_Readings where date(Date_time/1000 , 'unixepoch') >= date(:ms / 1000 , 'unixepoch') and date(Date_time/1000 , 'unixepoch') <= date(:endMs / 1000 , 'unixepoch') and P_id=:pregid and TYPE LIKE 'ECG'  ORDER BY Date_time ASC")
    fun getDeviceReadingHeartrate( ms : Long , endMs : Long,pregid :Int) : LiveData<List<Device_Readings>>

    @Query("select * from Device_Readings where date(Date_time/1000 , 'unixepoch') >= date(:ms / 1000 , 'unixepoch') and date(Date_time/1000 , 'unixepoch') <= date(:endMs / 1000 , 'unixepoch') and P_id=:pregid and TYPE LIKE 'BLOODGLUCOSE'  ORDER BY Date_time ASC")
    fun getDeviceReadingBG( ms : Long , endMs : Long,pregid :Int) : LiveData<List<Device_Readings>>

    @Query("select * from Device_Readings where date(Date_time/1000 , 'unixepoch') >= date(:ms / 1000 , 'unixepoch') and date(Date_time/1000 , 'unixepoch') <= date(:endMs / 1000 , 'unixepoch') and P_id=:pregid and TYPE LIKE 'SpO2'  ORDER BY Date_time ASC")
    fun getDeviceReadingspo2( ms : Long , endMs : Long,pregid :Int) : LiveData<List<Device_Readings>>


    @Query("select * from Device_Readings where ID= :rowid")
    fun getDeviceReadingHeartratebyid(rowid : Long) : Device_Readings

    @Query("SELECT * FROM Device_Readings where ID = :rowId")
    fun getDeviceReadings(rowId : Long) : Device_Readings

    @Insert
    fun insertDeviceReadings(deviceReadings: Device_Readings) : Long

    @Update
    fun updatedevicereadings(deviceReadings: Device_Readings)

    @Update
    fun updatedevicereadingslocal(deviceReadings: Device_Readings)

//    @Query("UPDATE device_readings SET D_Read1 = :dread1 WHERE ID = :id")
//    fun updatedevicereadings(dread1: String?,dread2: String?, dread3: String?,dread4: String?,dread5: String?, id: Int): Int

    @Query("delete from device_readings where ID = :id")
    fun deletedevicereading(id : Int)
}

@Dao
interface Userdao{
    @Insert
    fun insertUser(userReg: User_Reg)

    @Query("select * from User_Reg where U_id = 1")
    fun getuser() : LiveData<List<User_Reg>>

}

@Dao
interface Patientdao {
    @Insert
    fun insertpatient(patientReg: Patient_Reg)

    @Insert
    fun insertpatientdetails(patientDetails: Patient_details)

    @Query("select * from Patient_Reg where Preg_id =:pid")
    fun getpatient(pid: Int): LiveData<List<Patient_Reg>>

    @Query("select * from Patient_details where P_id =:pid")
    fun getpatientdetails(pid: Int): LiveData<List<Patient_details>>

    @Query("select * from Patient_details where P_id =:pid")
    fun getpatientdetails1(pid: Int): LiveData<Patient_details>

    @Query("select P_Name, Preg_id from Patient_Reg where U_id =:uid")
    fun getpatientnames(uid: Int): LiveData<List<Patientnameid>>

    @Query("SELECT * from Patient_Reg as a join Patient_details as b on a.Preg_id=b.P_id where Preg_id = :pid")
    fun getprofiledetails1(pid: Int): LiveData<ProfileDetails>


    @Query("SELECT * from Patient_Reg where Preg_id = :pid")
    fun getprofiledetails(pid: Int): LiveData<Patient_Reg>

    @Update
    fun updateprofiledetails(patientReg: Patient_Reg)

    @Update
    fun updatepatientdetails(patientDetails: Patient_details)
}

@Dao
interface DeviceRegDao{
    @Insert
    fun insertDeviceReg(deviceReg: Device_Reg)

    @Query("Select D_model from Device_Reg where D_model=:dmodel and D_Name=:dname and mac_addr=:macaddr")
    fun getDModel(dmodel: String, dname: String, macaddr:String) : String

    @Query("SELECT D_ID_NO FROM  Device_Reg where D_Name = :devicename  and D_Model=:dmodel")
    fun getDId(devicename: String, dmodel: String) : Int

    @Query("select reg.D_Id_No,reg.D_Name,reg.D_model,m.M_Name,reg.mac_addr from Device_Reg as reg join Manufrg_Details as m on reg.D_Mid=m.D_MId_No where reg.D_Name LIKE :typename and reg.D_model <> 'Manual Entry'")
    fun getDeviceList(typename: String) : List<DeviceList>

}

@Dao
interface ManufactureDao{

    @Query("Select D_MId_No from Manufrg_Details where M_Name= :name")
    fun getManufactureID(name: String) : Int

    @Insert
    fun insertmanufacture(manufrgDetails: Manufrg_Details)
}

@Dao
interface ECGReadingsDao{
    @Insert
    fun insertECG(ecgReadings: ECG_Readings)
}