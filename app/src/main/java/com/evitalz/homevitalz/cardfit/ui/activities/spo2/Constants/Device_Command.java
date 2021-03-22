package com.evitalz.homevitalz.cardfit.ui.activities.spo2.Constants;

import android.util.Log;

import androidx.core.view.MotionEventCompat;

import com.evitalz.homevitalz.cardfit.ui.activities.connect_device.Device_UUID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Device_Command {

     public static int[] get_cmd_set_device_date(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int[] cmd=new int[]{81, 51, ((month & 7) << 5) + day, (year << 1) + (month >> 3), minute, hour, 163,0};
        return new int[]{81, 51, ((month & 7) << 5) + day, (year << 1) + (month >> 3), minute, hour, 163, calculateOneByteCheckSum(cmd, 0, cmd.length - 2)};
    }
    public static int[] createTx71Cmd(int dataIndex) {
//        isOutOfIndex(dataIndex);
        int dataLowIndex = convertDataLowIndex(dataIndex);
        int dataHighIndex = convertDataHighIndex(dataIndex);
        return appendOneByteCheckSumToCmd(new int[]{81, 113, 2, dataLowIndex, dataHighIndex, 163});
    }
    public static int[] createTx51Cmd(int dataIndex) {
//        isOutOfIndex(dataIndex);
        int dataLowIndex = convertDataLowIndex(dataIndex);
        int dataHighIndex = convertDataHighIndex(dataIndex);
        return appendOneByteCheckSumToCmd(new int[]{0x51, 71, 2, dataLowIndex, dataHighIndex, 163});
    }
     public static int[] cmd_get_data_p1(int dataIndex, Device_UUID.User user) {
//        isOutOfIndex(dataIndex);
        int dataLowIndex = convertDataLowIndex(dataIndex);
        int dataHighIndex = convertDataHighIndex(dataIndex);
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 37;
        iArr[2] = dataLowIndex;
        iArr[3] = dataHighIndex;
        iArr[5] = user.getValue();
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }
    public static int[] cmd_get_data_p2(int dataIndex, Device_UUID.User user) {
        int dataLowIndex = convertDataLowIndex(dataIndex);
        int dataHighIndex = convertDataHighIndex(dataIndex);
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 38;
        iArr[2] = dataLowIndex;
        iArr[3] = dataHighIndex;
        iArr[5] = user.getValue();
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }

    public static int readIR(byte[] array){
         int ir1 = array[2] + (array[3] << 4);
         int ir2 = array[4] + (array[5] << 4);
        Log.d("ble_test", "readIR: ir1 => "+ir1 + " ir2 =>"+ir2);
        return ir1;
    }

    public static int[] cmd_getLive_data() {
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 73;
//        iArr[4] = 4;
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }
    public static int[] cmd_get_num_record(Device_UUID.User user) {
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 43;
        iArr[2] = user.getValue();
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }
    public static int get_num_records(Device_UUID.User user, int[] rx2BCmd, boolean is_BASE_FFFF) {
        List<int[]> rxCmdList = new ArrayList();
        rxCmdList.add(rx2BCmd);
        List<Integer> verifyCmdList = new ArrayList();
        verifyCmdList.add(Integer.valueOf(43));
        int storageNumber = (rx2BCmd[3] << 8) + rx2BCmd[2];
        int newestIndex = (rx2BCmd[5] << 8) + rx2BCmd[4];
        if (is_BASE_FFFF) {
            if (storageNumber == 65535) {
                storageNumber = 0;
                newestIndex = 0;
            } else {
                storageNumber++;
                newestIndex++;
            }
        }
        System.out.println("@@@User(0:CurrentUser , 1:User1 , 2:User2 , 3:User3 , 4:User4) : " + user);
        System.out.println("@@@Storage Number : " + storageNumber);
        System.out.println("@@@Newest Index : " + newestIndex);
        return storageNumber;
    }
    public static int[] cmd_clear_device_data() {
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 82;
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }

    public static int[] cmd_read_hr_pi_sp02() {
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 97;
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }
    public static int[] cmd_turn_off_dev(int deviceIndex) {
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 80;
        iArr[2] = deviceIndex;
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }
    public static int[] cmd_start_monitor() {
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 71;
        iArr[2] = 19;
        iArr[3] = 2;
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }

    public static int[] cmd_config() {
        int[] iArr = new int[7];
        iArr[0] = 81;
        iArr[1] = 58;
        iArr[2] = 4;
        iArr[3] = 0;
        iArr[4] = 0;
        iArr[6] = 163;
        return appendOneByteCheckSumToCmd(iArr);
    }

//    private static void verifyRxCmd(List<int[]> rxCmdList, List<Integer> verifyCmdList, boolean needToDoLastTwoByteCheckSum) {
//        int i = 0;
//        while (i < rxCmdList.size()) {
//            if (!isDoLastByteCheckSumSuccess((int[]) rxCmdList.get(i))) {
//                throw new CheckSumErrException();
//            } else if (needToDoLastTwoByteCheckSum && !isDoTwoByteCheckSumSuccess((int[]) rxCmdList.get(i))) {
//                throw new CheckSumErrException();
//            } else if (((int[]) rxCmdList.get(i))[1] != ((Integer) verifyCmdList.get(i)).intValue()) {
//                throw new MeterCmdWrongException(String.format("%s content incorrect.", new Object[]{convertToHexString((int[]) rxCmdList.get(i))}));
//            } else {
//                i++;
//            }
//        }
//    }
     public static Date convertRxCmdToDateObj(int[] rxCmd) {
        int day = 0;
        int month = 0;
        int year = 0;
        int minute = 0;
        int hour = 0;
        if(rxCmd[2]<0)
            rxCmd[2]=256+rxCmd[2];
        switch (rxCmd[1]) {
            case 35 /*35*/:
            case 37:
            case 38:
                day = rxCmd[2] & 31;
                month = (rxCmd[2] >> 5) + ((rxCmd[3] & 1) << 3);
                year = (rxCmd[3] >> 1) + 2000;
                minute = rxCmd[4] & 63;
                hour = rxCmd[5] & 31;
                break;
//            case PCLinkLibraryConstant.CMD_29 /*41*/:
//                day = rxCmd[2] & 31;
//                month = (rxCmd[2] >> 5) + ((rxCmd[3] & 1) << 3);
//                year = (rxCmd[3] >> 1) + PCLinkLibraryConstant.hTC_DEVICE_UNPAIR_AND_PAIRED_DELAY_TIME;
//                break;
//            case PCLinkLibraryConstant.CMD_71 /*113*/:
//                day = rxCmd[6];
//                month = rxCmd[5];
//                year = rxCmd[4] + PCLinkLibraryConstant.hTC_DEVICE_UNPAIR_AND_PAIRED_DELAY_TIME;
//                minute = rxCmd[8];
//                hour = rxCmd[7];
//                break;
//            case PCLinkLibraryConstant.CMD_84 /*132*/:
//                day = rxCmd[4] & 31;
//                month = (rxCmd[4] >> 5) + ((rxCmd[5] & 1) << 3);
//                year = ((rxCmd[5] & 30) >> 1) + PCLinkLibraryConstant.hTC_DEVICE_UNPAIR_AND_PAIRED_DELAY_TIME;
//                minute = rxCmd[6] & 63;
//                hour = rxCmd[7] & 31;
//                break;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static int calculateOneByteCheckSum(int[] cmd, int startIndex, int endIndex) {
        int checkSum = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            checkSum += cmd[i];
        }
        return checkSum & MotionEventCompat.ACTION_MASK;
    }
     public static int[] appendOneByteCheckSumToCmd(int[] sourceCmd) {
        int checkSum = calculateOneByteCheckSum(sourceCmd, 0, sourceCmd.length - 1);
        int[] cmdWithCheckSum = new int[(sourceCmd.length + 1)];
        System.arraycopy(sourceCmd, 0, cmdWithCheckSum, 0, sourceCmd.length);
        cmdWithCheckSum[cmdWithCheckSum.length - 1] = checkSum;

        return cmdWithCheckSum;
    }
     static int convertDataLowIndex(int dataIndex) {
        if (dataIndex > MotionEventCompat.ACTION_MASK) {
            return dataIndex & MotionEventCompat.ACTION_MASK;
        }
        return dataIndex;
    }
     static int convertDataHighIndex(int dataIndex) {
        if (dataIndex > MotionEventCompat.ACTION_MASK) {
            return dataIndex >> 8;
        }
        return 0;
    }
    public static byte[] intArrayToByteArray(int[] src) {
        int srcLength = src.length;
        byte[] dst = new byte[srcLength];
        for (int i = 0; i < srcLength; i++) {
            dst[i] = (byte) src[i];
        }
        return dst;
    }

}
