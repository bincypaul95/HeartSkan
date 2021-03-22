package com.evitalz.homevitalz.cardfit.ui.activities.spo2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.Device_UUID;
import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.SampleGattAttributes;
import com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.evitalz.homevitalz.cardfit.ui.activities.connect_device.SampleGattAttributes.uuidFromShortString;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.cmd_clear_device_data;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.cmd_getLive_data;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.cmd_get_data_p1;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.cmd_get_data_p2;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.cmd_get_num_record;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.cmd_start_monitor;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.cmd_turn_off_dev;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.convertRxCmdToDateObj;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.get_cmd_set_device_date;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.get_num_records;
import static com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants.Device_Command.intArrayToByteArray;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService1 extends Service {
    private final static String TAG = BluetoothLeService1.class.getSimpleName();
    private byte[] data_p1;
    private byte[] data_p2;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    boolean reconnect=false;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private boolean check=false;
    public static String ACTION_DATA_RECEIVED = "61_data_received";
    public static String PARAM_SP02 = "param_sp02";
    public static String PARAM_PULSE = "param_pulse";
    public static String PARAM_PI = "param_pi";
    Handler handler=new Handler();
    Runnable runnable;
    BluetoothGattCharacteristic characteristic1 = null;
    byte[] cmd;
    String model;
    private String giventype;
    private String rec_type="";
    private boolean get_data=false;
    int error_count=0;
    int num_rec=0;
    private String val1="",val2="",val3="",val4="";
    int sec=0;
    private boolean hasStarted = false;
    Timer t;
    TimerTask task;
    private static final UUID MEASUREMENT_SERVICE_UUID = UUID
            .fromString("46a970e0-0d5f-11e2-8b5e-0002a5d5c51b");
    private static final UUID MEASUREMENT_CHARACTERISTIC_UUID = UUID
            .fromString("0aad7ea0-0d60-11e2-8e3c-0002a5d5c51b");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");


    public static final String KEY_UNIT = "unit";
    public static final UUID TemperatureMeasurement = uuidFromShortString("2a1c");
    public static final UUID TemperatureType = uuidFromShortString("2a1d");

    public static final String VALUE_WEIGHT_SCALE_UNITS_KG = "kg";
    public static final String VALUE_WEIGHT_SCALE_UNITS_LBS = "lbs";
    public final static UUID UUID_WEIGHT_AD_CHAR = UUID
            .fromString(SampleGattAttributes.WEIGHT_AD_CHAR);
    public static final String KEY_SYSTOLIC = "systolic";
    public static final String KEY_DIASTOLIC = "diastolic";
    public static final String KEY_MEAS_ARTERIAL_PRESSURE = "measArterialPressure";
    public static final String KEY_PULSE_RATE = "pulseRate";

    public static final String KEY_WEIGHT = "weight";

    public static final String KEY_YEAR = "year";
    public static final String KEY_MONTH = "month";
    public static final String KEY_DAY = "day";
    public static final String KEY_HOURS = "hours";
    public static final String KEY_MINUTES = "minutes";
    public static final String KEY_SECONDS = "seconds";
    public static final String VALUE_TEMPERATURE_UNIT_C = "c";
    public static final String VALUE_TEMPERATURE_UNIT_F = "f";

    public static final String KEY_BODY_MOVEMENT_DETECTION = "bodyMovementDetection" ;
    public static final String KEY_CUFF_FIT_DETECTION = "cuffFitDetection" ;
    public static final String KEY_IRREGULAR_PULSE_DETECTION = "irregularPulseDetection";
    public static final String KEY_PULSE_RATE_RANGE_DETECTION = "pulseRateRangeDetection";
    public static final String KEY_MEASUREMENT_POSITION_DETECTION = "measurementPositionDetection";
    public final static String CONNECTION_WEIGHT = "weight";

    public static List<UUID> ServicesUUIDs = new ArrayList<UUID>();
    public static List<UUID> MeasuCharacUUIDs = new ArrayList<UUID>();
    //    private final static String TAG = getSimpleName();
    public static final UUID DateTime = uuidFromShortString("2a08");


    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DEVICE_NOT_FOUND =
            "com.example.bluetooth.le.ACTION_GATT_DEVICE_NOT_FOUND";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_NOT_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_NOT_AVAILABLE";
    public final static String ACTION_SETTING_DEVICE_DATE =
            "com.example.bluetooth.le.ACTION_SETTING_DEVICE_DATE";
    public final static String ACTION_CHECKING_DATA =
            "com.example.bluetooth.le.ACTION_CHECKING_DATA";
    public final static String ACTION_CONTINUOUS_DATA =
            "com.example.bluetooth.le.ACTION_CONTINUOUS_DATA";
    public final static String ACTION_FETCHING_DATA =
            "com.example.bluetooth.le.ACTION_FETCHING_DATA";
    public final static String ACTION_CLEARING_DATA =
            "com.example.bluetooth.le.ACTION_CLEARING_DATA";
    public final static String MISMATCH_READING =
            "com.example.bluetooth.le.MISMATCH_READING";
    public final static String ERROR_133 =
            "com.example.bluetooth.le.ERROR_133";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public static final UUID BloodPressureService = uuidFromShortString("1810");
    public static final UUID BloodPressureMeasurement = uuidFromShortString("2a35");
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public static final UUID AndCustomWeightScaleService = UUID.fromString("23434100-1FE4-1EFF-80CB-00FF78297D8B");
    public static final UUID AndCustomWeightScaleMeasurement = UUID.fromString("23434101-1FE4-1EFF-80CB-00FF78297D8B");
    public static final UUID WeightScaleService = uuidFromShortString("181d");
    public static final UUID WeightScaleMeasurement = uuidFromShortString("2a9d");
    public static final UUID HealthThermometerService = uuidFromShortString("1809");
//    public final static String ACTION_GATT_CONNECTED =
//            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
//    public final static String ACTION_GATT_DISCONNECTED =
//            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
//    public final static String ACTION_GATT_SERVICES_DISCOVERED =
//            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
//    public final static String ACTION_DATA_AVAILABLE =
//            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
//    public final static String EXTRA_DATA =
//            "com.example.bluetooth.le.EXTRA_DATA";



    private Integer mHeartRateForArtikCloud = null;

    int retry=3;
    int retry_count=0;
    List<Integer> irlist = new ArrayList<>();
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState == 0){
                Collections.min(irlist);
            }
            if(status==133)
            {
                if(hasStarted)
                {
                    t.cancel();
                    t.purge();
                    hasStarted=false;
                }

                if(sec<26)
                {
                    if(error_count>2)
                    {
                        close();
                        broadcastUpdate(ERROR_133);
                        return;
                    }

                    error_count++;
                    //                System.out.println("@@@"+refreshDeviceCache(mBluetoothGatt));
                    close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {


                            }

                            connect_internal(mBluetoothDeviceAddress, model, rec_type);
                        }
                    }).start();
                }
                else
                    broadcastUpdate(ACTION_GATT_DEVICE_NOT_FOUND);
                sec=0;
                return;
            }
            if (newState == STATE_CONNECTED){
                if(hasStarted)
                {
                    t.cancel();
                    t.purge();
//                    hasStarted=false;
                }
                sec=0;
                System.out.println("@@@ CONNECTED");
                error_count=0;
                broadcastUpdate(ACTION_GATT_CONNECTED);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                gatt.discoverServices();

            }

            else if(newState==STATE_DISCONNECTED)
            {
                Collections.min(irlist);
                Collections.max(irlist);
                Log.d("cmd_test", "onCharacteristicChanged:" +Collections.min(irlist)+ Collections.max(irlist));
                System.out.println("@@@ DISCONNECTED");
                if(hasStarted)
                {
                    t.cancel();
                    t.purge();
                    hasStarted=false;
                }
                sec=0;
                close();
                if(reconnect)
                {
                    System.out.println("@@@ RECONNECT");
                    reconnect=false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {


                            }
                            connect_internal(mBluetoothDeviceAddress, model, rec_type);
                        }
                    }).start();

                }
                else
                    broadcastUpdate(ACTION_GATT_DISCONNECTED);



            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            super.onServicesDiscovered(gatt, status);
            System.out.println("@@@Service Discovered"+status);
            BluetoothGattCharacteristic characteristic =
                    gatt.getService(Device_UUID.METER_UUID)
                            .getCharacteristic(Device_UUID.METER_UUID2);
            gatt.setCharacteristicNotification(characteristic, true);

            final BluetoothGattDescriptor descriptor =
                    characteristic.getDescriptors().get(0);

            descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            runnable=new Runnable() {
                @Override
                public void run() {
                    reconnect=true;
                    gatt.disconnect();

//                                    if(!check)
//                                                check=true;
//                        System.out.println("@@@ Handler service"+status);
//                        descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                        gatt.writeDescriptor(descriptor);
//                        handler.postDelayed(runnable,30000);
                }
            };
            handler.postDelayed(runnable,3000);

            gatt.writeDescriptor(descriptor);

            System.out.println("@@@Service Discovered"+status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            System.out.println("@@@"+ Arrays.toString(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            check=false;
            if(runnable!=null)
            {
                handler.removeCallbacks(runnable);
                runnable=null;
            }

            byte[] rec=characteristic.getValue();
            Log.d("cmd_test", "onCharacteristicChanged: "+rec[1]);
            if(rec[1]==(byte) (0x33) )
            {
                broadcastUpdate(ACTION_CHECKING_DATA);
                characteristic1 =
                        gatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);
                cmd=intArrayToByteArray(cmd_get_num_record(Device_UUID.User.values()[1]));



            }else if(rec[1] == (byte) 0x61){
                Log.d("cmd_test", "onCharacteristicChanged: 0x61 data ==> "+rec[2]);
                parse61Data(rec);
//                cmd = intArrayToByteArray(cmd_clear_device_data());
            }
            else if(rec[1] == (byte) 0x3A){

                characteristic1 =
                        gatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);
                 int[] cmd1 = cmd_start_monitor();
                characteristic1.setValue( intArrayToByteArray(cmd1)) ;
                gatt.writeCharacteristic(characteristic1);
            }
            else if(rec[1]==(byte) 0x60){
                int i=Device_Command.readIR(rec);
                if(i>900){
                    irlist.add(i);
                }

//                parse60Data();
                Log.d("cmd_test", "onCharacteristicChanged: 0x60 data ==> "+rec[2]);
            }
            else if(rec[1]==(byte) (0x2B))
            {
                int[] intArray = new int[rec.length];
                for (int i = 0; i < rec.length; intArray[i] = rec[i++]);
                num_rec=get_num_records(Device_UUID.User.values()[1],intArray,false);
                if(num_rec<0){
                    num_rec=256+num_rec;
                }
                if(num_rec>0)
                {


                    broadcastUpdate(ACTION_DATA_AVAILABLE);
                    characteristic1 =
                            gatt.getService(Device_UUID.METER_UUID)
                                    .getCharacteristic(Device_UUID.METER_UUID2);
                    if(model.equals("TNG SCALE")){
                        get_data=true;
                        cmd=intArrayToByteArray(Device_Command.createTx71Cmd(0));
                    }
                    else
                        cmd=intArrayToByteArray(cmd_get_data_p1(0, Device_UUID.User.values()[1]));



                    System.out.println("@@@");
//
                }
                else
                {
                    broadcastUpdate(ACTION_DATA_NOT_AVAILABLE);
                    characteristic1 =
                            gatt.getService(Device_UUID.METER_UUID)
                                    .getCharacteristic(Device_UUID.METER_UUID2);
                    cmd = intArrayToByteArray(cmd_turn_off_dev(0));


//                        close();
                }

            }
            else if(rec[1]==(byte) (0x25))
            {
                broadcastUpdate(ACTION_FETCHING_DATA);
                data_p1=rec;
                characteristic1 =
                        gatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);
                cmd=intArrayToByteArray(cmd_get_data_p2(0, Device_UUID.User.values()[1]));


            }
            else if(rec[1]==(byte) (0x26))
            {
                data_p2=rec;
                int[] intArray = new int[rec.length];
                int[] intArray1 = new int[rec.length];
                for (int i = 0; i < rec.length; intArray[i] = rec[i++]);
                for (int i = 0; i < data_p1.length; intArray1[i] = data_p1[i++]);
                switch(model)
                {
                    case "DIAMOND MOBILE":
                        parseRx25CmdAndRx26CmdToBloodGlucoseRec(Device_UUID.User.values()[1],intArray1,intArray);
                        break;
                    case "TNG SPO2":
                        parseRx25CmdAndRx26CmdToSpO2Rec(Device_UUID.User.values()[1],intArray1,intArray);
                        break;
                    case "DIAMOND CUFF BP":
                        parseRx25CmdAndRx26CmdToBloodPressure(Device_UUID.User.values()[1],intArray1,intArray);
                        break;
                    case "FORA IR21":
                        parseRx25CmdAndRx26CmdToTemperature(Device_UUID.User.values()[1],intArray1,intArray);
                        break;
                    case "TNG SCALE":

                        break;
                    case "FORA 6 CONNECT":
                        parseRx25CmdAndRx26CmdToBloodGlucoseRec(Device_UUID.User.values()[1],intArray1,intArray,characteristic1,gatt);
                        return;


                }

                broadcastUpdate(ACTION_CLEARING_DATA);
                characteristic1 =
                        gatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);

