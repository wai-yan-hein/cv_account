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
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 *
 * @author winswe
 */
@Configuration
public class CareServerSenderConfig {

    @Value("${activemq.broker-url}")
    private String brokerUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(CareServerSenderConfig.class);

    @Bean
    public ActiveMQConnectionFactory senderActiveMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory
                = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        activeMQConnectionFactory.setTransportListener(new MQTransportListener());
        LOGGER.info("senderActiveMQConnectionFactory : " + brokerUrl);

        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(
                senderActiveMQConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        //jmsTemplate.setPubSubDomain(true);
        Global.mqConStatus = true;
        return jmsTemplate;
    }
}
