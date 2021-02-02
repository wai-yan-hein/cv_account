/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.entity.ChartOfAccount;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class COAL2TableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(COAL2TableModel.class);
    private List<ChartOfAccount> listCOA = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name", "Group"};

    public COAL2TableModel(List<ChartOfAccount> listCOA) {
        this.listCOA = listCOA;
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
        if (listCOA == null) {
            return null;
        }

        if (listCOA.isEmpty()) {
            return null;
        }

        try {
            ChartOfAccount coa = listCOA.get(row);

            switch (column) {
                case 0: //Code
                    return coa.getCoaCodeUsr();
                case 1: //Description
                    return coa.getCoaNameEng();
                case 2:
                    return coa.getParentUsrDesp();
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
        if (listCOA == null) {
            return 0;
        } else {
            return listCOA.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public ChartOfAccount getCOA(int row) {
        if (listCOA == null) {
            return null;
        } else if (listCOA.isEmpty()) {
            return null;
        } else {
            return listCOA.get(row);
        }
    }

    public int getSize() {
        if (listCOA == null) {
            return 0;
        } else {
            return listCOA.size();
        }
    }
}
