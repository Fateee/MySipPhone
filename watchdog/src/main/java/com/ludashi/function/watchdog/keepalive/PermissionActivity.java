package com.ludashi.function.watchdog.keepalive;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * @author fanzhipeng
 */
public class PermissionActivity extends FragmentActivity {
    private static final int PERMISSION_REQUEST_CODE = 9999;

    private String mPermissionDes;

    private PermissionCallback mPermissionCallback;


    /**
     * 申请权限
     *
     * @param permissionDesId 权限说明StringRes，,在用户拒绝后提示用户的说明
     * @param callback        回调
     * @param permissions     权限
     */
    protected void requestPermissions(@StringRes int permissionDesId, PermissionCallback callback, @NonNull String... permissions) {
        requestPermissions(getString(permissionDesId), callback, permissions);
    }


    /**
     * 申请权限
     *
     * @param permissionDes 权限说明,在用户拒绝后提示用户的说明
     * @param callback      回调
     * @param permissions   权限
     */
    protected void requestPermissions(String permissionDes, PermissionCallback callback, @NonNull String... permissions) {
        mPermissionCallback = callback;
        mPermissionDes = permissionDes;
        if (checkPermissions(permissions)) {
            if (mPermissionCallback != null) {
                mPermissionCallback.hasPermission();
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 检查是否已经授权
     */
    protected boolean checkPermissions(@NonNull String... permissions) {
        boolean flag = true;
        for (String permission : permissions) {
            // 如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
            // 但是，某些手机上用户可以关闭该权限，
            // 关闭后，ActivityCompat.checkSelfPermission()可能会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)
            try {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    flag = false;
                    break;
                }
            } catch (Exception e) {
                flag = false;
            }
        }
        return flag;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        boolean hasAllGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    if (mPermissionCallback != null) {
                        mPermissionCallback.dontAskAgain();
                    }
                } else {
                    if (mPermissionCallback != null) {
                        mPermissionCallback.lossPermission();
                    }
                }
                break;
            }
        }
        if (hasAllGranted) {
            if (mPermissionCallback != null) {
                mPermissionCallback.hasPermission();
            }
        }
    }


    public void openSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        if (intent.resolveActivity(getPackageManager()) == null) {
            return;
        }
        startActivity(intent);
    }
}
