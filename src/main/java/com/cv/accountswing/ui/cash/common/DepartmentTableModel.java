/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.entity.Department;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class DepartmentTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(Department.class);
    private List<Department> listDep = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};

    public DepartmentTableModel(List<Department> listDep) {
        this.listDep = listDep;
    }

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
                    return dep.getDeptCode();
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
        if (listDep == null) {
            return null;
        } else if (listDep.isEmpty()) {
            return null;
        } else {
            return listDep.get(row);
        }
    }

    public int getSize() {
        if (listDep == null) {
            return 0;
        } else {
            return listDep.size();
        }
    }
}