//                cmd = intArrayToByteArray(cmd_clear_device_data());
//                cmd=intArrayToByteArray(cmd_read_hr_pi_sp02());
                cmd = intArrayToByteArray(cmd_start_monitor());
            }
//            else if(rec[1]==(byte) (0x71))
//            {
//                broadcastUpdate(ACTION_CLEARING_DATA);
//                (ACTION_FETCHING_DATA);
//                byte[] test=characteristic.getValue();
////                    int[] intArray = new int[rec.length];
////                    for (int i = 0; i < rec.length; intArray[i] = rec[i++]);
//                get_data=true;
//                data_p1=rec;
//                if(get_data)
//                {
//                    byte[] combined = new byte[data_p1.length + rec.length];
//
//                    System.arraycopy(data_p1,0,combined,0         ,data_p1.length);
//                    System.arraycopy(rec,0,combined,data_p1.length,rec.length);
//                    data_p1=combined;
//                    if(data_p1.length>33)
//                    {
//                        int[] intArray = new int[data_p1.length];
//                        for (int i = 0; i < data_p1.length; intArray[i] = data_p1[i++]);
//                        parseRx71CmdToWeightScaleRec(intArray);
//                        get_data=false;
//                        broadcastUpdate(ACTION_CLEARING_DATA);
//                        characteristic1 =
//                                gatt.getService(Device_UUID.METER_UUID)
//                                        .getCharacteristic(Device_UUID.METER_UUID2);
//
//                        cmd = intArrayToByteArray(cmd_clear_device_data());
//                    }
//
//                }
//
//
//
//
//            }
//            else if(rec[1]==(byte) (0x49))
            else if(rec[1]==(byte) (0x47))
            {
                broadcastUpdate(ACTION_CONTINUOUS_DATA);
                int[] intArray = new int[rec.length];
                for (int i = 0; i < rec.length; intArray[i] = rec[i++]);
                characteristic1 =
                        gatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);
                cmd = intArrayToByteArray(cmd_clear_device_data());
