package com.dantsu.escposprinter.connection.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

import java.util.Objects;

public class BluetoothPrintersConnections extends BluetoothConnections {

    /**
     * Easy way to get the first bluetooth printer paired / connected.
     *
     * @return a EscPosPrinterCommands instance
     */
    @Nullable
    public static BluetoothConnection selectFirstPaired() {
        BluetoothPrintersConnections printers = new BluetoothPrintersConnections();
        BluetoothConnection[] bluetoothPrinters = printers.getList();

        if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
            Log.d("printers count", bluetoothPrinters.length + " bluetooth printers");
            for (BluetoothConnection printer : bluetoothPrinters) {
                try {
                    Log.d("printers count", bluetoothPrinters.length + " bluetooth printers");

                    return printer.connect();
                } catch (EscPosConnectionException e) {
                    Log.d("can't connect to", " bluetooth printer");
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.d("selectFirstPaired", "we still don't have bluetooth printers!");
        }
        return null;
    }

    /**
     * Easy way to connect to specific bluetooth printer paired / connected.
     *
     * @return a EscPosPrinterCommands instance
     */
    @Nullable
    public static BluetoothConnection connectToSpecificPrinter(String macAddress) {
        BluetoothPrintersConnections printers = new BluetoothPrintersConnections();
        BluetoothConnection[] bluetoothPrinters = printers.getList();

        if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
            System.out.println("printers count, " + bluetoothPrinters.length + " bluetooth printers");
            for (BluetoothConnection printer : bluetoothPrinters) {
                try {
                    if(!printer.isConnected() && Objects.equals(printer.getDevice().getAddress(), macAddress)) {
                        Log.d("printers count", bluetoothPrinters.length + " bluetooth printers");
                        return printer.connect();
                    }
                } catch (EscPosConnectionException e) {
                    System.out.println("can't connect to bluetooth printer with address: " + macAddress);
                    e.printStackTrace();
                }
            }
        }
        else
        {
            System.out.println("selectSpecificPrinter -> we don't have bluetooth printers!");
        }
        return null;
    }

    @Nullable
    public static void disconnectOtherPrinters(String connectedPrinterMacAddress) {
        BluetoothPrintersConnections printers = new BluetoothPrintersConnections();
        BluetoothConnection[] bluetoothPrinters = printers.getList();

        if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
            for (BluetoothConnection otherPrinters : bluetoothPrinters)
            {
                if(otherPrinters.isConnected() && !Objects.equals(otherPrinters.getDevice().getAddress(), connectedPrinterMacAddress)) {
                    otherPrinters.disconnect();
                }
            }
        }
        else
        {
            System.out.println("disconnectOtherPrinters -> we don't have bluetooth printers!");
        }
    }

    /**
     * Get a list of bluetooth printers.
     *
     * @return an array of EscPosPrinterCommands
     */
    @SuppressLint("MissingPermission")
    @Nullable
    public BluetoothConnection[] getList() {
        BluetoothConnection[] bluetoothDevicesList = super.getList();

        if (bluetoothDevicesList == null) {
            System.out.println("Bluetooth, we don't have bluetooth printers!");
            return null;
        }
        else
        {
            System.out.println("Bluetooth, we still have bluetooth printers!");
        }

        int i = 0;
        BluetoothConnection[] printersTmp = new BluetoothConnection[bluetoothDevicesList.length];
        for (BluetoothConnection bluetoothConnection : bluetoothDevicesList) {
            BluetoothDevice device = bluetoothConnection.getDevice();

            int majDeviceCl = device.getBluetoothClass().getMajorDeviceClass(),
                    deviceCl = device.getBluetoothClass().getDeviceClass();

            if (majDeviceCl == BluetoothClass.Device.Major.IMAGING && (deviceCl == BluetoothClass.Device.Major.IMAGING || deviceCl == 1664)) {
                printersTmp[i++] = new BluetoothConnection(device);
            }
        }
        System.out.println("getList, printers count: " + bluetoothDevicesList.length);

        System.arraycopy(printersTmp, 0, bluetoothDevicesList, 0, i);
        return bluetoothDevicesList;
    }

}
