/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.common;

import com.cv.inv.entity.StockType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class StockTypeTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockTypeTableModel.class);
    private String[] columnNames = {"Code", "Type Name"};
    private List<StockType> listItem = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listItem.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
     @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StockType itemType = listItem.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return itemType.getItemTypeCode();
            case 1:
                return itemType.getItemTypeName();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<StockType> getListItem() {
        return listItem;
    }

    public void setListItem(List<StockType> listItem) {
        this.listItem = listItem;
        fireTableDataChanged();
    }

    public StockType getItemType(int row) {
        return listItem.get(row);
    }

    public void setItemType(StockType itemType, int row) {
        if (!listItem.isEmpty()) {
            listItem.set(row, itemType);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addItemType(StockType item) {
        if (!listItem.isEmpty()) {
            listItem.add(item);
            fireTableRowsInserted(listItem.size() - 1, listItem.size() - 1);
        }
    }

}
