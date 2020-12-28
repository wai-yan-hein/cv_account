/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.common;

import java.io.IOException;
import java.net.InetAddress;
import java.util.GregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class NetworkDetector extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkDetector.class);
    private NetworkObserver networkObserver;
    private boolean error = false;
    private final int timeOut = 10000;

    public NetworkObserver getNetworkObserver() {
        return networkObserver;
    }

    public void setNetworkObserver(NetworkObserver networkObserver) {
        this.networkObserver = networkObserver;
    }

    @Override
    public void run() {
        try {
            //LOGGER.info("Network Detector Started.");
            String ipAddress = "graph.facebook.com";
            InetAddress inet = InetAddress.getByName(ipAddress);
            long start = new GregorianCalendar().getTimeInMillis();
            if (inet.isReachable(timeOut)) {
                //LOGGER.info("Network Reached.");
                long finish = new GregorianCalendar().getTimeInMillis();
                long time = finish - start;
                if (networkObserver != null) {
                    //LOGGER.info("Network Online.");
                    networkObserver.sendPingTime(time);
                }
            } else {
                //LOGGER.info("Network UnReached.");
                if (networkObserver != null) {
                    networkObserver.sendPingTime(-1);
                }
                //System.out.println(ipAddress + " NOT reachable.");
            }
            error = false;
        } catch (IOException e) {
            //LOGGER.error("Network Error :" + e.getMessage());
            if (!error) {
                //LOGGER.info("Network  Offline");
                networkObserver.sendPingTime(-1);
                error = true;
            }
            //System.out.println("Exception:" + e.getMessage());
        }

    }

}
