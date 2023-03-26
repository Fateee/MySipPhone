package com.ludashi.function.watchdog;

public interface WakeBy {
    String UI = "main";
    String SYNC = "sync";
    String NATIVE = "native";
    String JOB_PERIODIC = "job_periodic";
    String JOB_CHARGING = "job_charging";
    String JOB_IDLE = "job_idle";
    String JOB_NETWORK = "job_network";
    String JOB_MEDIA = "job_media";
    String NOTIFICATION = "notification";
    String SERVICE_RESTART = "restart";
    String PUSH_UMENG = "push_umeng";
    String PUSH = "push";
    String TILE_BOOST = "tile_boost";
    String TILE_COOLING = "tile_cooling";
    String ALIVE_INSTRUMENTATION = "power_instrumentation";
    String ALIVE_RECEIVER = "power_export_receiver";
    String ALIVE_SERVICE = "power_export_service";
    String WALLPAPER = "wallpaper";
    String PULL_SELFAPP = "selfapp";
}
