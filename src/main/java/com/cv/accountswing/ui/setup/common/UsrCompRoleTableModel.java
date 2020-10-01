/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.entity.view.VUsrCompRole;
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
public class UsrCompRoleTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsrCompRoleTableModel.class);
    private List<VUsrCompRole> listUCR = new ArrayList();
    private String[] columnNames = {"Compan Name", "Role Name"};

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
        return String.class;

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            VUsrCompRole ucr = listUCR.get(row);

            switch (column) {
                case 0: //Id
                    return ucr.getCompName();
                case 1: //Name
                    return ucr.getRoleName();
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
        if (listUCR == null) {
            return 0;
        }
        return listUCR.size();
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

    public List<VUsrCompRole> getListUCR() {
        return listUCR;
    }

    public void setListUCR(List<VUsrCompRole> listUCR) {
        this.listUCR = listUCR;
        fireTableDataChanged();
    }

    public VUsrCompRole getVUsrCompRole(int row) {
        return listUCR.get(row);
    }

    public void deleteVUsrCompRole(int row) {
        if (!listUCR.isEmpty()) {
            listUCR.remove(row);
            fireTableRowsDeleted(0, listUCR.size());
        }

    }

    public void addVUsrCompRole(VUsrCompRole ucr) {
        listUCR.add(ucr);
        fireTableRowsInserted(listUCR.size() - 1, listUCR.size() - 1);
    }

    public void setVUsrCompRole(int row, VUsrCompRole ucr) {
        if (!listUCR.isEmpty()) {
            listUCR.set(row, ucr);
            fireTableRowsUpdated(row, row);
        }
    }

}
