/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.inv.entity.StockType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class StockTypeTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(StockTypeTableModel.class);
    private List<StockType> listType = new ArrayList<>();
    private final String[] columnNames = {"Stock Type"};

    public StockTypeTableModel(List<StockType> listType) {
        this.listType = listType;
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
        if (listType == null) {
            return null;
        }

        if (listType.isEmpty()) {
            return null;
        }

        try {
            StockType type = listType.get(row);

            switch (column) {
                case 0: //Code
                    return type.getItemTypeName();
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
        if (listType == null) {
            return 0;
        } else {
            return listType.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public StockType getType(int row) {
        if (listType == null) {
            return null;
        } else if (listType.isEmpty()) {
            return null;
        } else {
            return listType.get(row);
        }
    }

}
