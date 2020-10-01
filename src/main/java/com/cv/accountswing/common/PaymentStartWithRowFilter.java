/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.common;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PaymentStartWithRowFilter extends RowFilter<Object, Object> {

    static Logger log = Logger.getLogger(PaymentStartWithRowFilter.class.getName());
    private final JTextField jtf;
    private final JCheckBox chk;

    public PaymentStartWithRowFilter(JTextField jtf, JCheckBox chk) {
        this.jtf = jtf;
        this.chk = chk;
    }

    @Override
    public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
        for (int i = 0; i < entry.getValueCount(); i++) {
            //String tmp = entry.getStringValue(i);
            //log.info("filter : Entry - " + tmp.toUpperCase() + "  type : " + jtf.getText().toUpperCase());
            if (entry.getStringValue(i) != null) {
                if (chk.isSelected()) {
                    if (jtf.getText().isEmpty()) {
                        String tmpValue = entry.getStringValue(5);
                        if (tmpValue != null) {
                            if (!tmpValue.isEmpty()) {
                                int ttlOverDue = Integer.parseInt(tmpValue);
                                if (ttlOverDue > 0) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        if (entry.getStringValue(i).toUpperCase().startsWith(
                                jtf.getText().toUpperCase())) {
                            String tmpValue = entry.getStringValue(5);
                            if (tmpValue != null) {
                                if (!tmpValue.isEmpty()) {
                                    int ttlOverDue = Integer.parseInt(tmpValue);
                                    if (ttlOverDue > 0) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (entry.getStringValue(i).toUpperCase().startsWith(
                            jtf.getText().toUpperCase())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
