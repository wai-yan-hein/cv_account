/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.common;

import com.cv.accountswing.entity.SystemProperty;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class SystemPropertyTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropertyTableModel.class);
    private List<SystemProperty> listSP = new ArrayList();
    private String[] columnNames = {"Key", "Value"};

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
            case 0:
                return String.class;
            case 1:
                return String.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            SystemProperty sp = listSP.get(row);

            switch (column) {
                case 0: //Id
                    return sp.getKey().getPropKey();
                case 1: //Name
                    return sp.getPropValue();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (listSP == null) {
            return 0;
        }
        return listSP.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<SystemProperty> getListSP() {
        return listSP;
    }

    public void setListSP(List<SystemProperty> listSP) {
        this.listSP = listSP;
        fireTableDataChanged();
    }

    public SystemProperty getSP(int row) {
        return listSP.get(row);
    }

    public void deleteSP(int row) {
        if (!listSP.isEmpty()) {
            listSP.remove(row);
            fireTableRowsDeleted(0, listSP.size());
        }

    }
    public void addSP(SystemProperty sp) {
        listSP.add(sp);
        fireTableRowsInserted(listSP.size() - 1, listSP.size() - 1);
    }

    public void setSP(int row, SystemProperty sp) {
        if (!listSP.isEmpty()) {
            listSP.set(row, sp);
            fireTableRowsUpdated(row, row);
        }
    }

}