//                    if(!get_data) {
//                try {
//                    response_live_data(intArray,characteristic1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//

            }
            else if(rec[1]==(byte) (0x52))
            {
                characteristic1 =
                        gatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);
//                cmd = intArrayToByteArray(cmd_turn_off_dev(0));
                cmd = intArrayToByteArray(cmd_getLive_data());
                check=true;
                characteristic1.setValue(cmd);
                gatt.writeCharacteristic(characteristic1);
                handler.removeCallbacks(runnable);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gatt.disconnect();

                    }
                },1000);

                return;
//                    gatt.disconnect();
//                    close();
            }
//                if(t!=null&&t.isAlive())
//                {
//                    t.interrupt();
//                    t=null;
//                }
//                t=new Thread(new Runnable() {
//                    @Override
//                    public void run() {
            check=true;
            characteristic1.setValue(cmd);
            gatt.writeCharacteristic(characteristic1);
            runnable=new Runnable() {
                @Override
                public void run() {
//                                    if(!check)
//                                                check=true;
                    System.out.println("@@@ Handler running");
                    characteristic1.setValue(cmd);
                    gatt.writeCharacteristic(characteristic1);
                    handler.postDelayed(runnable,1500);
                }
            };
            handler.postDelayed(runnable,1500);
//                            try {
//                                Thread.sleep(1500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }




//                    }
//                });
//                t.start();
        }
        private boolean refreshDeviceCache(BluetoothGatt gatt){
            try {

                Method localMethod = gatt.getClass().getMethod("refresh");
                return (boolean) (Boolean) localMethod.invoke(gatt);
            }
            catch (Exception localException) {
//            Log.e(TAG, "An exception occured while refreshing device");
                System.out.println("@@@"+localException.toString());
            }
            return false;
        }
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            check=false;
            if(runnable!=null)
            {
                handler.removeCallbacks(runnable);
                runnable=null;
            }

            BluetoothGattCharacteristic characteristic =
                    gatt.getService(Device_UUID.METER_UUID)
                            .getCharacteristic(Device_UUID.METER_UUID2);



