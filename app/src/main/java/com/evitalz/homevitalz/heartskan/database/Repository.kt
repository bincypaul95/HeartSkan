package com.evitalz.homevitalz.heartskan.database

import androidx.lifecycle.LiveData
import com.evitalz.homevitalz.heartskan.ui.activities.home.Patientnameid


class DeviceReadingsRepository(private val deviceReadingdao: DeviceReadingdao){

    fun devicereadings( ms : Long , endMs : Long,pregid :Int) : LiveData<List<Device_Readings>>{
        return deviceReadingdao.getDeviceReadings(ms , endMs,pregid)
    }
    fun devicereadingsdesc( ms : Long , endMs : Long,pregid:Int) : LiveData<List<Device_Readings>>{
        return deviceReadingdao.getDeviceReadingsdesc(ms , endMs,pregid)
    }
    fun devicereadingheartrate( ms : Long , endMs : Long,pregid:Int) : LiveData<List<Device_Readings>>{
        return deviceReadingdao.getDeviceReadingHeartrate(ms , endMs,pregid)
    }
    fun devicereadingBG( ms : Long , endMs : Long,pregid:Int) : LiveData<List<Device_Readings>>{
        return deviceReadingdao.getDeviceReadingBG(ms , endMs,pregid)
    }

    fun devicereadingSpo2( ms : Long , endMs : Long,pregid:Int) : LiveData<List<Device_Readings>>{
        return deviceReadingdao.getDeviceReadingspo2(ms , endMs,pregid)
    }

    fun devicereadingheartratebyid( rowid : Long) : Device_Readings {
        return deviceReadingdao.getDeviceReadingHeartratebyid(rowid)
    }
    fun insertdevicereadings(deviceReadings: Device_Readings) : Long{
        return deviceReadingdao.insertDeviceReadings(deviceReadings)
    }
    fun updatedevicereadings(deviceReadings: Device_Readings){
        deviceReadingdao.updatedevicereadings(deviceReadings)
    }
    fun updatedevicereadingslocal(deviceReadings: Device_Readings){
        deviceReadingdao.updatedevicereadingslocal(deviceReadings)
    }
    fun deletedevicereading(id : Int){
        deviceReadingdao.deletedevicereading(id)
    }

    fun getDeviceReadings(id : Long) : Device_Readings = deviceReadingdao.getDeviceReadings(id)

}

class UserRepository(private val userdao: Userdao){
    fun insertuser(userReg: User_Reg){
        userdao.insertUser(userReg)
    }
    fun getuser() : LiveData<List<User_Reg>>{
        return userdao.getuser()
    }
}

class PatientRepository(private val patientdao: Patientdao){
    fun insertuser(patientReg: Patient_Reg){
        patientdao.insertpatient(patientReg)
    }
    fun insertpatientdetails(patientDetails: Patient_details){
        patientdao.insertpatientdetails(patientDetails)
    }

    fun getpatientdetails(pid: Int) : LiveData<List<Patient_details>>{
        return patientdao.getpatientdetails(pid)
    }

    fun getpatientdetails1(pid: Int) : LiveData<Patient_details>{
        return patientdao.getpatientdetails1(pid)
    }
    fun getpatientreg(pid: Int) : LiveData<List<Patient_Reg>>{
        return patientdao.getpatient(pid)
    }
    fun getuser(pid: Int) : LiveData<List<Patient_Reg>>{
        return patientdao.getpatient(pid)
    }

    fun getprofiledetails(pid:Int): LiveData<Patient_Reg>{
        return patientdao.getprofiledetails(pid)
    }

    fun getprofiledetails1(pid:Int): LiveData<ProfileDetails>{
        return patientdao.getprofiledetails1(pid)
    }

    fun getpatientnames(uid: Int) : LiveData<List<Patientnameid>>{
        return patientdao.getpatientnames(uid)
    }
    fun updatepatientprofile(patientReg: Patient_Reg){
        patientdao.updateprofiledetails(patientReg)
    }
    fun updatepatientdetails(patientDetails: Patient_details){
        patientdao.updatepatientdetails(patientDetails)
    }

}

class ECGRepository(private val ecgReadingsDao: ECGReadingsDao){

    fun insertecg(ecgReadings: ECG_Readings){
        ecgReadingsDao.insertECG(ecgReadings)
    }
}