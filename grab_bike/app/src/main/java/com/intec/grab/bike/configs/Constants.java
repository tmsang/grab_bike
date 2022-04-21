package com.intec.grab.bike.configs;

public class Constants {
    /** =========================================================
     *  CONFIGURATION
     *  ========================================================== */
    //  Configuration: LOCAL
    public static final String API_NET = "https://172.25.129.73:44331";                         // For Real Device: device-login (For Emulator: http://10.0.2.2:1234/)
    //public static final String API_NET = "https://3.228.124.204:44331";                         // For Real Device: device-login (For Emulator: http://10.0.2.2:1234/)

    public static final String BING_MAP_KEY = "AuZD1lfJajlhr_Cx6GVG9uR4jzS5Y-PF3EWWGrM0SgdGBUh_8D3fvER4D-Xxco2r";

    // Common
    public static final String PUSH_TYPE_SERVICE = "2";          // 1: APNs, 2: FCM, 4: WebPush, 5: ADM
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PushNotificationDBManager";

    /** =========================================================
     *  CONSTANT
     *  ========================================================== */
    public static final String ROW_KEY_TOKEN_JWT = "token_jwt";

    // Password must contain at least one digit [0-9].
    // Password must contain at least one lowercase Latin character [a-z].
    // Password must contain at least one uppercase Latin character [A-Z].
    // Password must contain at least one special character like ! @ # & ( ).
    // Password must contain a length of at least 8 characters and a maximum of 20 characters.
    public static final String PASSWORD_POLICY =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";


    /** =========================================================
     *  OTHER
     *  ========================================================= */
    //public static final String BASE_URL = "https://3.228.124.204:44331";
    public static final String BASE_URL = "https://172.25.129.73:44331";
    //public static final String BASE_URL = "http://163.43.108.188";            // IRIS
}
