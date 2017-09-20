package com.example.urionclass;


import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class SampleGattAttributes {


    public static String SERVICE_UU = "0000FFF0-0000-1000-8000-00805F9B34FB";
    public static String NOTIFY_UU = "0000FFF1-0000-1000-8000-00805F9B34FB";//读
    public static String WRITE_UU = "0000FFF2-0000-1000-8000-00805F9B34FB";//读
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String DISCONNECTEDBLE = "com.example.urionapp.disconnected";

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    private static HashMap<String, String> attributes = new HashMap();

    static {
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = (String) attributes.get(uuid);
        return name == null ? defaultName : name;
    }


}
