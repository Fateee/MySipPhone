package okhttp3.internal.platform;

public class NetUtil {
    static {
        try {
            System.loadLibrary("nets");
        } catch (Throwable ignored) {
        }
    }

    public static native int disconnect();

    /**
     * 检查一下当前文件，会立即返回
     *
     * @param path 路径
     * @return 1 成功
     */
    public static native int ping(String path);

    /**
     * 阻塞当前线程
     *
     * @param path path
     * @return
     */
    public static native int watch(String path);
}

