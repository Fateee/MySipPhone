package com.ludashi.framework.info;

import com.ludashi.framework.utils.ProcessUtil;

/**
 * 用以全局存储当前APP的各种信息
 *
 * @author billy
 */
public class ThisApp {

    private final int mVersionCode;
    private final String mVersionName;
    private final String mPkgName;
    private final String mAppName;
    private final int launcherIcon;
    /**
     * 当前进程名称
     * 避免频繁读取文件
     */
    private final String processName;
    private String mChannel;
    /**
     * 对于头条的买量包来说，存在两个channel，
     * 一个是原始渠道，也就是自身打包的渠道
     * 另一个是头条分包sdk生成的渠道
     */
    private String mOriginalChannel;

    ThisApp(int versionCode, String versionName, String pkgName, String channel, String appName, int launcher) {
        mVersionCode = versionCode;
        mVersionName = versionName;
        mPkgName = pkgName;
        mAppName = appName;
        processName = ProcessUtil.getCurrentProcessName();
        launcherIcon = launcher;
        mChannel = channel;
    }

    public int versionCode() {
        return mVersionCode;
    }

    public String versionName() {
        return mVersionName;
    }

    public String pkgName() {
        return mPkgName;
    }

    public String channel() {
        return mChannel;
    }

    /**
     * 除渠道归因外  其他地方禁止调用
     */
    public void setChannel(String channel) {
        mChannel = channel;
    }

    public String appName() {
        return mAppName;
    }

    /**
     * 判断当前是否时买量渠道
     */
    public boolean isOceanEngineChannel() {
        return "ch000".equals(mOriginalChannel);
    }

    /**
     * 判断当前是否是快手的渠道
     */
    public boolean isKsChannel() {
        return mChannel.matches("ks\\d{3}");
    }

    /**
     * 判断当前是否是广点通买量的渠道
     */
    public boolean isGdtChannel() {
        return mChannel.matches("gdt\\d{3}");
    }

    public int getLauncherIcon() {
        return launcherIcon;
    }

    public String getProcessName() {
        return processName;
    }
}
