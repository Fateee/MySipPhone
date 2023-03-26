package O00000Oo.O000000o.O000000o.O000000o;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

/**
 * 保活涉及到的信息
 */
public class ALiveInfo implements Parcelable {
    public static final Parcelable.Creator CREATOR;

    static {
        CREATOR = new Creator();
    }

    /**
     * 需要监听的另外两个进程的文件，
     * 比如clean进程 那么监听另外两个进程 assist、other进程的文件
     * 即：PowerAssistService_native_clean,PowerOtherService_native_clean
     */
    public String[] twoOtherFiles;
    public String currentProcessName;
    public Intent intentPowerExportService;
    public Intent intentActionUpdateReceiver;
    public Intent intentPowerInstrumentation;

    public ALiveInfo() {
    }

    public ALiveInfo(Parcel parcel) {
        this.twoOtherFiles = parcel.createStringArray();
        this.currentProcessName = parcel.readString();
        if (parcel.readInt() != 0) {
            this.intentPowerExportService = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }

        if (parcel.readInt() != 0) {
            this.intentActionUpdateReceiver = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }

        if (parcel.readInt() != 0) {
            this.intentPowerInstrumentation = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }
    }

    public static ALiveInfo decode(String arg3) {
        Parcel v0 = Parcel.obtain();
        byte[] v3 = Base64.decode(arg3, 2);
        v0.unmarshall(v3, 0, v3.length);
        v0.setDataPosition(0);
        ALiveInfo v3_1 = (ALiveInfo) ALiveInfo.CREATOR.createFromParcel(v0);
        v0.recycle();
        return v3_1;
    }

    @Override  // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        Parcel v0 = Parcel.obtain();
        v0.writeStringArray(this.twoOtherFiles);
        v0.writeString(this.currentProcessName);
        if (this.intentPowerExportService == null) {
            v0.writeInt(0);
        } else {
            v0.writeInt(1);
            this.intentPowerExportService.writeToParcel(v0, 0);
        }

        if (this.intentActionUpdateReceiver == null) {
            v0.writeInt(0);
        } else {
            v0.writeInt(1);
            this.intentActionUpdateReceiver.writeToParcel(v0, 0);
        }

        if (this.intentPowerInstrumentation == null) {
            v0.writeInt(0);
        } else {
            v0.writeInt(1);
            this.intentPowerInstrumentation.writeToParcel(v0, 0);
        }

        String v1 = Base64.encodeToString(v0.marshall(), 2);
        v0.recycle();
        return v1;
    }

    @Override  // android.os.Parcelable
    public void writeToParcel(Parcel arg3, int arg4) {
        arg3.writeStringArray(this.twoOtherFiles);
        arg3.writeString(this.currentProcessName);
        if (this.intentPowerExportService == null) {
            arg3.writeInt(0);
        } else {
            arg3.writeInt(1);
            this.intentPowerExportService.writeToParcel(arg3, 0);
        }

        if (this.intentActionUpdateReceiver == null) {
            arg3.writeInt(0);
        } else {
            arg3.writeInt(1);
            this.intentActionUpdateReceiver.writeToParcel(arg3, 0);
        }

        if (this.intentPowerInstrumentation == null) {
            arg3.writeInt(0);
            return;
        }

        arg3.writeInt(1);
        this.intentPowerInstrumentation.writeToParcel(arg3, 0);
    }

    public static class Creator implements Parcelable.Creator {
        @Override  // android.os.Parcelable$Creator
        public Object createFromParcel(Parcel arg2) {
            return new ALiveInfo(arg2);
        }

        @Override  // android.os.Parcelable$Creator
        public Object[] newArray(int arg1) {
            return new ALiveInfo[arg1];
        }
    }
}

