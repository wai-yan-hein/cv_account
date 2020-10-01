/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.inv.entity.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public class StockTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(StockTableModel.class);
    private List<Stock> listStock = new ArrayList<>();
    private final String[] columnNames = {"Code", "Description", "Barcode"};

    public StockTableModel(List<Stock> listStock) {
        this.listStock = listStock;
    }

    @Override
    public int getRowCount() {
        if (listStock == null) {
            return 0;
        } else {
            return listStock.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStock == null) {
            return null;
        }

        if (listStock.isEmpty()) {
            return null;
        }
        Stock stock = listStock.get(row);
        switch (column) {
            case 0://Code
                return stock.getStockCode();
            case 1://Name
                return stock.getStockName();
            case 2://Barcode
                return stock.getBarcode();
            default:
                return null;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public Stock getStock(int row) {
        if (listStock == null) {
            return null;
        } else if (listStock.isEmpty()) {
            return null;
        } else {
            return listStock.get(row);
        }
    }

    public int getSize() {
        if (listStock == null) {
            return 0;
        } else {
            return listStock.size();
        }
    }

}
