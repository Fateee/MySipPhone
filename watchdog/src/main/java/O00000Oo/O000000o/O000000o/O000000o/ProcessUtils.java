package O00000Oo.O000000o.O000000o.O000000o;

import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

/**
 * 启动进程，拼接本地监听文件的名称
 * c
 */
public class ProcessUtils {
    public static String fileName(Class arg1, String arg2) {
        return arg1.getSimpleName() + arg2;
    }

    public static String[] fileName(Class[] arg4, String[] arg5) {
        String[] fileNames = new String[arg5.length];
        int i;
        for (i = 0; i < arg5.length; ++i) {
            fileNames[i] = ProcessUtils.fileName(arg4[i], arg5[i]);
        }

        return fileNames;
    }

    /**
     * 通过sh 命令行工具，向系统添加环境变量CLASSPATH、_LD_LIBRARY_PATH、LD_LIBRARY_PATH，并且执行app_process64 可执行文件，
     * 并且执行app_process64，对应的是64位手机的命令，是在/system/bin/app_process64 下，是一个可执行文件，应该是启动应用
     * 添加的环境变量，估计C中会用到
     * export CLASSPATH=$CLASSPATH:/data/app/com.ludashi.alive-hW6xgJJh9JyeeUX1FUpwJw==/base.apk
     * export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:/data/app/com.ludashi.alive-hW6xgJJh9JyeeUX1FUpwJw==/lib/arm64
     * export LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:/data/app/com.ludashi.alive-hW6xgJJh9JyeeUX1FUpwJw==/lib/arm64
     * app_process64 / okhttp3.internal.platform.inner.PowerGemEntrance AgAAAEkAAAAvAGQAYQB0AGEALwB1AHMAZQByAC8AMAAvAGMAbwBtAC4AbAB1AGQAYQBzAGgAaQAuAGEAbABpAHYAZQAvAGEAcABwAF8AVABtAHAARABpAHIALwBQAG8AdwBlAHIAQQBzAHMAaQBzAHQAUwBlAHIAdgBpAGMAZQBfAG4AYQB0AGkAdgBlAF8AYwBsAGUAYQBuAAAASAAAAC8AZABhAHQAYQAvAHUAcwBlAHIALwAwAC8AYwBvAG0ALgBsAHUAZABhAHMAaABpAC4AYQBsAGkAdgBlAC8AYQBwAHAAXwBUAG0AcABEAGkAcgAvAFAAbwB3AGUAcgBPAHQAaABlAHIAUwBlAHIAdgBpAGMAZQBfAG4AYQB0AGkAdgBlAF8AYwBsAGUAYQBuAAAAAAAFAAAAYwBsAGUAYQBuAAAAAQAAAP////8AAAAA//////////8AAAAAAAAAAAAAAAD/////EQAAAGMAbwBtAC4AbAB1AGQAYQBzAGgAaQAuAGEAbABpAHYAZQAAACQAAABhAG4AZAByAG8AaQBkAHgALgBjAG8AcgBlAC4AYQBwAHAALgBQAG8AdwBlAHIARQB4AHAAbwByAHQAUwBlAHIAdgBpAGMAZQAAAAAAAAAAAAAAAAAAAAAAAAAAAP7///8AAAAAAQAAACEAAABhAG4AZAByAG8AaQBkAHgALgBjAG8AcgBlAC4AYQBwAHAALgBVAFAARABBAFQARQBfAFIARQBDAEUASQBWAEUAUgAAAAAAAAD//////////wAAAAAAAAAAAAAAABEAAABjAG8AbQAuAGwAdQBkAGEAcwBoAGkALgBhAGwAaQB2AGUAAAD/////AAAAAAAAAAAAAAAAAAAAAP7///8AAAAAAQAAAP////8AAAAA//////////8AAAAAAAAAAAAAAAD/////EQAAAGMAbwBtAC4AbAB1AGQAYQBzAGgAaQAuAGEAbABpAHYAZQAAADQAAABvAGsAaAB0AHQAcAAzAC4AaQBuAHQAZQByAG4AYQBsAC4AcABsAGEAdABmAG8AcgBtAC4AaQBuAG4AZQByAC4AUABvAHcAZQByAEkAbgBzAHQAcgB1AG0AZQBuAHQAYQB0AGkAbwBuAAAAAAAAAAAAAAAAAAAAAAAAAAAA/v///wAAAAA= --application --nice-name=clean --daemon &
     * <p>
     * 通过ProcessBuilder 实现Android程序，执行其他程序并返回数据
     *
     * @param workingDirectory 工作目录
     * @param args             参数
     * @param shLines          参数
     * @return 若执行失败或执行完成 数据
     */
    public static String startProcess(File workingDirectory, Map args, String[] shLines) {
        LogUtils.log("startProcess() p1:" + (null == workingDirectory ? "NULL FILE" : workingDirectory.getAbsolutePath()));
        LogUtils.log("startProcess() p2:" + args);
        StringBuilder p3 = new StringBuilder();
        for (String arg : shLines) {
            p3.append(arg).append("\n");
        }
        LogUtils.log("startProcess() p3:" + p3);
        String v1 = System.getenv("PATH");
        LogUtils.log("startProcess() Path:" + v1);
        int v2 = 0;
        String exitMsg = null;
        if (TextUtils.isEmpty(v1)) {
            v1 = null;
        } else {
            String[] v4 = v1.split(":");
            int i;
            for (i = 0; i < v4.length; ++i) {
                File v6 = new File(v4[i], "sh");
                if (v6.exists()) {
                    v1 = v6.getPath();
                    break;
                }
            }
        }
        LogUtils.log("startProcess() final use Path:" + v1);

        if (v1 == null) {
            StringBuilder v9 = new StringBuilder();
            v9.append("The devices(" + Build.MODEL + ") has not shell ");
            v9.append(Build.MODEL);
            v9.append(") has not shell ");
            v9.append(v1);
            return null;
        }

        OutputStream outputStream = null;
        Process process;
        BufferedReader bufferedReader = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(new String[0]).command(new String[]{v1}).redirectErrorStream(true);
            if (workingDirectory != null) {
                processBuilder.directory(workingDirectory);
            }

            processBuilder.environment().putAll(System.getenv());
            if (args != null && args.size() > 0) {
                processBuilder.environment().putAll(args);
            }

            process = processBuilder.start();
            outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            while (v2 < shLines.length) {
                String arg = shLines[v2];
                if (!arg.endsWith("\n")) {
                    arg = arg + "\n";
                }

                outputStream.write(arg.getBytes());
                outputStream.flush();
                ++v2;
            }

            outputStream.write("exit 156\n".getBytes());
            outputStream.flush();
            // 等待执行完成
            process.waitFor();
            // 获取获取数据
            exitMsg = ProcessUtils.read(bufferedReader);
            LogUtils.log("startProcess() execute done and sh response:" + exitMsg);
        } catch (Throwable v9_1) {
            v9_1.printStackTrace();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException unused_ex) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException unused_ex) {
                }
            }
        }

        return exitMsg;
    }

    public static String read(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        return stringBuilder.length() > 0 ? stringBuilder.toString() : null;
    }
}

