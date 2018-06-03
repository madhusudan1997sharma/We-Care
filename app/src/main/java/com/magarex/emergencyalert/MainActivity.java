package com.magarex.emergencyalert;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements LocationListener {

    LocationManager locationManager;
    Location location = null;
    String lat, lon, number1, number2, number3, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            FileInputStream fin;
            int c = 0;
            String tempnumber = "";

            fin = openFileInput("permissions");
            while ((c = fin.read()) != -1) {
                tempnumber = tempnumber + Character.toString((char) c);
            }
        } catch (IOException e) { RuntimePermissionshandler(); }

        Button add = (Button) findViewById(R.id.add);
        Button panic = (Button) findViewById(R.id.panic);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddContacts.class);
                startActivity(i);
            }
        });

        panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleHotspot(true);
                GettingLocation();
                SendingMessage();
            }
        });
    }

    public void GettingLocation()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Exception e) { }
        if (location == null) {
            try {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) { }
        }
        if (location == null) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } catch (Exception e) { }
        }
        if (location != null) {
            lon = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
            lat = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
        } else {
            lat = "N/A";
            lon = "N/A";
        }
    }

    private void SendingMessage()
    {
        message = "I am in Danger. Location, Latitude: " + lat + ", Longitude: " + lon;
        try {
            FileInputStream fin;
            int c = 0;
            fin = openFileInput("number1");
            number1 = "";
            while ((c = fin.read()) != -1) {
                number1 = number1 + Character.toString((char) c);
            }
            c = 0;
            fin = openFileInput("number2");
            number2 = "";
            while ((c = fin.read()) != -1) {
                number2 = number2 + Character.toString((char) c);
            }
            c = 0;
            fin = openFileInput("number3");
            number3 = "";
            while ((c = fin.read()) != -1) {
                number3 = number3 + Character.toString((char) c);
            }
        } catch (IOException e) { }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            try {
                smsManager.sendTextMessage("tel:" + number1, null, message, null, null);
            } catch (Exception e) { }
            try {
                smsManager.sendTextMessage("tel:" + number2, null, message, null, null);
            } catch (Exception e) { }
            try {
                smsManager.sendTextMessage("tel:" + number3, null, message, null, null);
            } catch (Exception e) { }
            Toast.makeText(MainActivity.this, "Messages Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }
    }

    public boolean ToggleHotspot(boolean ON_OFF)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!Settings.System.canWrite(getApplicationContext()))
            {
                Intent writeSettingIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);

                // Works when calling directly from MainActivity.java,but not from Android Library.WHY??
                // writeSettingIntent.setData(Uri.parse("package: " + mContext.getPackageName()));
                writeSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(writeSettingIntent);
            }
        }
        //TODO -> Check if hotspot enabled.If not start it...
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try{
            Method invokeMethod = wifiMgr.getClass().getMethod("setWifiApEnabled",WifiConfiguration.class,boolean.class);
            return (Boolean)invokeMethod.invoke(wifiMgr,initHotspotConfig(),ON_OFF);
        }
        catch(Throwable ignoreException)
        {
            Toast.makeText(getApplicationContext(), "Failed to Create Hotspot", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private WifiConfiguration initHotspotConfig(){

        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = "Save Me! Rape Emergency!";
        // must be 8 length
        wifiConfig.preSharedKey = "abcd12345";

        wifiConfig.hiddenSSID = true;


        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);


        return wifiConfig;
    }

    private void RuntimePermissionshandler()
    {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Access Fine Location");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Access Coarse Location");
        if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("Send SMS");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("Access Internet");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read/Write Storage");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_SETTINGS))
            permissionsNeeded.add("Toggle Hotspot");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant some permission(s) in order to use this application";
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 124);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 124);
            return;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 124:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_SETTINGS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permissions Denied
                    Toast.makeText(MainActivity.this, "Some Permission(s) are Denied. Grant them on next screen", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);

                    try { FileOutputStream fout;
                    fout = openFileOutput("permissions", Context.MODE_PRIVATE);
                    fout.write("ok".getBytes()); } catch (Exception e) { }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if(android.os.Build.VERSION.SDK_INT > 23) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location loc) {
        location = loc;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle b){}

    @Override
    public void onProviderEnabled(String s){}

    @Override
    public void onProviderDisabled(String s){}
}
