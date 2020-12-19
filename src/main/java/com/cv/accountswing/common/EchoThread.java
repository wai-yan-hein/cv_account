/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.common;

import com.cv.inv.entry.CustomerGrid;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class EchoThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(EchoThread.class);

    protected Socket socket;
    private CustomerGrid customerGrid;

    public CustomerGrid getCustomerGrid() {
        return customerGrid;
    }

    public void setCustomerGrid(CustomerGrid customerGrid) {
        this.customerGrid = customerGrid;
    }

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream data;
        try {
            data = new DataInputStream(socket.getInputStream());
            String phoneNo = (String) data.readUTF();
            if (!phoneNo.isEmpty()) {
                customerGrid.setPhoneNumber(phoneNo);
                data.close();
            }
        } catch (IOException e) {
            log.error("DataInputStream :" + e.getMessage());
        }
    }
}
