package com.ludashi.framework.utils.sys;

import androidx.annotation.Nullable;

public class DefaultPlatform extends RomPlatformHolder {
    @Override
    public int platformId() {
        return RomPlatform.UNKNOW;
    }

    @Nullable
    @Override
    public String romVersion() {
        return "";
    }
}
