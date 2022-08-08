package com.izam.android.printservice;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.print.PrintHelper;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.util.ArrayList;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class FlutterMainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.izam.dev/print_service";

    private BluetoothConnection[] devices = null;
    private ArrayList<String> devicesInfo = null;
    private BluetoothConnection chosenDevice = null;


    public void initializePrintService()
    {
        PDFBoxResourceLoader.init(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            rootView.findViewById(R.id.print_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PrintHelper printHelper = new PrintHelper(getActivity());
                    printHelper.setColorMode(PrintHelper.COLOR_MODE_COLOR);
                    printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    printHelper.printBitmap("job-" + SystemClock.uptimeMillis(), Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888));
                }
            });

            return rootView;
        }
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if(call.method.equals("getBluetoothPrinters")) {
                                initializePrintService();
                                devicesInfo = getBluetoothPrintersInfo();
                                if(devicesInfo != null)
                                {
                                    result.success(devicesInfo.toString());
                                }
                                else {
                                    result.error("UNAVAILABLE", "Bluetooth printers are not avaialble.", null);
                                }
                            }
                            else
                            {
                                result.notImplemented();
                            }
                        }
                );
    }

    @SuppressLint("MissingPermission")
    private ArrayList<String> getBluetoothPrintersInfo() {
        if(devicesInfo != null) {
            devicesInfo.clear();
        }
        else {
            devicesInfo = new ArrayList<String>();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            devices = IzamPrintService.getConnectedDevicesList();
            if(devices != null && devices.length > 0)
            {
                for(BluetoothConnection device : devices)
                {
                    devicesInfo.add(device.getDevice().getName() + "|" + device.getDevice().getAddress());
                }
                return devicesInfo;
            }
        }
        return null;
    }

    private BluetoothConnection setChosenBluetoothPrinter(String macAddress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return chosenDevice = IzamPrintService.setChosenDevices(macAddress);
        }
        return null;
    }
}