//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                int[] cmd=cmd_getLive_data();
//                int[] cmd=cmd_get_num_record(Device_UUID.User.values()[1]);
//                int[] cmd=cmd_start_stop_monitoring(1,0);

            int[] cmd=get_cmd_set_device_date(Calendar.getInstance().getTime());
//                int[] cmd=cmd_get_data_p2(1, Device_UUID.User.values()[1]);
            characteristic.setValue(intArrayToByteArray(cmd));
            gatt.writeCharacteristic(characteristic);
            System.out.println("@@@Descriptor");


        }
    };
    public void set_system_time()
    {
        BluetoothGattCharacteristic characteristic =
                mBluetoothGatt.getService(Device_UUID.METER_UUID)
                        .getCharacteristic(Device_UUID.METER_UUID2);

        int[] cmd=get_cmd_set_device_date(Calendar.getInstance().getTime());
//                int[] cmd=cmd_get_data_p2(1, Device_UUID.User.values()[1]);
        characteristic.setValue(intArrayToByteArray(cmd));
        boolean success=mBluetoothGatt.writeCharacteristic(characteristic);
        while(!success){
            success=mBluetoothGatt.writeCharacteristic(characteristic);
        }
        System.out.println("@@@Descriptor");
    }

    private void handle_data(byte[] rec) {
        check=false;
        BluetoothGattCharacteristic characteristic1=null;
        byte cmd[]=null;
        retry_count=0;
        if(rec[1]==(byte) (0x33) )
        {
            broadcastUpdate(ACTION_CHECKING_DATA);
            characteristic1 =
                    mBluetoothGatt.getService(Device_UUID.METER_UUID)
                            .getCharacteristic(Device_UUID.METER_UUID2);
            cmd=intArrayToByteArray(cmd_get_num_record(Device_UUID.User.values()[1]));



        }
        else if(rec[1]==(byte) (0x2B))
        {
            int[] intArray = new int[rec.length];
            for (int i = 0; i < rec.length; intArray[i] = rec[i++]);
            int num_rec=get_num_records(Device_UUID.User.values()[1],intArray,false);
            if(num_rec>0)
            {


                broadcastUpdate(ACTION_DATA_AVAILABLE);
                characteristic1 =
                        mBluetoothGatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);
                cmd=intArrayToByteArray(cmd_get_data_p1(0, Device_UUID.User.values()[1]));



                System.out.println("@@@");
//
            }
            else
            {
                broadcastUpdate(ACTION_DATA_NOT_AVAILABLE);
                characteristic1 =
                        mBluetoothGatt.getService(Device_UUID.METER_UUID)
                                .getCharacteristic(Device_UUID.METER_UUID2);
                cmd = intArrayToByteArray(cmd_turn_off_dev(0));


//                        close();
            }

        }
        else if(rec[1]==(byte) (0x25))
        {
            broadcastUpdate(ACTION_FETCHING_DATA);
            data_p1=rec;
            characteristic1 =
                    mBluetoothGatt.getService(Device_UUID.METER_UUID)
                            .getCharacteristic(Device_UUID.METER_UUID2);
            cmd=intArrayToByteArray(cmd_get_data_p2(0, Device_UUID.User.values()[1]));


        }
        else if(rec[1]==(byte) (0x26))
        {
            data_p2=rec;
            int[] intArray = new int[rec.length];
            int[] intArray1 = new int[rec.length];
            for (int i = 0; i < rec.length; intArray[i] = rec[i++]);
            for (int i = 0; i < data_p1.length; intArray1[i] = data_p1[i++]);
            parseRx25CmdAndRx26CmdToSpO2Rec(Device_UUID.User.values()[1],intArray1,intArray);
            broadcastUpdate(ACTION_CLEARING_DATA);
            characteristic1 =
                    mBluetoothGatt.getService(Device_UUID.METER_UUID)
                            .getCharacteristic(Device_UUID.METER_UUID2);

            cmd = intArrayToByteArray(cmd_clear_device_data());



        }

//        else if(rec[1]==(byte) (0x49))
        else if(rec[1]==(byte) (0x47))
        {
            broadcastUpdate(ACTION_CONTINUOUS_DATA);
            int[] intArray = new int[rec.length];
            for (int i = 0; i < rec.length; intArray[i] = rec[i++]);
            characteristic1 =
                    mBluetoothGatt.getService(Device_UUID.METER_UUID)
                            .getCharacteristic(Device_UUID.METER_UUID2);
//                    if(!get_data) {
            try {
                response_live_data(intArray,characteristic1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//

        }
        else if(rec[1]==(byte) (0x52))
        {
            characteristic1 =
                    mBluetoothGatt.getService(Device_UUID.METER_UUID)
                            .getCharacteristic(Device_UUID.METER_UUID2);
            cmd = intArrayToByteArray(cmd_turn_off_dev(0));
//            check=true;
            characteristic1.setValue(cmd);
            mBluetoothGatt.writeCharacteristic(characteristic1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothGatt.disconnect();

                }
            },1000);

            return;
//                    gatt.disconnect();
//                    close();
        }
//                if(t!=null&&t.isAlive())
//                {
//                    t.interrupt();
//                    t=null;
//                }
//                t=new Thread(new Runnable() {
//                t=new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//        check=true;
        characteristic1.setValue(cmd);
        check=true;


        do
        {
            boolean success=mBluetoothGatt.writeCharacteristic(characteristic1);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("@@@ RESEND COMMAND");
            success=mBluetoothGatt.writeCharacteristic(characteristic1);
            retry_count++;
        }while(check&&retry_count<retry);
