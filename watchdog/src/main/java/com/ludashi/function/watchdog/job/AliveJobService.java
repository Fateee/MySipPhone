package com.ludashi.function.watchdog.job;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;


/**
 * @author hly
 * @date 7/19/16
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AliveJobService extends JobService {
    public static final int PERIODIC_JOB_ID = 0x01;
    public static final int CHARGING_JOB_ID = 0x02;
    public static final int IDLE_JOB_ID = 0x03;
    public static final int UNMETERED_NETWORK_JOB_ID = 0x04;
    public static final int MEDIA_JOB_ID = 0x05;
    private static final String TAG = "ReliveJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        LogUtil.i(TAG, "alive onStartJob, job id is : " + jobParameters.getJobId());
        WatchDog.trySetWakeBy(constructSource(jobParameters));
        try {
            jobFinished(jobParameters, true);
        } catch (final Throwable throwable) {
            LogUtil.i(TAG, "alive onStartJob, finish problem: " + throwable.getMessage());
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private String constructSource(JobParameters parameters) {
        String src = null;
        switch (parameters.getJobId()) {
            case PERIODIC_JOB_ID:
                src = WakeBy.JOB_PERIODIC;
                break;
            case CHARGING_JOB_ID:
                src = WakeBy.JOB_CHARGING;
                break;
            case IDLE_JOB_ID:
                src = WakeBy.JOB_IDLE;
                break;
            case UNMETERED_NETWORK_JOB_ID:
                src = WakeBy.JOB_NETWORK;
                break;
            case MEDIA_JOB_ID:
                src = WakeBy.JOB_MEDIA;
                break;
            default:
                break;
        }
        return src;
    }
}
