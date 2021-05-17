/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.entity.Customer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class CustomerTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CustomerTableModel.class);
    private List<Customer> listTrader = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};

    public CustomerTableModel(List<Customer> listTrader) {
        this.listTrader = listTrader;
    }

    public CustomerTableModel() {
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
        if (listTrader == null) {
            return null;
        }

        if (listTrader.isEmpty()) {
            return null;
        }

        try {
            Customer trader = listTrader.get(row);

            switch (column) {
                case 0: //Code
                    return trader.getUserCode();
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

    public Customer getTrader(int row) {
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
            Customer customer = new Customer();
            listTrader.add(customer);
            fireTableRowsInserted(listTrader.size() - 1, listTrader.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listTrader.isEmpty() || listTrader == null) {
            status = true;
        } else {
            Customer customer = listTrader.get(listTrader.size() - 1);
            if (customer.getCode()== null) {
                status = false;
            }
        }

        return status;
    }
}
