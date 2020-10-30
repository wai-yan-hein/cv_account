/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.common;

import java.io.IOException;
import java.net.InetAddress;
import java.util.GregorianCalendar;

/**
 *
 * @author Lenovo
 */
public class NetworkDetector extends Thread {

    private final long sleepTime = 5000;
    private NetworkObserver networkObserver;
    private boolean error = false;

    public NetworkObserver getNetworkObserver() {
        return networkObserver;
    }

    public void setNetworkObserver(NetworkObserver networkObserver) {
        this.networkObserver = networkObserver;
    }

    @Override
    public void run() {
        try {
            String ipAddress = "www.google.com";
            InetAddress inet = InetAddress.getByName(ipAddress);

            long start = new GregorianCalendar().getTimeInMillis();

            if (inet.isReachable(5000)) {
                long finish = new GregorianCalendar().getTimeInMillis();
                long time = finish - start;
                if (networkObserver != null) {
                    networkObserver.sendPingTime(time);
                }
            } else {
                if (networkObserver != null) {
                    networkObserver.sendPingTime(-1);
                }
                //System.out.println(ipAddress + " NOT reachable.");
            }
            error = false;
            Thread.sleep(sleepTime);
        } catch (IOException | InterruptedException e) {
            if (!error) {
                networkObserver.sendPingTime(-1);
                error = true;
            }
            //System.out.println("Exception:" + e.getMessage());
        }

    }

}
