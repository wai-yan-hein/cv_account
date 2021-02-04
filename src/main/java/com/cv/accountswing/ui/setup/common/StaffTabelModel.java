/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.entity.Staff;
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
public class StaffTabelModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffTabelModel.class);
    private List<Staff> listStaff = new ArrayList();
    private String[] columnNames = {"Code", "Name", "Active"};

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
            case 2:
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Staff staff = listStaff.get(row);

            switch (column) {
                case 0: //Id
                    return staff.getUserCode();
                case 1: //Name
                    return staff.getTraderName();
                case 2:
                    return staff.getActive();
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
        if (listStaff == null) {
            return 0;
        }
        return listStaff.size();
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

    public List<Staff> getListStaff() {
        return listStaff;
    }

    public void setListStaff(List<Staff> listStaff) {
        this.listStaff = listStaff;
        fireTableDataChanged();
    }

    public Staff getStaff(int row) {
        return listStaff.get(row);
    }

    public void deleteStaff(int row) {
        if (!listStaff.isEmpty()) {
            listStaff.remove(row);
            fireTableRowsDeleted(0, listStaff.size());
        }

    }

    public void addStaff(Staff staff) {
        listStaff.add(staff);
        fireTableRowsInserted(listStaff.size() - 1, listStaff.size() - 1);
    }

    public void setStaff(int row, Staff staff) {
        if (!listStaff.isEmpty()) {
            listStaff.set(row, staff);
            fireTableRowsUpdated(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
