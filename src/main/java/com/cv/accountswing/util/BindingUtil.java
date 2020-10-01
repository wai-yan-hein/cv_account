/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.util;

import com.cv.accountswing.common.KeyPropagate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 *
 * @author winswe
 */
public class BindingUtil {

    public static void BindCombo(JComboBox cbo, List list) {
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding
                = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, list, cbo);

        jComboBoxBinding.bind();
    }

    public static void BindCombo(JComboBox cbo, List list, KeyPropagate kp, boolean autoComplete) {
        BindCombo(cbo, list);
        if (autoComplete) {
            ComBoBoxAutoComplete comBoBoxAutoComplete = new ComBoBoxAutoComplete(cbo, kp);
        }
    }

    public static void BindComboFilter(JComboBox cbo, List list) {
        if (list == null) {
            list = new ArrayList();
        }
        list.add(0, "All");
        BindCombo(cbo, list);
    }

    public static void BindComboFilter(JComboBox cbo, List list, KeyPropagate kp, boolean autoComplete, boolean filter) {
        if (filter) {
            BindComboFilter(cbo, list);
        } else {
            BindCombo(cbo, list);
        }
        if (autoComplete) {
            ComBoBoxAutoComplete comBoBoxAutoComplete = new ComBoBoxAutoComplete(cbo, kp);
        }
    }

    public static void BindTable(JTable tbl, List list) {
        org.jdesktop.swingbinding.JTableBinding jTableBinding
                = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, list, tbl);
        jTableBinding.bind();
    }
}