//        runnable=new Runnable() {
//            @Override
//            public void run() {
////                                    if(!check)
////                                                check=true;
//                System.out.println("@@@ Handler running");
//                characteristic1.setValue(cmd);
//                gatt.writeCharacteristic(characteristic1);
//                handler.postDelayed(runnable,1500);
//            }
//        };
//        handler.postDelayed(runnable,1500);
//                            try {
//                                Thread.sleep(1500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }




//                    }
//                });
//                t.start();
    }

    private void call_descriptor(){
        BluetoothGattCharacteristic characteristic =
                mBluetoothGatt.getService(Device_UUID.METER_UUID)
                        .getCharacteristic(Device_UUID.METER_UUID2);
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);

        final BluetoothGattDescriptor descriptor =
                characteristic.getDescriptors().get(0);

        descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        boolean success=mBluetoothGatt.writeDescriptor(descriptor);
        System.out.println("@@@ descriptor");
        if(!success)
        {
            reconnect=true;
            mBluetoothGatt.disconnect();
            System.out.println("@@@ descriptor");
        }
//                runnable=new Runnable() {
//                    @Override
//                    public void run() {
//                        reconnect=true;
//                        gatt.disconnect();
//
////                                    if(!check)
////                                                check=true;
////                        System.out.println("@@@ Handler service"+status);
////                        descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
////                        gatt.writeDescriptor(descriptor);
////                        handler.postDelayed(runnable,30000);
//                    }
//                };
//                handler.postDelayed(runnable,5000);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

//    private void broadcastUpdate(final String action,
//                                 final BluetoothGattCharacteristic characteristic) {
//        final Intent intent = new Intent(action);
//
//        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
//        // carried out as per profile specifications:
//        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//
//            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
//            sendBroadcast(intent);
//
//            mHeartRateForArtikCloud = heartRate;
//
//
//        }
//        // Comment out the original code that sends other measurement data to UI so that
//        // only heart rate data is sent to UI and then to ARTIK Cloud
////        else {
////            // For all other profiles, writes the data formatted in HEX.
////            Log.d(TAG, "broadcastUpdate(action, characteristic): characteristics.getUuid = " + characteristic.getUuid());
////
////            final byte[] data = characteristic.getValue();
////            if (data != null && data.length > 0) {
////                final StringBuilder stringBuilder = new StringBuilder(data.length);
////                for(byte byteChar : data)
////                    stringBuilder.append(String.format("%02X ", byteChar));
////                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
////            }
////            sendBroadcast(intent);
////        }
//
//    }

    public class LocalBinder extends Binder {
        public BluetoothLeService1 getService() {
            return BluetoothLeService1.this;
        }
    }

//    @androidx.annotation.Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    //    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @param dev_Name
     * @param type
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address, String dev_Name, String type) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        model=dev_Name;
        giventype=type;
        // Previously connected device.  Try to reconnect.
        if (address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
//        if(hasStarted)
//        {

        if(!hasStarted){
            if(t!=null)
                t=null;
            t = new Timer();
            task=new TimerTask() {
                @Override
                public void run() {
                    hasStarted=true;
                    sec++;
                    System.out.println("@@@running");
                    if(sec==15){
                        close();
                        mBluetoothGatt=null;
                        t.cancel();
                        t.purge();
                        hasStarted=false;
                        sec=0;
                        broadcastUpdate(ACTION_GATT_DEVICE_NOT_FOUND);

                    }
                }

            };
            t.schedule(task, 0,1000);
        }
        hasStarted=true;

//        }
        mBluetoothAdapter.startDiscovery();
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        System.out.println("@@@entered device");
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }
    public boolean connect_internal(final String address, String dev_Name, String type) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        model=dev_Name;
        giventype=type;
        // Previously connected device.  Try to reconnect.
        if (address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
//        if(hasStarted)
//        {


//        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        System.out.println("@@@entered device");
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public void  parseRx25CmdAndRx26CmdToSpO2Rec(Device_UUID.User user, int[] rx25Cmd, int[] rx26Cmd)  {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx25Cmd);
        rxCmdList.add(rx26Cmd);
        List<Integer> verifyCmdList = new ArrayList();
        verifyCmdList.add(Integer.valueOf(37));
        verifyCmdList.add(Integer.valueOf(38));
        Date measureTime = convertRxCmdToDateObj(rx25Cmd);
        int spO2 = (rx26Cmd[3] << 8) + rx26Cmd[2];
        int pulse = rx26Cmd[5];
        if(pulse<0)
            pulse=256+pulse;
        System.out.println("@@@User (0:CurrentUser , 1:User1 , 2:User2 , 3:User3 , 4:User4) : " + user);
        System.out.println("@@@Measure Time : " + measureTime);
        System.out.println("@@@spO2 Value : " + spO2);
        System.out.println("@@@pulse Value : " + pulse);
        final Intent i1 = new Intent("Receiving_Data");
        SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        i1.putExtra("DATE",f2.format(measureTime));
        i1.putExtra("TYPE","Pulse Oximeter");
        i1.putExtra("VAL1", Integer.toString(pulse));
        i1.putExtra("VAL2", Integer.toString(spO2));
        i1.putExtra("VAL3","");
        i1.putExtra("VAL4","");
        i1.putExtra("MANUF","FORA");
//        sendBroadcast(i1);
    }

    public void parse61Data(byte[] array){
        int sp02 = array[2];
        int pulse = array[4];
        float pi = array[3] / 10f;
        final Intent i1 = new Intent(ACTION_DATA_RECEIVED);
        i1.putExtra(PARAM_SP02 , sp02);
        i1.putExtra(PARAM_PULSE , pulse);
        i1.putExtra(PARAM_PI , pi);
        Log.d("ble_test", "parse61Data: "+sp02);
        sendBroadcast(i1);
    }

    public void parse60Data(){
        final Intent i1 = new Intent(ACTION_DATA_RECEIVED);
//        i1.putExtra("hf",Collections.min(irlist));
//        i1.putExtra("lf",Collections.max(irlist));
        sendBroadcast(i1);
    }

    public  void response_live_data(int[] rx49Cmd, BluetoothGattCharacteristic characteristic) throws InterruptedException {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx49Cmd);
        List<Integer> verifyCmdList = new ArrayList();
        verifyCmdList.add(Integer.valueOf(73));
