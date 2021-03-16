package com.evitalz.homevitalz.cardfit.ui.activities.connect_device;/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    public static final UUID ClientCharacteristicConfiguration = uuidFromShortString("2902");
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final UUID BloodPressureService = uuidFromShortString("1810");
    public static final UUID BloodPressureMeasurement = uuidFromShortString("2a35");
    public static String WEIGHT_AD_SERVICE = "23434100-1FE4-1EFF-80CB-00FF78297D8B";
    public static String WEIGHT_AD_CHAR = "23434101-1FE4-1EFF-80CB-00FF78297D8B";
    public static String WEIGHT_SERVICE = "181D-0000-1000-8000-00805f9b34fb";
    public static String WEIGHT_CHAR = "2A9D-0000-1000-8000-00805f9b34fb";
    public static final String KEY_TEMPERATURE_UNIT = "temperatureUnit";
    public static final String KEY_TEMPERATURE_VALUE = "temperatureValue";
    public static final String KEY_TEMPERATURE_TYPE = "temperatureType";
    static {
        // Sample1 Services.
        //attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        //attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(BloodPressureService.toString(),"Blood Pressure Service");
        attributes.put(BloodPressureMeasurement.toString(),"Blood Pressure Service");

        // Sample1 Characteristics.
        //attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        //attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
    public static UUID uuidFromShortString(String uuid) {
        return UUID.fromString(String.format("0000%s-0000-1000-8000-00805f9b34fb", uuid));
    }
}
