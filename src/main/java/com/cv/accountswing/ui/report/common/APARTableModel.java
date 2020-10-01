/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report.common;

import com.cv.accountswing.entity.view.VApar;
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
public class APARTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(APARTableModel.class);
    private List<VApar> listAPAR = new ArrayList();
    private String[] columnNames = {"Code", "Trader Name", "Currency", "Dr-Amt", "Cr-Amt", "Net Change"};

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
            VApar apar = listAPAR.get(row);

            switch (column) {
                case 0: //code
                    return apar.getTraderId();
                case 1: //Name
                    return apar.getTraderName();
                case 2:
                    return apar.getKey().getCurrency();
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
        if (listAPAR == null) {
            return 0;
        }
        return listAPAR.size();
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

    public List<VApar> getListAPAR() {
        return listAPAR;
    }

    public void setListAPAR(List<VApar> listAPAR) {
        this.listAPAR = listAPAR;
        fireTableDataChanged();
    }

    public VApar getAPAR(int row) {
        return listAPAR.get(row);
    }

    public void deleteAPAR(int row) {
        if (!listAPAR.isEmpty()) {
            listAPAR.remove(row);
            fireTableRowsDeleted(0, listAPAR.size());
        }

    }

    public void addAPAR(VApar apar) {
        listAPAR.add(apar);
        fireTableRowsInserted(listAPAR.size() - 1, listAPAR.size() - 1);
    }

    public void setAPAR(int row, VApar apar) {
        if (!listAPAR.isEmpty()) {
            listAPAR.set(row, apar);
            fireTableRowsUpdated(row, row);
        }
    }

}