//        verifyRxCmd(rxCmdList, verifyCmdList, false);
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
        Date measureTime = calendar.getTime();
        int spO2 = (rx49Cmd[3] << 8) + rx49Cmd[2];
        int pulse = rx49Cmd[5];
        if (pulse<0)
            pulse=256+pulse;

        System.out.println("@@@SpO2 : " + spO2);
        System.out.println("@@@Pulse : " + pulse);
        if(spO2==0)
        {
            Thread.sleep(1000);
            byte[] cmd = intArrayToByteArray(cmd_getLive_data());
            characteristic.setValue(cmd);
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
        else
        {
            byte[] cmd = intArrayToByteArray(cmd_clear_device_data());
            characteristic.setValue(cmd);
            mBluetoothGatt.writeCharacteristic(characteristic);
            final Intent i1 = new Intent("Receiving_Data");
            SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            i1.putExtra("DATE",f2.format(measureTime));
            i1.putExtra("TYPE","Pulse Oximeter");
            i1.putExtra("VAL1", Integer.toString(spO2));
            i1.putExtra("VAL2", Integer.toString(pulse));
            i1.putExtra("VAL3","");
            i1.putExtra("VAL4","");
            i1.putExtra("MANUF","FORA");
            sendBroadcast(i1);

        }

    }
    public  void parseRx71CmdToWeightScaleRec(int[] rx71Cmd) {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx71Cmd);
        List<Integer> verifyCmdList = new ArrayList();
        verifyCmdList.add(Integer.valueOf(113));
//        verifyRxCmd(rxCmdList, verifyCmdList, false);
        Date measureTime = convertRxCmdToDateObj(rx71Cmd);
        int stableTime = rx71Cmd[3];
        int code = rx71Cmd[9];
//        GenderType gender = GenderType.NotDefined;
//        switch (rx71Cmd[10]) {
//            case 0:
//                gender = GenderType.Female;
//                break;
//            case 1:
//                gender = GenderType.Male;
//                break;
//            default:
//                gender = GenderType.NotDefined;
//                break;
//        }
        int height = rx71Cmd[11];
        double weight = ((double) ((rx71Cmd[16] << 8) + rx71Cmd[17])) * 0.1d;

        int age = rx71Cmd[14];
        double bf = ((double) ((rx71Cmd[20] << 8) + rx71Cmd[21])) * 0.1d;
        int bmr = (rx71Cmd[22] << 8) + rx71Cmd[23];
        double bmi = ((double) ((rx71Cmd[24] << 8) + rx71Cmd[25])) * 0.1d;
        Log.d(TAG, "Measure Time : " + measureTime);
        Log.d(TAG, "Code : " + code);
//        Log.d(TAG, "Gender : " + gender.toString());
        Log.d(TAG, "Height : " + height);
        Log.d(TAG, "Weight : " + weight);
        Log.d(TAG, "Age : " + age);
        Log.d(TAG, "BMI : " + bmi);
        Log.d(TAG, "BMR : " + bmr);
        Log.d(TAG, "BF : " + bf);
        if(height<0)
            height=256+height;
        final Intent i1 = new Intent("Receiving_Data");
        SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        i1.putExtra("DATE","20"+rx71Cmd[4]+"-"+checkDigit(rx71Cmd[5])+"-"+checkDigit(rx71Cmd[6])+" "+checkDigit(rx71Cmd[7])+":"+checkDigit(rx71Cmd[8])+":00");
        i1.putExtra("TYPE","Weight Scale");
        i1.putExtra("VAL1", String.format("%.1f",weight));
        i1.putExtra("VAL2", Integer.toString(height));
        i1.putExtra("VAL3","");
        i1.putExtra("VAL4","");
        i1.putExtra("MANUF","FORA");
        sendBroadcast(i1);
    }
    public String checkDigit(int number)
    {
        return number<=9?"0"+number: String.valueOf(number);
    }
    public void parseRx25CmdAndRx26CmdToBloodGlucoseRec(Device_UUID.User user, int[] rx25Cmd, int[] rx26Cmd) {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx25Cmd);
        rxCmdList.add(rx26Cmd);
        List<Integer> verifyCmdList = new ArrayList();
        verifyCmdList.add(37);
        verifyCmdList.add(38);
        Date measureTime = convertRxCmdToDateObj(rx25Cmd);
        boolean transmitted = false;
        if (((rx25Cmd[5] >> 6) & 1) == 1) {
            transmitted = true;
        }
        int glucoseValue = (rx26Cmd[3] << 8) + rx26Cmd[2];
        if(glucoseValue<0)
            glucoseValue=256+glucoseValue;
        int ambientValue = rx26Cmd[4];
        int codeNo = rx26Cmd[5] & 63;
        Device_UUID.BloodGlucoseType type = Device_UUID.BloodGlucoseType.General;
        switch (rx26Cmd[5] >> 6) {
            case 1:
                type = Device_UUID.BloodGlucoseType.AC;
                break;
            case 2:
                type = Device_UUID.BloodGlucoseType.PC;
                break;
            case 3:
                type = Device_UUID.BloodGlucoseType.QC;
                break;
            default:
                type = Device_UUID.BloodGlucoseType.General;
                break;
        }
