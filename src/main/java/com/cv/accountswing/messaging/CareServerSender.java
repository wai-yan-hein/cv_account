/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.messaging;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.gson.DateDeSerializer;
import com.cv.accountswing.gson.DateSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import javax.jms.MapMessage;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
//@Component
//@PropertySource(value = {"classpath:application.properties"})
public class CareServerSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(CareServerSender.class);
    private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Date.class, new DateSerializer()).
            registerTypeAdapter(Date.class, new DateDeSerializer()).create();
    @Value("${server.queue}")
    private String topicStringDestination;
    @Value("${destination.queue}")

    private String replyQueue;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    private Environment environment;

    public void sendMapMessage(MessageCreator mc) {
        jmsTemplate.send(topicStringDestination, mc);

    }

    public void sendNewInitializeMessage() {
        MessageCreator mc = (Session session) -> {
            MapMessage message = session.createMapMessage();

            message.setString("msgOwner", Global.uuid);
            message.setString("operationId", "NEW-INIT");
            message.setString("replyTo", replyQueue);
            return message;
        };

        sendMapMessage(mc);
    }

    public void sendNewPropertyInitMessage() {
        MessageCreator mc = (Session session) -> {
            MapMessage message = session.createMapMessage();

            message.setString("msgOwner", Global.uuid);
            message.setString("operationId", "NEW-SYSPROP");
            message.setString("replyTo", replyQueue);
            return message;
        };

        sendMapMessage(mc);
    }
}
