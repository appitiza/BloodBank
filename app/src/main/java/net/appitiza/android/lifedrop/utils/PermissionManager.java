package net.appitiza.android.lifedrop.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import net.appitiza.android.lifedrop.R;

/**
 * Zco Engineering Dept.
 */
public class PermissionManager {
    public static final int ALL_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE = 2;
    public static final int EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE = 3;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 5;
    public static final int SMS_PERMISSION_REQUEST_CODE = 5;
    public static final int CONTACT_READ_PERMISSION_REQUEST_CODE = 6;
    public static final int CALL_PERMISSION_REQUEST_CODE = 7;
    private static final String TAG = "Permission";

    public static boolean checkIsGreaterThanM() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean checkPermissionForContacts(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean checkPermissionForCamera(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean checkPermissionForWriteExternalStorage(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermissionForReadExternalStorage(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean checkPermissionForLocation(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean checkPermissionForReceiveSMS(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean checkPermissionForCall(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    /**
     * Request Permission
     */
    public static void requestPermissionForContact(Activity activity) {
        activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                CONTACT_READ_PERMISSION_REQUEST_CODE);
    }
    public static void requestPermissionForWriteExternalStorage(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE);
    }

    public static void requestPermissionForReadExternalStorage(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE);
    }
    public static void requestPermissionForLocation(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }
    public static void requestPermissionForReceiveSMS(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_REQUEST_CODE);
    }
    public static void requestPermissionForAll(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS}, ALL_PERMISSION_REQUEST_CODE);

    }
    /**
     * Is permission rationale
     * Used to Check If the permission is not in the application and user decline the popup previously.
     */

    public boolean isExternalStorageWritePermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean isExternalStorageReadPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    public static boolean isLocationPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

}
