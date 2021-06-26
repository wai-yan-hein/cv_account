/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report.common;

import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.util.Util1;
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
public class DrAmtTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrAmtTableModel.class);
    private List<VGl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Description", "Ref", "No :", "Dr-Amt"};

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
            case 4:
                return Double.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            VGl apar = listVGl.get(row);

            switch (column) {
                case 0: //date
                    return Util1.toDateStr(apar.getGlDate(), "dd/MM/yyyy");
                case 1: //des
                    return apar.getDescription();
                case 2://ref
                    return apar.getReference();
                case 3:
                    return apar.getRefNo();
                case 4://dr-amt
                    return apar.getDrAmt();
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
        if (listVGl == null) {
            return 0;
        }
        return listVGl.size();
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

    public List<VGl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<VGl> listVGl) {
        this.listVGl = listVGl;
        fireTableDataChanged();
    }

    public VGl getVGl(int row) {
        return listVGl.get(row);
    }

    public void deleteVGl(int row) {
        if (!listVGl.isEmpty()) {
            listVGl.remove(row);
            fireTableRowsDeleted(0, listVGl.size());
        }

    }

    public void addVGl(VGl apar) {
        listVGl.add(apar);
        fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void setVGl(int row, VGl apar) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, apar);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
        }
    }

}
