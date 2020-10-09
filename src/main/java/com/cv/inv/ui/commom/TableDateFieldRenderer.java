/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.ui.commom;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author WSwe
 */
public class TableDateFieldRenderer extends DefaultTableCellRenderer {

    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");

    public TableDateFieldRenderer(){
        super();
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        
        if (value instanceof Date) {
            value = f.format(value);
        }
        
        return super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
    }
}