//        BloodGlucoseType type2 = convertToType2((rx26Cmd[5] & 60) >> 2);
        System.out.println("@@@User (0:CurrentUser , 1:User1 , 2:User2 , 3:User3 , 4:User4) : " + user);
        System.out.println("@@@Measure Time : " + measureTime);
        System.out.println("@@@The reading has been transmitted or not : " + transmitted);
        System.out.println("@@@Glucose Value : " + glucoseValue);
        System.out.println("@@@Ambient Value : " + ambientValue);
        System.out.println("@@@Code No : " + codeNo);
        System.out.println("@@@Type (0:General , 1:AC , 2:PC , 3:QC) : " + type.getValue());
//        System.out.println("@@@Type2 (0:General , 6:HEMATOCRIT , 7:KETONE) : " + type2.getValue());
        final Intent i1 = new Intent("Receiving_Data");
        SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        i1.putExtra("DATE",f2.format(measureTime));
        i1.putExtra("TYPE","Blood Glucose");
        i1.putExtra("VAL1", Integer.toString(glucoseValue));
        i1.putExtra("VAL2", Integer.toString(type.getValue()));
        i1.putExtra("VAL3","");
        i1.putExtra("VAL4","");
        i1.putExtra("MANUF","FORA");
        sendBroadcast(i1);


    }
    public void parseRx25CmdAndRx26CmdToBloodPressure(Device_UUID.User user, int[] rx25Cmd, int[] rx26Cmd) {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx25Cmd);
        rxCmdList.add(rx26Cmd);
        List<Integer> verifyCmdList = new ArrayList();
        verifyCmdList.add(Integer.valueOf(37));
        verifyCmdList.add(Integer.valueOf(38));
//        verifyRxCmd(rxCmdList, verifyCmdList, false);
        Date measureTime = convertRxCmdToDateObj(rx25Cmd);
        boolean arrhy = false;
        if (((rx25Cmd[4] >> 6) & 1) == 1) {
            arrhy = true;
        }
        boolean eve = false;
        if (((rx25Cmd[5] >> 5) & 1) == 1) {
            eve = true;
        }
        boolean transmitted = false;
        if (((rx25Cmd[5] >> 6) & 1) == 1) {
            transmitted = true;
        }
        boolean avg = false;
        if ((rx25Cmd[5] >> 7) == 1) {
            avg = true;
        }

        int systolicValue = rx26Cmd[2];
        int mapValue = rx26Cmd[3];
        int diastolicValue = rx26Cmd[4];
        int pulseValue = rx26Cmd[5];
        if(systolicValue<0)
            systolicValue=256+systolicValue;
        if(diastolicValue<0)
            diastolicValue=256+diastolicValue;
        if(pulseValue<0)
            pulseValue=256+pulseValue;
        System.out.println("@@@User (0:CurrentUser , 1:User1 , 2:User2 , 3:User3 , 4:User4) : " + user);
        System.out.println("@@@Measure Time : " + measureTime);
        System.out.println("@@@Is Arrhythmia or not(true:Arrhythmia , false:Normal) : " + arrhy);
        System.out.println("@@@Is evening time measurement(true:Evening time measurement, false:Day time measurement) : " + eve);
        System.out.println("@@@The reading has been transmitted or not : " + transmitted);
        System.out.println("@@@Is average measurement reading(true:Average measurement reading, false:Single measurement reading) : " + avg);
        System.out.println("@@@Systolic Value : " + systolicValue);
        System.out.println("@@@MAP Value : " + mapValue);
        System.out.println("@@@Diastolic Value : " + diastolicValue);
        System.out.println("@@@Pulse Value : " + pulseValue);
        final Intent i1 = new Intent("Receiving_Data");
        SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        i1.putExtra("DATE",f2.format(measureTime));
        i1.putExtra("TYPE","Blood Pressure");
        i1.putExtra("VAL1", Integer.toString(systolicValue));
        i1.putExtra("VAL2", Integer.toString(diastolicValue));
        i1.putExtra("VAL3", Integer.toString(pulseValue));
        i1.putExtra("VAL4", Integer.toString(mapValue));
        i1.putExtra("MANUF","FORA");
        sendBroadcast(i1);

    }
    public void parseRx25CmdAndRx26CmdToTemperature(Device_UUID.User user, int[] rx25Cmd, int[] rx26Cmd) {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx25Cmd);
        rxCmdList.add(rx26Cmd);
        List<Integer> verifyCmdList = new ArrayList();
        verifyCmdList.add(Integer.valueOf(37));
        verifyCmdList.add(Integer.valueOf(38));
//        verifyRxCmd(rxCmdList, verifyCmdList, false);
        Date measureTime = convertRxCmdToDateObj(rx25Cmd);
        Device_UUID.ObjectType objectType = Device_UUID.ObjectType.Ear;
        switch (rx25Cmd[4] >> 6) {
            case 0:
                objectType = Device_UUID.ObjectType.Ear;
                break;
            case 1:
                objectType = Device_UUID.ObjectType.ForeHead;
                break;
            case 4:
                objectType = Device_UUID.ObjectType.Body;
                break;
        }
        boolean isTxed = false;
        if (((rx25Cmd[5] >> 6) & 1) == 1) {
            isTxed = true;
        }
        double objectTemperatureValue = ((double) ((rx26Cmd[3] << 8) + rx26Cmd[2])) * 0.1d;
        double ambientTemperatureValue = ((double) ((rx26Cmd[5] << 8) + rx26Cmd[4])) * 0.1d;
        System.out.println("@@@User (0:CurrentUser , 1:User1 , 2:User2 , 3:User3 , 4:User4) : " + user);
        System.out.println("@@@Measure Time : " + measureTime);
        System.out.println("@@@ObjectType (0:Ear Temperature , 1:Forehead Ear Temperature , 4:Body) : " + objectType.getValue());
        System.out.println("@@@The reading has been transmitted or not : " + isTxed);
        System.out.println("@@@Object Temperature Value : " + objectTemperatureValue);
        System.out.println("@@@Ambient Temperature Value : " + ambientTemperatureValue);
        final Intent i1 = new Intent("Receiving_Data");
        SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        i1.putExtra("DATE",f2.format(measureTime));
        i1.putExtra("TYPE","Temperature");
        i1.putExtra("VAL1", String.format("%.1f",objectTemperatureValue));
        i1.putExtra("VAL2","c");
        i1.putExtra("VAL3","");
        i1.putExtra("VAL4","");
        i1.putExtra("MANUF","FORA");
        sendBroadcast(i1);
    }
    public void parseRx25CmdAndRx26CmdToBloodGlucoseRec(Device_UUID.User user, int[] rx25Cmd, int[] rx26Cmd, BluetoothGattCharacteristic characteristic1, BluetoothGatt gatt) {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx25Cmd);
        rxCmdList.add(rx26Cmd);
