package com.ludashi.function.watchdog.keepalive;

public interface PermissionCallback {

    void hasPermission();

    void lossPermission();

    void dontAskAgain();
}