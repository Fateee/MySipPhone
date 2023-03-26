package com.ludashi.function.watchdog.foundation.pref;

/**
 * 使用SharedPref时，未指定单独的SharedPreference文件名的情况下，需要在此处申明Key，以方便查询是否重复
 */
public interface PrefKeys {

    String AUTO_START_PERMISSION = "auto_start_permission";

    String HIGH_POWER_CONSUMPTION = "high_power_consumption";

    interface PlanConfigKeys {
        String FILE = "plan_config_file";
        /**
         * permission guide配置数据
         */
        String PLAN_ID = "plan_id";

        /**
         * 是否需要获取计划标识
         */
        String LOAD_PLAN_FLAG = "load_plan_flag";
    }

    interface DaemonConfigKeys {
        String FILE = "daemon_config_file";

        /**
         * 配置刷新时间
         */
        String REFRESH_TIME = "refresh_time";

        /**
         * native进程守护
         */
        String NATIVE_DAEMON = "native_daemon";

        /**
         * 后台播放无声音乐
         */
        String SILENCE_MUSIC = "silence_music";

        /**
         * 1像素activity
         */
        String ONE_PIXEL = "one_pixel";

        /**
         * 设置壁纸
         */
        String SET_WALLPAPER = "set_wallpaper";

        /**
         * 增强监控锁屏状态
         */
        String STRENGTHEN_MONITORING_LOCK_SCREEN = "strengthen_monitoring_lock_screen";
        /**
         * 后台任务
         */
        String JOB_SCHEDULE = "job_schedule";
        /**
         * 多进程守护
         */
        String DUAL_PROCESS_DAEMON = "dual_process_daemon";
        /**
         * 账户同步
         */
        String ACCOUNT_SYNC = "account_sync";
    }

    interface PermissionGuideKeys {
        String FILE = "permission_guide_file";
        /**
         * 第一次点击Back键，是否展示引导
         */
        String FIRST_BACK = "firstBack";
        /**
         * 主页面右上角的警示图标
         */
        String MAIN_ICON = "mainIcon";
        /**
         * 点击手机加速入口
         */
        String BOOST_ENTRY = "boostEntry";
        /**
         * 点击手机降温入口
         */
        String COOL_ENTRY = "coolEntry";

        String BATTERY_PERMISSION_OPEN = "battery_permission_open";
    }
}
