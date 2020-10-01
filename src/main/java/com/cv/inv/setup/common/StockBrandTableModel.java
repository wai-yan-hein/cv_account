/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.common;

import com.cv.inv.entity.StockBrand;
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
public class StockBrandTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockBrandTableModel.class);
    private String[] columnNames = {"Brand"};
    private List<StockBrand> listItemBrand = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listItemBrand.size();
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
        StockBrand brand = listItemBrand.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return brand.getBrandName();
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

    public List<StockBrand> getListItemBrand() {
        return listItemBrand;
    }

    public void setListItemBrand(List<StockBrand> listItemBrand) {
        this.listItemBrand = listItemBrand;
        fireTableDataChanged();
    }

    public StockBrand getItemBrand(int row) {
        return listItemBrand.get(row);
    }

    public void setItemBrand(StockBrand brand, int row) {
        if (!listItemBrand.isEmpty()) {
            listItemBrand.set(row, brand);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addItemBrand(StockBrand item) {
        if (!listItemBrand.isEmpty()) {
            listItemBrand.add(item);
            fireTableRowsInserted(listItemBrand.size() - 1, listItemBrand.size() - 1);
        }
    }

}
