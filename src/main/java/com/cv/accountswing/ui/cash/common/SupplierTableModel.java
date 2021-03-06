/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.entity.Trader;
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
public class SupplierTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(SupplierTableModel.class);
    private List<Supplier> listTrader = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public SupplierTableModel(List<Supplier> listTrader) {
        this.listTrader = listTrader;
    }

    public SupplierTableModel() {
    }

    public List<Supplier> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<Supplier> listTrader) {
        this.listTrader = listTrader;
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
        if (listTrader == null) {
            return null;
        }

        if (listTrader.isEmpty()) {
            return null;
        }

        try {
            Supplier trader = listTrader.get(row);

            switch (column) {
                case 0: //Code
                    return Util1.isNull(trader.getUserCode(), trader.getCode());
                case 1: //Description
                    return trader.getTraderName();
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
            if (!listTrader.isEmpty()) {
                if (value != null) {
                    switch (column) {
                        case 0:
                            if (value instanceof Trader) {
                                Trader loc = (Trader) value;
                                Supplier sup = new Supplier();
                                sup.setCode(loc.getCode());
                                sup.setUserCode(loc.getUserCode());
                                sup.setTraderName(loc.getTraderName());
                                listTrader.set(row, sup);

                            }
                            break;
                        case 1:
                            if (value instanceof Trader) {
                                Trader loc = (Trader) value;
                                Supplier sup = new Supplier();
                                sup.setCode(loc.getCode());
                                sup.setUserCode(loc.getUserCode());
                                sup.setTraderName(loc.getTraderName());
                                listTrader.set(row, sup);
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
        if (listTrader == null) {
            return 0;
        } else {
            return listTrader.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Supplier getTrader(int row) {
        if (listTrader == null) {
            return null;
        } else if (listTrader.isEmpty()) {
            return null;
        } else {
            return listTrader.get(row);
        }
    }

    public int getSize() {
        if (listTrader == null) {
            return 0;
        } else {
            return listTrader.size();
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            Supplier supplier = new Supplier();
            listTrader.add(supplier);
            fireTableRowsInserted(listTrader.size() - 1, listTrader.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listTrader.isEmpty() || listTrader == null) {
            status = true;
        } else {
            Supplier supplier = listTrader.get(listTrader.size() - 1);
            if (supplier.getCode() == null) {
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
}
