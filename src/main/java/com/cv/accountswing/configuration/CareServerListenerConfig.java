/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.configuration;

import com.cv.accountswing.common.Global;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

/**
 *
 * @author winswe
 */
//@Configuration
//@EnableJms
public class CareServerListenerConfig {
    @Value("${activemq.broker-url}")
    private String brokerUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(CareServerListenerConfig.class);
    
    @Bean
    public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory
                = new ActiveMQConnectionFactory();
        //activeMQConnectionFactory.setUserName(brokerUrl);
        //activeMQConnectionFactory.setPassword(brokerUrl);
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        Global.mqListener = new MQTransportListener();
        activeMQConnectionFactory.setTransportListener(Global.mqListener);
        
        LOGGER.info("receiverActiveMQConnectionFactory : " + brokerUrl);
        return activeMQConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(receiverActiveMQConnectionFactory());
        //factory.setPubSubDomain(true);
        //factory.setDestinationResolver(null);
        factory.setConcurrency("1-1");
        Global.mqConStatus = true;
        return factory;
    }
}
