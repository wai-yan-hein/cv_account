/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.messaging;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.ReloadData;
import com.cv.accountswing.gson.DateDeSerializer;
import com.cv.accountswing.gson.DateSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Date;
import java.util.UUID;
import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
//@Component
public class CareServerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CareServerListener.class);
    private ReloadData rlData;
    private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Date.class, new DateSerializer()).
            registerTypeAdapter(Date.class, new DateDeSerializer()).create();

    public ReloadData getRlData() {
        return rlData;
    }

    public void setRlData(ReloadData rlData) {
        this.rlData = rlData;
    }

    @JmsListener(destination = "${destination.queue}")
    public void receiveMapMessage(final MapMessage message) {

        try {
            
        } catch (Exception ex) {
            LOGGER.error("receiveMapMessage : " + ex.getMessage());
        }
    }

    @JmsListener(destination = "${destination.queue}")
    public void receiveMapMessage1(final MapMessage message) {

        try {
            
        } catch (Exception ex) {
            LOGGER.error("receiveMapMessage : " + ex.getMessage());
        }
    }

    @JmsListener(destination = "${destination.queue}file")
    public void receiveFileMessage(final BytesMessage message) {

        try {
            
        } catch (Exception ex) {
            LOGGER.error("receiveMapMessage : " + ex.getMessage());
        }
    }

}
