package com.evitalz.homevitalz.cardfit.ui.activities.connect_device;

import java.util.UUID;

public class Device_UUID {
    public static final UUID METER_UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
    public static final UUID YASEE_METER_UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    public static final UUID YASEE_SERVICE_UUID = UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb");
    public static final UUID D_HEART_S_UUID = UUID.fromString("6cac23ef-6245-4c08-921e-d9245e5a257a");
    public static final UUID Bio_S_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public static final UUID Bio_C_UUID = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    public static final UUID D_HEART_C_UUID = UUID.fromString("2787300a-35a1-448a-bd8f-e0630e5432ea");
    public static final UUID METER_UUID2 = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    public static final UUID GATT_SERVICE_PRIMARY = UUID.fromString( "00001000-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_WRITEABLE = UUID.fromString("00001001-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_NOTIFY = UUID.fromString("00001002-0000-1000-8000-00805f9b34fb");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG1 = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_READABLE = UUID.fromString("00001003-0000-1000-8000-00805f9b34fb");
    public static final UUID BP_SERVICE = UUID.fromString("D44BC439-ABFD-45A2-B575-925416129601");

    public enum User {
        CurrentUser(0),
        User1(1),
        User2(2),
        User3(3),
        User4(4),
        AllUser(122);

        private final int value;

        private User(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
    public enum BloodGlucoseType {
        General(0),
        AC(1),
        PC(2),
        QC(3),
        HEMATOCRIT(6),
        KETONE(7),
        HB(10),
        UA(11),
        CHOL(12),
        LACTATE(13),
        UNKNOWN(99);

        private final int value;

        private BloodGlucoseType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
    public enum MeterUsers {
        SingleUserTypeZero(0),
        SingleUserTypeOne(1),
        TwoUsers(2),
        FourUsers(4);

        private final int value;

        private MeterUsers(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
    public enum ObjectType {
        Ear(0),
        ForeHead(1),
        Body(4);

        private final int value;

        private ObjectType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
