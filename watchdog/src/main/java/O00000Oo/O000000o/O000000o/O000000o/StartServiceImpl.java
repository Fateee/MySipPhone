package O00000Oo.O000000o.O000000o.O000000o;

import android.content.Context;

import androidx.core.app.PowerAssistService;
import androidx.core.app.PowerCleanService;
import androidx.core.app.PowerOtherService;

import okhttp3.internal.platform.PowerGem;

/**
 * 具体的启动逻辑，不同进程启动不同的service
 */
public class StartServiceImpl implements PowerGemEntry.IStartService {
    public StartServiceImpl(PowerGem arg1) {
    }

    public boolean startOneService(Context context, String arg4) {
        Class clazz;
        try {
            PowerGemEntry entry = PowerGem.instance.getPowerGemEntry();
            if (arg4.equals(entry.processNameDaemon)) {
                clazz = PowerCleanService.class;
            } else if (arg4.equals(entry.processNameAssist)) {
                clazz = PowerAssistService.class;
            } else if (!arg4.equals(entry.processNameOther)) {
                clazz = null;
            } else {
                clazz = PowerOtherService.class;
            }

            PowerStatHelper.startAndBindService(context, clazz);
            return true;
        } catch (Exception unused_ex) {
            return false;
        }
    }
}

