package O00000Oo.O000000o.O000000o.O000000o;

import android.content.Context;
import android.content.Intent;

import O000000o.O000000o.O000000o.BroadcastReceiverServiceStartPower;

public class PowerGemEntry {
    public String packageName;
    public String processNameDaemon;
    public String processNameAssist;
    public String processNameOther;
    public Intent intentPowerExportService;
    public Intent intentPowerInstrumentation;
    public Intent intentActionUpdateReceiver;
    public String appTmpDir;
    public String nativeLibraryDir;
    public String publicSourceDir;
    public IStartService interfaze;
    public PowerGemEntry(PowerGemEntryBuilder arg1, BroadcastReceiverServiceStartPower arg2) {
        this.packageName = arg1.packageName;
        this.processNameDaemon = arg1.processNameDaemon;
        this.processNameAssist = arg1.processNameAssist;
        this.processNameOther = arg1.processNameOther;
        this.intentActionUpdateReceiver = arg1.intentActionUpdateReceiver;
        this.intentPowerInstrumentation = arg1.intentPowerInstrumentation;
        this.intentPowerExportService = arg1.intentPowerExportService;
        this.appTmpDir = arg1.appTmpDir;
        this.nativeLibraryDir = arg1.nativeLibraryDir;
        this.publicSourceDir = arg1.publicSourceDir;
        this.interfaze = arg1.interfaze;
    }
    public interface IStartService {
    }

    public static final class PowerGemEntryBuilder {
        public String packageName;
        public String processNameDaemon;
        public String processNameAssist;
        public String processNameOther;
        public Intent intentPowerExportService;
        public Intent intentPowerInstrumentation;
        public Intent intentActionUpdateReceiver;
        public String appTmpDir;
        public String nativeLibraryDir;
        public String publicSourceDir;
        public IStartService interfaze;
        public Context context;

        public PowerGemEntryBuilder(Context arg1) {
            this.context = arg1;
        }
    }
}

