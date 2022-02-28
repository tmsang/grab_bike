package com.intec.grab.bike_driver.utils.log;

public class UncaughtExceptionHandler {
    public static void registerCurrentThread() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Log.e("uncaught exception", e));
    }
}
