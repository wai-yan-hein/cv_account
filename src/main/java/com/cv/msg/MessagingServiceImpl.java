/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.msg;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.util.Util1;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
@PropertySource(value = {"classpath:accounts.properties"})
public class MessagingServiceImpl implements MessagingService {

    private static final Logger logger = LoggerFactory.getLogger(MessagingServiceImpl.class);

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Environment environment;

    private final String QUEUE_NAME = "ACCOUNT";

    @Override
    public void sendPaymentToInv(final Gl gl, final Trader trader) {
        if (jmsTemplate != null) {
            String inventoryQueueName = environment.getRequiredProperty("activemq.inv.queue.name");
            final int locationId = Integer.parseInt(environment.getRequiredProperty("inventory.payment.location.id"));

            jmsTemplate.setDefaultDestinationName(inventoryQueueName);
            jmsTemplate.send(new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    MapMessage mm = session.createMapMessage();

                    mm.setString("type", "ADD");
                    mm.setString("entity", "PAYMENT");
                    mm.setString("traderCode", trader.getAppTraderCode());
                    mm.setString("payDate", Util1.toDateStr(gl.getGlDate(), "yyyy-MM-dd"));
                    mm.setInt("locationId", locationId);
                    mm.setString("currency", "MMK");
                    mm.setLong("glId", gl.getGlId());
                    double paid = 0;
                    if (trader instanceof Customer) {
                        if (gl.getDrAmt() != null) {
                            paid = gl.getDrAmt();
                        } else if (gl.getCrAmt() != null) {
                            paid = gl.getCrAmt() * -1;
                        }
                    } else if (trader instanceof Supplier) {
                        if (gl.getDrAmt() != null) {
                            paid = gl.getDrAmt() * -1;
                        } else if (gl.getCrAmt() != null) {
                            paid = gl.getCrAmt();
                        }
                    }
                    mm.setDouble("paidAmtC", paid);
                    mm.setDouble("paidAmtP", paid);
                    mm.setString("remark", "Payment in ledger.");
                    mm.setString("payOption", "Cash");
                    mm.setString("queueName", QUEUE_NAME);
                    return mm;
                }
            });
        }

    }

    @Override
    public void sendDeletePaymentToInv(final long glId) {
        if (jmsTemplate != null) {
            String inventoryQueueName = environment.getRequiredProperty("activemq.inv.queue.name");
            jmsTemplate.setDefaultDestinationName(inventoryQueueName);

            jmsTemplate.send(new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    MapMessage mm = session.createMapMessage();

                    mm.setString("entity", "DELETE-PAYMENT");
                    mm.setLong("glId", glId);
                    return mm;
                }
            });
        }
    }

    @Override
    public void sendPaymentToInvGV(final Gl gl, final Trader trader) {
        if (jmsTemplate != null) {
            String inventoryQueueName = environment.getRequiredProperty("activemq.inv.queue.name");
            final int locationId = Integer.parseInt(environment.getRequiredProperty("inventory.payment.location.id"));
            jmsTemplate.setDefaultDestinationName(inventoryQueueName);

            jmsTemplate.send(new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    MapMessage mm = session.createMapMessage();

                    mm.setString("type", "ADD");
                    mm.setString("entity", "PAYMENT");
                    mm.setString("traderCode", trader.getAppTraderCode());
                    mm.setString("payDate", Util1.toDateStr(gl.getGlDate(), "yyyy-MM-dd"));
                    mm.setInt("locationId", locationId);
                    mm.setString("currency", "MMK");
                    mm.setLong("glId", gl.getGlId());
                    double paid = 0;
                    if (trader instanceof Customer) {
                        if (gl.getDrAmt() != null) {
                            paid = gl.getDrAmt() * -1;
                        } else if (gl.getCrAmt() != null) {
                            paid = gl.getCrAmt();
                        }
                    } else if (trader instanceof Supplier) {
                        if (gl.getDrAmt() != null) {
                            paid = gl.getDrAmt();
                        } else if (gl.getCrAmt() != null) {
                            paid = gl.getCrAmt() * -1;
                        }
                    }
                    mm.setDouble("paidAmtC", paid);
                    mm.setDouble("paidAmtP", paid);
                    mm.setString("remark", "Payment in ledger.");
                    mm.setString("payOption", "Cash");
                    mm.setString("queueName", QUEUE_NAME);
                    return mm;
                }
            });
        }
    }
}
