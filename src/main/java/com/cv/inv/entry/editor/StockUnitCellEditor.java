/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.editor;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.util.BindingUtil;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;

/**
 *
 * @author lenovo
 */
public class StockUnitCellEditor extends AbstractCellEditor implements TableCellEditor {

    static Logger log = Logger.getLogger(StockUnitCellEditor.class.getName());
    JComponent component = null;
    JTable jTable;
    int colIndex = -1;

    @Override
    public Object getCellEditorValue() {
        Object obj = ((JComboBox) component).getSelectedItem();
        return obj;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
        colIndex = vColIndex;

        if (isSelected) {
            // cell (and perhaps other cells) are selected

        }

        try {
            JComboBox jb = new JComboBox();

            BindingUtil.BindCombo(jb, Global.listStockUnit);
            component = jb;
            System.out.println("Unit");

        } catch (Exception ex) {
            log.error("getTableUnitCellEditor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        // Configure the component with the specified value
        //((JTextField) component).setText("");

        // Return the configured component
        return component;
    }

}
