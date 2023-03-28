package com.ludashi.function.watchdog.job;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;

import com.ludashi.framework.utils.log.LogUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author hly
 * @date 7/26/16
 */
public class JobHelper {

    public static final String TAG = JobHelper.class.getSimpleName();

    /**
     * how frequently the sync should be performed, in millis
     */
    private static final long INTERVAL_FOR_JOB = TimeUnit.MINUTES.toMillis(10);
    private static final long DEADLINE_FOR_JOB = TimeUnit.HOURS.toMillis(12);

    public static void enableOrNot(boolean enable, Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LogUtil.i(TAG, "current build version: " + Build.VERSION.SDK_INT + " not support scheduleJobs");
            return;
        }
        try {
            enableOrDisableService(context, enable);
        } catch (Throwable e) {
            LogUtil.i(TAG, "ReliveServiceHelper enableOrNot enableOrDisplayService error, msg: " + e.getMessage());
        }
        if (enable) {
            try {
                LogUtil.i(TAG, "enable job schedule");
                scheduleJobs(context);
            } catch (Throwable e) {
                LogUtil.i(TAG, "ReliveServiceHelper enableOrNot scheduleJobs error, msg: " + e.getMessage());
            }
        } else {
            try {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancelAll();
            } catch (Throwable e) {
                LogUtil.i(TAG, "ReliveServiceHelper enableOrNot  jobScheduler.cancelAll() error, msg: " + e.getMessage());
            }
        }
    }

    private static void scheduleJobs(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        // 周期唤醒
        schedule(AliveJobService.PERIODIC_JOB_ID, context);
        // 充电唤醒
        schedule(AliveJobService.CHARGING_JOB_ID, context);
        // 设备空闲唤醒
        schedule(AliveJobService.IDLE_JOB_ID, context);
        // 不计费网络连接时唤醒
        schedule(AliveJobService.UNMETERED_NETWORK_JOB_ID, context);
        // 媒体文件发生增删，如拍照等
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            schedule(AliveJobService.MEDIA_JOB_ID, context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void schedule(int jobId, Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            LogUtil.i(TAG, "job scheduler is null");
            return;
        }
        JobInfo.Builder builder = new JobInfo.Builder(
                jobId, new ComponentName(context.getPackageName(), AliveJobService.class.getName())
        );
        //支持设备重启（国内rom大部分都需要获得自启动权限后才生效）
        builder.setPersisted(true);
        switch (jobId) {
            case AliveJobService.PERIODIC_JOB_ID:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setPeriodic(INTERVAL_FOR_JOB, TimeUnit.MINUTES.toMillis(5));
                } else {
                    builder.setPeriodic(INTERVAL_FOR_JOB);
                }
                break;
            case AliveJobService.CHARGING_JOB_ID:
                builder.setMinimumLatency(INTERVAL_FOR_JOB)
                        .setOverrideDeadline(DEADLINE_FOR_JOB)
                        .setRequiresCharging(true);
                break;
            case AliveJobService.IDLE_JOB_ID:
                builder.setMinimumLatency(INTERVAL_FOR_JOB)
                        .setOverrideDeadline(DEADLINE_FOR_JOB)
                        .setRequiresDeviceIdle(true);
                break;
            case AliveJobService.UNMETERED_NETWORK_JOB_ID:
                builder.setMinimumLatency(INTERVAL_FOR_JOB)
                        .setOverrideDeadline(DEADLINE_FOR_JOB)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
                break;
            case AliveJobService.MEDIA_JOB_ID:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setPersisted(false)
                            .addTriggerContentUri(
                                    new JobInfo.TriggerContentUri(
                                            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                                            JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS))
                            .addTriggerContentUri(
                                    new JobInfo.TriggerContentUri(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS))
                            .setTriggerContentMaxDelay(TimeUnit.MINUTES.toMillis(30))
                            .setTriggerContentUpdateDelay(TimeUnit.MINUTES.toMillis(1))
                            .setMinimumLatency(TimeUnit.MINUTES.toMillis(30));
                }
                break;
            default:
                break;
        }

        int result = jobScheduler.schedule(builder.build());
        LogUtil.i(TAG, "alive schedule job " + jobId + ", result = " + (result == JobScheduler.RESULT_SUCCESS));
    }

    private static void enableOrDisableService(Context context, boolean enable) {
        final PackageManager pm = context.getPackageManager();
        final ComponentName compName = new ComponentName(context.getPackageName(), AliveJobService.class.getName());
        if (pm != null) {
            pm.setComponentEnabledSetting(compName,
                    enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
