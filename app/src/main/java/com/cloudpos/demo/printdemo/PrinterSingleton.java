package com.cloudpos.demo.printdemo;

import android.content.Context;
import android.graphics.Bitmap;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.printer.PrinterHtmlListener;

import java.io.InputStream;

public class PrinterSingleton {

    private PrinterDevice device;
    private static volatile PrinterSingleton instance;

    private PrinterSingleton(Context mContext) {
        if (device == null) {
            device = (PrinterDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.printer");
            try {
                device.open();
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static PrinterSingleton getInstance(Context mContext) {
        if (instance == null) {
            synchronized (PrinterSingleton.class) {
                if (instance == null) {
                    instance = new PrinterSingleton(mContext);
                }
            }
        }
        return instance;
    }

    public synchronized void printPDFAsync(InputStream inputStream) {
        try {
            device.printPDF(inputStream);
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void printPDFInPagesAsync(InputStream inputStream, int pageNumbers) {
        try {
            device.printPDF(inputStream, pageNumbers);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printPDFWithBrightness(InputStream inputStream, int pageNumbers, int brightness) {
        try {
            device.printPDFWithBrightness(inputStream, pageNumbers, brightness);
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

}


