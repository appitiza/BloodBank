package net.appitiza.android.lifedrop.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import net.appitiza.android.lifedrop.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;


public class GeneralUtils {

    public static SortedMap<Currency, Locale> currencyLocaleMap;
    public static AlertDialog alert;
    static {
        currencyLocaleMap = new TreeMap<>(new Comparator<Currency>() {
            public int compare(Currency c1, Currency c2) {
                return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
            }
        });
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (Exception e) {
            }
        }

    }

    public static String getCurrencySymbol(Context context, double latitude, double longitude) {
        String countryCode = getCountryCode(context, latitude, longitude);
        if (countryCode != null) {
            try {
                Locale currentlocale = new Locale("", countryCode);
                Currency currency = Currency.getInstance(currentlocale);
                return currency.getSymbol(currencyLocaleMap.get(currency));
            }
            catch (Exception e)
            {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getCurrencySymbol(Context context, String countryCode) {
        if (countryCode != null) {
            Locale currentlocale = new Locale("", countryCode);
            Currency currency = Currency.getInstance(currentlocale);
            return currency.getSymbol(currencyLocaleMap.get(currency));
        } else {
            return null;
        }
    }

    public static String getCurrencyCode(Context context, double latitude, double longitude) {
        String countryCode = getCountryCode(context, latitude, longitude);
        if (countryCode != null) {
            Locale currentlocale = new Locale("", countryCode);
            Currency currency = Currency.getInstance(currentlocale);
            return currency.getCurrencyCode();
        } else {
            return "";
        }
    }

    public static String getCountry(Context context, double latitude, double longitude) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            return null;
        }
        if (addresses.size() > 0) {
            String toastMsg = String.format("Place: %s", addresses.get(0).getLocality() + " - " + addresses.get(0).getCountryName() + " - " + addresses.get(0).getCountryCode());
            return addresses.get(0).getCountryName();

        }
        return null;
    }
    public static String getAddress(Context context, double latitude, double longitude) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            return null;
        }
        if (addresses.size() > 0) {
            return addresses.get(0).getLocality();

        }
        return null;
    }
    public static String getCountryCode(Context context, double latitude, double longitude) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getCountryCode();

            }
            else
            {
                return "";
            }
        } catch (IOException e) {
            return e.getMessage();
        }


    }


    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public static File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 60 , outputStream);

            return file;
        } catch (Exception e) {
            System.out.println(""+e.getMessage());
            Log.e("file compression error",""+e.getMessage());
            return null;
        }
    }

    public static boolean checkAndRequestLocationPermissions(Activity activity, int requestCode) {

        int location = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), requestCode);
            return false;
        }
        return true;
    }
    public static boolean checkPermission(Context context, String permission) {
        boolean result = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        return result;
    }
    public void reDirectToPermissionScreen(final Activity activity, String message, String positiveButtonText) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.app_name));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });

        if (alert != null) {
            alert.dismiss();
        }
        alert = builder.create();
        alert.show();
    }
}
