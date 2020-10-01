/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.configuration;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.ReloadData;
import java.io.IOException;
import org.apache.activemq.transport.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author winswe
 */
public class MQTransportListener implements TransportListener {

    private static final Logger logger = LoggerFactory.getLogger(MQTransportListener.class);
    private ReloadData rld;

    @Override
    public void onCommand(Object obj) {
        if (rld != null) {
            rld.reload("ConnectionStatus", "Online");
        }
    }

    @Override
    public void onException(IOException ex) {
        logger.error("onException : " + ex.getMessage());
        if (rld != null) {
            rld.reload("ConnectionStatus", "Offline");
        }
    }

    @Override
    public void transportInterupted() {
        logger.error("transportInterupted");
        Global.mqConStatus = false;
        if (rld != null) {
            rld.reload("ConnectionStatus", "Offline");
        }
    }

    @Override
    public void transportResumed() {
        logger.error("transportResumed");
        Global.mqConStatus = true;
        if (rld != null) {
            rld.reload("ConnectionStatus", "Online");
        }
    }

    public ReloadData getRld() {
        return rld;
    }

    public void setRld(ReloadData rld) {
        this.rld = rld;
    }
}
