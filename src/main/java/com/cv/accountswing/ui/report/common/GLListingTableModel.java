/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report.common;

import com.cv.accountswing.entity.view.VTriBalance;
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
public class GLListingTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(GLListingTableModel.class);
    private List<VTriBalance> listTBal = new ArrayList();
    private String[] columnNames = {"Code", "Description", "Currency", "Dr-Amt", "Cr-Amt", "Net Change"};

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
            case 3:
                return Double.class;
            case 4:
                return Double.class;
            case 5:
                return Double.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            VTriBalance apar = listTBal.get(row);

            switch (column) {
                case 0: //code
                    return apar.getUsrCoaCode();
                case 1: //Name
                    return apar.getCoaName();
                case 2:
                    return apar.getKey().getCurrId();
                case 3:
                    return apar.getDrAmt();
                case 4:
                    return apar.getCrAmt();
                case 5:
                    return apar.getClosing();
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
        if (listTBal == null) {
            return 0;
        }
        return listTBal.size();
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

    public List<VTriBalance> getListTBAL() {
        return listTBal;
    }

    public void setListTBAL(List<VTriBalance> listTBal) {
        this.listTBal = listTBal;
        fireTableDataChanged();
    }

    public VTriBalance getTBAL(int row) {
        return listTBal.get(row);
    }

    public void deleteTBAL(int row) {
        if (!listTBal.isEmpty()) {
            listTBal.remove(row);
            fireTableRowsDeleted(0, listTBal.size());
        }

    }

    public void addTBAL(VTriBalance apar) {
        listTBal.add(apar);
        fireTableRowsInserted(listTBal.size() - 1, listTBal.size() - 1);
    }

    public void setTBAL(int row, VTriBalance apar) {
        if (!listTBal.isEmpty()) {
            listTBal.set(row, apar);
            fireTableRowsUpdated(row, row);
        }
    }

}
