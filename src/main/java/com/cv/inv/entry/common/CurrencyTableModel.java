/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.entity.Currency;
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
public class CurrencyTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CurrencyTableModel.class);
    private List<Currency> listCurrency = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public CurrencyTableModel(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
    }

    public CurrencyTableModel() {
    }

    public List<Currency> getListCurrency() {
        return listCurrency;
    }

    public void setListCurrency(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
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
        if (listCurrency == null) {
            return null;
        }

        if (listCurrency.isEmpty()) {
            return null;
        }

        try {
            Currency cur = listCurrency.get(row);

            switch (column) {
                case 0: //Code
                    return cur.getKey() == null ? null : cur.getKey().getCode();
                case 1: //Description
                    return cur.getCurrencyName();
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
            if (!listCurrency.isEmpty()) {
                if (value != null) {
                    switch (column) {
                        case 0:
                            if (value instanceof Currency) {
                                Currency cur = (Currency) value;
                                listCurrency.set(row, cur);
                            }
                            break;
                        case 1:
                            if (value instanceof Currency) {
                                Currency cur = (Currency) value;
                                listCurrency.set(row, cur);
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
        if (listCurrency == null) {
            return 0;
        } else {
            return listCurrency.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    private void reqTable() {
        int row = table.getRowCount();
        if (row >= 0) {
            table.setRowSelectionInterval(row - 1, row - 1);
            table.setColumnSelectionInterval(0, 0);
            table.requestFocus();
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            Currency cur = new Currency();
            listCurrency.add(cur);
            fireTableRowsInserted(listCurrency.size() - 1, listCurrency.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listCurrency.isEmpty() || listCurrency == null) {
            status = true;
        } else {
            Currency cur = listCurrency.get(listCurrency.size() - 1);
            if (cur.getKey().getCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public void delete(int row) {
        if (!listCurrency.isEmpty()) {
            Currency cur = listCurrency.get(row);
            if (cur.getKey() != null) {
                listCurrency.remove(row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }
}
