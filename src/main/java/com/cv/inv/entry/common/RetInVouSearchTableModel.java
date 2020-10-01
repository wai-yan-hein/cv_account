/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @lenovo
 */
public class RetInVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetInVouSearchTableModel.class.getName());
    private List<Gl> listGl = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "User", "V-Total"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Vou No
                return String.class;
            case 2: //Customer
                return String.class;
            case 3: //User
                return String.class;
            case 4: //V-Total
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listGl == null) {
            return null;
        }

        if (listGl.isEmpty()) {
            return null;
        }

        try {
            Gl gl = listGl.get(row);

            switch (column) {
                case 0: //Date
                    return gl.getGlDate();
                case 1: //Vou No

                    return gl.getVouNo();

                case 2: //Customer
                    return gl.getTraderId();
                case 3: //User
                    return gl.getCreatedBy();
                case 4: //V-Total
                    return gl.getVouTotal();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listGl == null) {
            return 0;
        }
        return listGl.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Gl> getListGl() {
        return listGl;
    }

    public void setListGl(List<Gl> listGl) {
        this.listGl = listGl;
        fireTableDataChanged();
    }

    public Gl getSelectVou(int row) {
        if (listGl != null) {
            if (!listGl.isEmpty()) {
                return listGl.get(row);
            }
        }
        return null;
    }
}