//        List<Integer> verifyCmdList = new ArrayList();
//        verifyCmdList.add(37);
//        verifyCmdList.add(38);
        Date measureTime = convertRxCmdToDateObj(rx25Cmd);
        boolean transmitted = false;
        if (((rx25Cmd[5] >> 6) & 1) == 1) {
            transmitted = true;
        }
        int glucoseValue = (rx26Cmd[3] << 8) + rx26Cmd[2];
        if(glucoseValue<0)
            glucoseValue=256+glucoseValue;
        int ambientValue = rx26Cmd[4];
        int codeNo = rx26Cmd[5] & 63;
        Device_UUID.BloodGlucoseType type = Device_UUID.BloodGlucoseType.General;
        int type1=rx26Cmd[5] >> 6;
        int type2 = ((rx26Cmd[5] & 60) >> 2);
        if(get_data)
        {
            if((type2==6||type2==11)&&glucoseValue!=-1)
            {
                rec_type="Blood Glucose";
                val3= Float.toString(glucoseValue);
                val4= Float.toString((float) (glucoseValue/2.95));
            }
            else
            {

                val2="";
                val3="";
                val4="";
            }
        }
        else
        {
//            if(type1>=0&&type1<=3&&type2<5)
            if(type1<=3&&type2<5)
            {
                switch (type1) {
                    case 1:
                        type = Device_UUID.BloodGlucoseType.AC;
                        break;
                    case 2:
                        type = Device_UUID.BloodGlucoseType.PC;
                        break;
                    case 3:
                        type = Device_UUID.BloodGlucoseType.QC;
                        break;
                    default:
                        type = Device_UUID.BloodGlucoseType.General;
                        break;
                }
                if(num_rec>1)
                {
                    val1= Float.toString(glucoseValue);
                    rec_type="Blood Glucose";
                    get_data=true;
                    byte[] cmd=intArrayToByteArray(cmd_get_data_p2(1, Device_UUID.User.values()[1]));
                    characteristic1.setValue(cmd);
                    mBluetoothGatt.writeCharacteristic(characteristic1);
                    return;
                }
            }
            else if(type2>=7&&type2<=12)
            {
                switch (type2) {
//                case 6:
//                    rec_type="HEMATOCRIT";
//                    break;
                    case 7:
                        rec_type="Ketone";
                        System.out.println("@@@"+ Math.round((glucoseValue/30.0) * 100.0) / 100.0);
                        val1=""+ Math.round((glucoseValue/30.0) * 100.0) / 100.0;
                        break;
                    case 8:
                        rec_type="Uric Acid";
                        val1= String.format("%.1f",((float) (glucoseValue/10.0)));
                        break;
                    case 9:
                        rec_type="Cholesterol";
                        val1= String.format("%.1f",(float) (glucoseValue));
                        break;
                    case 11:
                        rec_type="Hemoglobin";
                        val1= String.format("%.1f",(float) (glucoseValue/10.0));
                        break;
                    case 12:
                        rec_type="Lactate";
                        val1= String.format("%.1f",(float) (glucoseValue/9.008));
                        break;
                }
            }
        }

        if(!rec_type.equals(giventype))
        {
            final Intent i1 = new Intent(MISMATCH_READING);
            sendBroadcast(i1);
            gatt.disconnect();
            return;
        }

        broadcastUpdate(ACTION_CLEARING_DATA);
        byte[] cmd = intArrayToByteArray(cmd_clear_device_data());
        characteristic1.setValue(cmd);
        gatt.writeCharacteristic(characteristic1);
        final Intent i1 = new Intent("Receiving_Data");
        SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        i1.putExtra("DATE",f2.format(measureTime));
        i1.putExtra("TYPE",giventype);
        i1.putExtra("VAL1",val1);
        i1.putExtra("VAL2",val2);
        i1.putExtra("VAL3",val3);
        i1.putExtra("VAL4",val4);
        i1.putExtra("MANUF","FORA");
        sendBroadcast(i1);
//        }

        System.out.println("@@@User (0:CurrentUser , 1:User1 , 2:User2 , 3:User3 , 4:User4) : " + user);
        System.out.println("@@@Measure Time : " + measureTime);
        System.out.println("@@@The reading has been transmitted or not : " + transmitted);
        System.out.println("@@@Glucose Value : " + glucoseValue);
        System.out.println("@@@Ambient Value : " + ambientValue);
        System.out.println("@@@Code No : " + codeNo);
        System.out.println("@@@Type (0:General , 1:AC , 2:PC , 3:QC) : " + type.getValue());
//        System.out.println("@@@Type2 (0:General , 6:HEMATOCRIT , 7:KETONE) : " + type2.getValue());



    }


    List<Integer> getList(){
        return irlist;
    }

}
