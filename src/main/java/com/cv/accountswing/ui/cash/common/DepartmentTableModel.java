/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.entity.Department;
import com.cv.accountswing.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class DepartmentTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(DepartmentTableModel.class);
    private List<Department> listDep = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public DepartmentTableModel(List<Department> listDep) {
        this.listDep = listDep;
    }

    public DepartmentTableModel() {
    }

    public List<Department> getListDep() {
        return listDep;
    }

    public void setListDep(List<Department> listDep) {
        this.listDep = listDep;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDep == null) {
            return null;
        }

        if (listDep.isEmpty()) {
            return null;
        }

        try {
            Department dep = listDep.get(row);

            switch (column) {
                case 0: //Code
                    return Util1.isNull(dep.getUsrCode(), dep.getDeptCode());
                case 1: //Description
                    return dep.getDeptName();
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
        try {
            if (!listDep.isEmpty()) {
                if (value != null) {
                    switch (column) {
                        case 0:
                            if (value instanceof Department) {
                                Department dep = (Department) value;
                                listDep.set(row, dep);

                            }
                            break;
                        case 1:
                            if (value instanceof Department) {
                                Department dep = (Department) value;
                                listDep.set(row, dep);
                            }
                            break;
                    }
                    addNewRow();
                    reqTable();

                }
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
    }

    private void reqTable() {
        int row = table.getRowCount();
        if (row >= 0) {
            table.setRowSelectionInterval(row - 1, row - 1);
            table.setColumnSelectionInterval(0, 0);
            table.requestFocus();
        }
    }

    @Override
    public int getRowCount() {
        if (listDep == null) {
            return 0;
        } else {
            return listDep.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Department getDepatment(int row) {
        return listDep.get(row);

    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            Department dep = new Department();
            listDep.add(dep);
            fireTableRowsInserted(listDep.size() - 1, listDep.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listDep.isEmpty() || listDep == null) {
            status = true;
        } else {
            Department dep = listDep.get(listDep.size() - 1);
            if (dep.getDeptCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public void addDepartment(Department dep) {
        if (!listDep.isEmpty()) {
            if (dep != null) {
                listDep.add(dep);
                fireTableRowsInserted(listDep.size() - 1, listDep.size() - 1);
            }
        }
    }

    public void delete(int row) {
        if (!listDep.isEmpty()) {
            Department dep = listDep.get(row);
            if (dep.getDeptCode() != null) {
                listDep.remove(row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }
}
