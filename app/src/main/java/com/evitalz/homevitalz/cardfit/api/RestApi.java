/* JSON API for MED APP */
package com.evitalz.homevitalz.cardfit.api;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RestApi {
    private final String urlString = "https://heartskanservice.azurewebsites.net/MedAppHandler.ashx";

    private static String convertStreamToUTF8String(InputStream stream) throws IOException {
	    String result = "";
	    StringBuilder sb = new StringBuilder();
	    try {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[4096];
            int readedChars = 0;
            while (readedChars != -1) {
                readedChars = reader.read(buffer);
                if (readedChars > 0)
                   sb.append(buffer, 0, readedChars);
            }
            result = sb.toString();
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    private String load(String contents) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(60000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream());
        w.write(contents);
        w.flush();
        InputStream istream = conn.getInputStream();
        String result = convertStreamToUTF8String(istream);
        return result;
    }


    private Object mapObject(Object o) {
		Object finalValue = null;
		if (o.getClass() == String.class) {
			finalValue = o;
		}
		else if (Number.class.isInstance(o)) {
			finalValue = String.valueOf(o);
		} else if (Date.class.isInstance(o)) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", new Locale("en", "USA"));
			finalValue = sdf.format((Date)o);
		}
		else if (Collection.class.isInstance(o)) {
			Collection<?> col = (Collection<?>) o;
			JSONArray jarray = new JSONArray();
			for (Object item : col) {
				jarray.put(mapObject(item));
			}
			finalValue = jarray;
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			Method[] methods = o.getClass().getMethods();
			for (Method method : methods) {
				if (method.getDeclaringClass() == o.getClass()
						&& method.getModifiers() == Modifier.PUBLIC
						&& method.getName().startsWith("get")) {
					String key = method.getName().substring(3);
					try {
						Object obj = method.invoke(o, null);
						Object value = mapObject(obj);
						map.put(key, value);
						finalValue = new JSONObject(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		return finalValue;
	}

    public JSONObject add_patient4(int U_id,String p_name,String dob,int p_gender,int p_age,String p_image,String Mobile_Number,int IDP_pid,String vesseslID,String blood_group,String email_ID,String national_id_number,String Height,String Weight,String Crew_cdc_seamen_id,String Crew_passport_number,String Ldate,String Date_of_Registration) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "add_patient4");
        p.put("U_id",mapObject(U_id));
        p.put("p_name",mapObject(p_name));
        p.put("dob",mapObject(dob));
        p.put("p_gender",mapObject(p_gender));
        p.put("p_age",mapObject(p_age));
        p.put("p_image",mapObject(p_image));
        p.put("Mobile_Number",mapObject(Mobile_Number));
        p.put("IDP_pid",mapObject(IDP_pid));
        p.put("vesseslID",mapObject(vesseslID));
        p.put("blood_group",mapObject(blood_group));
        p.put("email_ID",mapObject(email_ID));
        p.put("national_id_number",mapObject(national_id_number));
        p.put("Height",mapObject(Height));
        p.put("Weight",mapObject(Weight));
        p.put("Crew_cdc_seamen_id",mapObject(Crew_cdc_seamen_id));
        p.put("Crew_passport_number",mapObject(Crew_passport_number));
        p.put("Ldate",mapObject(Ldate));
        p.put("Date_of_Registration",mapObject(Date_of_Registration));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject add_Manufrg_Details(String M_Name) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "add_Manufrg_Details");
        p.put("M_Name",mapObject(M_Name));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject insert_Device_Reg2(String D_Name,String D_UUID,String D_model,int D_MId_No,int U_id,String mac_address) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "insert_Device_Reg2");
        p.put("D_Name",mapObject(D_Name));
        p.put("D_UUID",mapObject(D_UUID));
        p.put("D_model",mapObject(D_model));
        p.put("D_MId_No",mapObject(D_MId_No));
        p.put("U_id",mapObject(U_id));
        p.put("mac_address",mapObject(mac_address));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject update_device_reg(int Dreg_id,String D_model,String mac_address,String Ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "update_device_reg");
        p.put("Dreg_id",mapObject(Dreg_id));
        p.put("D_model",mapObject(D_model));
        p.put("mac_address",mapObject(mac_address));
        p.put("Ldate",mapObject(Ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject add_Device_Readings2(String deviceName,int D_Id,int P_id,String Date_Time,String D_Value1,String D_Value2,String D_Value3,String D_Value4,int CycleID,String Device_Photo,String D_Value5,String notes) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "add_Device_Readings2");
        p.put("deviceName",mapObject(deviceName));
        p.put("D_Id",mapObject(D_Id));
        p.put("P_id",mapObject(P_id));
        p.put("Date_Time",mapObject(Date_Time));
        p.put("D_Value1",mapObject(D_Value1));
        p.put("D_Value2",mapObject(D_Value2));
        p.put("D_Value3",mapObject(D_Value3));
        p.put("D_Value4",mapObject(D_Value4));
        p.put("CycleID",mapObject(CycleID));
        p.put("Device_Photo",mapObject(Device_Photo));
        p.put("D_Value5",mapObject(D_Value5));
        p.put("notes",mapObject(notes));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject add_Ecg_Readings(int D_Id,String ecg_values) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "add_Ecg_Readings");
        p.put("D_Id",mapObject(D_Id));
        p.put("ecg_values",mapObject(ecg_values));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject update_patient4(int Preg_id,String p_name,String dob,int p_gender,int p_age,String p_image,String Mobile_Number,String vesseslID,String blood_group,String email_ID,String national_id_number,String Height,String Weight,String Crew_cdc_seamen_id,String Crew_passport_number,String Ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "update_patient4");
        p.put("Preg_id",mapObject(Preg_id));
        p.put("p_name",mapObject(p_name));
        p.put("dob",mapObject(dob));
        p.put("p_gender",mapObject(p_gender));
        p.put("p_age",mapObject(p_age));
        p.put("p_image",mapObject(p_image));
        p.put("Mobile_Number",mapObject(Mobile_Number));
        p.put("vesseslID",mapObject(vesseslID));
        p.put("blood_group",mapObject(blood_group));
        p.put("email_ID",mapObject(email_ID));
        p.put("national_id_number",mapObject(national_id_number));
        p.put("Height",mapObject(Height));
        p.put("Weight",mapObject(Weight));
        p.put("Crew_cdc_seamen_id",mapObject(Crew_cdc_seamen_id));
        p.put("Crew_passport_number",mapObject(Crew_passport_number));
        p.put("Ldate",mapObject(Ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getUserDetails(String emailID,String password) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "getUserDetails");
        p.put("emailID",mapObject(emailID));
        p.put("password",mapObject(password));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject addCycleDetails(int Pid,int Uid,String CycleName,String Date_Time,int IDP_CycleID) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "addCycleDetails");
        p.put("Pid",mapObject(Pid));
        p.put("Uid",mapObject(Uid));
        p.put("CycleName",mapObject(CycleName));
        p.put("Date_Time",mapObject(Date_Time));
        p.put("IDP_CycleID",mapObject(IDP_CycleID));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getCycleDatils2(int uid,int pid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "getCycleDatils2");
        p.put("uid",mapObject(uid));
        p.put("pid",mapObject(pid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject add_logs2(String Type,String Desc,int Status,int Idp_id,String Upd_date,int uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "add_logs2");
        p.put("Type",mapObject(Type));
        p.put("Desc",mapObject(Desc));
        p.put("Status",mapObject(Status));
        p.put("Idp_id",mapObject(Idp_id));
        p.put("Upd_date",mapObject(Upd_date));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject insert_manual_user(String User_Name,String User_Dob,int User_Gender,String User_Eamil,String User_Phone,String User_Password,String User_image,String imei,int account_status,int Lstatus,String Lstatus_time,int IDP_uid,int IsEncrypt) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "insert_manual_user");
        p.put("User_Name",mapObject(User_Name));
        p.put("User_Dob",mapObject(User_Dob));
        p.put("User_Gender",mapObject(User_Gender));
        p.put("User_Eamil",mapObject(User_Eamil));
        p.put("User_Phone",mapObject(User_Phone));
        p.put("User_Password",mapObject(User_Password));
        p.put("User_image",mapObject(User_image));
        p.put("imei",mapObject(imei));
        p.put("account_status",mapObject(account_status));
        p.put("Lstatus",mapObject(Lstatus));
        p.put("Lstatus_time",mapObject(Lstatus_time));
        p.put("IDP_uid",mapObject(IDP_uid));
        p.put("IsEncrypt",mapObject(IsEncrypt));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject validate_Email(String emailID) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "validate_Email");
        p.put("emailID",mapObject(emailID));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject synch_patient(int uid,String ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "synch_patient");
        p.put("uid",mapObject(uid));
        p.put("ldate",mapObject(ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject synch_Device(int uid,String ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "synch_Device");
        p.put("uid",mapObject(uid));
        p.put("ldate",mapObject(ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject synch_DeviceReadings(int pid,String ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "synch_DeviceReadings");
        p.put("pid",mapObject(pid));
        p.put("ldate",mapObject(ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject synch_Ecg(int uid,String ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "synch_Ecg");
        p.put("uid",mapObject(uid));
        p.put("ldate",mapObject(ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject synch_Ecg1(int id,String ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "synch_Ecg1");
        p.put("id",mapObject(id));
        p.put("ldate",mapObject(ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject synch_Manufacture(String ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "synch_Manufacture");
        p.put("ldate",mapObject(ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject delete_DeviceReading(int rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "delete_DeviceReading");
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject update_Device_Readings(int rid,String Date_Time,String D_Value1,String D_Value2,String D_Value3,String D_Value4,String D_Value5,String notes) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "update_Device_Readings");
        p.put("rid",mapObject(rid));
        p.put("Date_Time",mapObject(Date_Time));
        p.put("D_Value1",mapObject(D_Value1));
        p.put("D_Value2",mapObject(D_Value2));
        p.put("D_Value3",mapObject(D_Value3));
        p.put("D_Value4",mapObject(D_Value4));
        p.put("D_Value5",mapObject(D_Value5));
        p.put("notes",mapObject(notes));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject insertPatientDetails1(int P_id,String bmi,int diabetic,int kidney_disease,int angina,int smoker,String HbA1C,int steroid_medication,String waistsize,String hipsize,String BP) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "insertPatientDetails1");
        p.put("P_id",mapObject(P_id));
        p.put("bmi",mapObject(bmi));
        p.put("diabetic",mapObject(diabetic));
        p.put("kidney_disease",mapObject(kidney_disease));
        p.put("angina",mapObject(angina));
        p.put("smoker",mapObject(smoker));
        p.put("HbA1C",mapObject(HbA1C));
        p.put("steroid_medication",mapObject(steroid_medication));
        p.put("waistsize",mapObject(waistsize));
        p.put("hipsize",mapObject(hipsize));
        p.put("BP",mapObject(BP));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject updatePatientDetails(int P_id,String bmi,int diabetic,int kidney_disease,int angina,int smoker,String HbA1C,int steroid_medication,String waistsize,String hipsize,String BP) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "updatePatientDetails");
        p.put("P_id",mapObject(P_id));
        p.put("bmi",mapObject(bmi));
        p.put("diabetic",mapObject(diabetic));
        p.put("kidney_disease",mapObject(kidney_disease));
        p.put("angina",mapObject(angina));
        p.put("smoker",mapObject(smoker));
        p.put("HbA1C",mapObject(HbA1C));
        p.put("steroid_medication",mapObject(steroid_medication));
        p.put("waistsize",mapObject(waistsize));
        p.put("hipsize",mapObject(hipsize));
        p.put("BP",mapObject(BP));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getPatientDetails(int pid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "getPatientDetails");
        p.put("pid",mapObject(pid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject checkAccessCode(String access_token,int uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "checkAccessCode");
        p.put("access_token",mapObject(access_token));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject chechUserAccess(int uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "chechUserAccess");
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject verifyDetailsByDOB(String dob,String email) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "verifyDetailsByDOB");
        p.put("dob",mapObject(dob));
        p.put("email",mapObject(email));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject updatePassword(String email,String password) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "updatePassword");
        p.put("email",mapObject(email));
        p.put("password",mapObject(password));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject verifyDetailsByOtp(String email) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "verifyDetailsByOtp");
        p.put("email",mapObject(email));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Spo2Data(int pid,String ldate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "Spo2Data");
        p.put("pid",mapObject(pid));
        p.put("ldate",mapObject(ldate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DoctorLogin(int pid,String doctorEmail,String doctorName) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "DoctorLogin");
        p.put("pid",mapObject(pid));
        p.put("doctorEmail",mapObject(doctorEmail));
        p.put("doctorName",mapObject(doctorName));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Spo2LiveData(int pid,int rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "Spo2LiveData");
        p.put("pid",mapObject(pid));
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject synch_devicereadings(int pregid,String lastsynctime) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "synch_devicereadings");
        p.put("pregid",mapObject(pregid));
        p.put("lastsynctime",mapObject(lastsynctime));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject add_devicereadings(int pid,String datetime,String value1,String value2,String value3,String value4,String value5,String dphoto,String notes,String type,int flag,int cycleid,String Lastupdateddate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "add_devicereadings");
        p.put("pid",mapObject(pid));
        p.put("datetime",mapObject(datetime));
        p.put("value1",mapObject(value1));
        p.put("value2",mapObject(value2));
        p.put("value3",mapObject(value3));
        p.put("value4",mapObject(value4));
        p.put("value5",mapObject(value5));
        p.put("dphoto",mapObject(dphoto));
        p.put("notes",mapObject(notes));
        p.put("type",mapObject(type));
        p.put("flag",mapObject(flag));
        p.put("cycleid",mapObject(cycleid));
        p.put("Lastupdateddate",mapObject(Lastupdateddate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject update_devicereadings(int rid,String Date_Time,String D_Value1,String D_Value2,String D_Value3,String D_Value4,String D_Value5,String notes) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "update_devicereadings");
        p.put("rid",mapObject(rid));
        p.put("Date_Time",mapObject(Date_Time));
        p.put("D_Value1",mapObject(D_Value1));
        p.put("D_Value2",mapObject(D_Value2));
        p.put("D_Value3",mapObject(D_Value3));
        p.put("D_Value4",mapObject(D_Value4));
        p.put("D_Value5",mapObject(D_Value5));
        p.put("notes",mapObject(notes));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject delete_Device_Reading(int rowid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","MEDAPI");
        o.put("method", "delete_Device_Reading");
        p.put("rowid",mapObject(rowid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

}


