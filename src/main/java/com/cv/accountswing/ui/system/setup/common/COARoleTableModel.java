/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.common;

import com.cv.accountswing.entity.ChartOfAccount;
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
public class COARoleTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(COARoleTableModel.class);
    private List<ChartOfAccount> listCOA = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public COARoleTableModel() {
    }

    public List<ChartOfAccount> getListCOA() {
        return listCOA;
    }

    public void setListCOA(List<ChartOfAccount> listCOA) {
        this.listCOA = listCOA;
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
        try {
            ChartOfAccount coa = listCOA.get(row);

            switch (column) {
                case 0: //Code
                    return coa.getCoaCodeUsr();
                case 1: //Description
                    return coa.getCoaNameEng();
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
            if (!listCOA.isEmpty()) {
                if (value != null) {
                    switch (column) {
                        case 0:
                            if (value instanceof ChartOfAccount) {
                                ChartOfAccount coa = (ChartOfAccount) value;
                                listCOA.set(row, coa);

                            }
                            break;
                        case 1:
                            if (value instanceof ChartOfAccount) {
                                ChartOfAccount coa = (ChartOfAccount) value;
                                listCOA.set(row, coa);
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

    public void addNewRow() {
        if (hasEmptyRow()) {
            ChartOfAccount coa = new ChartOfAccount();
            listCOA.add(coa);
            fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listCOA.isEmpty() || listCOA == null) {
            status = true;
        } else {
            ChartOfAccount sm = listCOA.get(listCOA.size() - 1);
            if (sm.getCode() == null) {
                status = false;
            }
        }

        return status;
    }

    private void reqTable() {
        int row = table.getRowCount();
        if (row >= 0) {
            table.setRowSelectionInterval(row - 1, row - 1);
            table.setColumnSelectionInterval(0, 0);
            table.requestFocus();
        }
    }

    public void delete(int row) {
        if (!listCOA.isEmpty()) {
            ChartOfAccount t = listCOA.get(row);
            if (t.getCode() != null) {
                listCOA.remove(row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }
}
