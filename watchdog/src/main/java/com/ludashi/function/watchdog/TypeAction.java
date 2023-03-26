package com.ludashi.function.watchdog;

public interface TypeAction {

    interface Permission{
        String TYPE = "permission";

        String REQUEST_PERMISSION_START = "request_permission_start";

        String ALL_PERMISSION_GRANTED = "all_permission_granted";
    }

    interface Alive {
        String TYPE = "alive";
        String ALIVE_BY_ = "by_";
    }
}
