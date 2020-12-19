/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report.common;

import com.cv.inv.entity.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class StockFilterTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockFilterTableModel.class);
    private final String[] columnNames = {"Stock Code", "Stock Name"};
    private List<Stock> listStock = new ArrayList<>();
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return listStock.size();
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
        Stock stock = listStock.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return stock.getStockCode();
            case 1:
                return stock.getStockName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Stock stock = listStock.get(rowIndex);
            if (aValue != null) {
                switch (columnIndex) {
                    case 0:
                        if (aValue instanceof Stock) {
                            Stock s = (Stock) aValue;
                            stock.setStockCode(s.getStockCode());
                            stock.setStockName(s.getStockName());
                        }
                        break;
                    case 1:
                        if (aValue instanceof Stock) {
                            Stock s = (Stock) aValue;
                            stock.setStockCode(s.getStockCode());
                            stock.setStockName(s.getStockName());
                        }
                        break;

                }
                addNewRow();
                setSelection(rowIndex + 1, 0);
            }
        } catch (Exception e) {
            LOGGER.error("setValueAt :" + e.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);
        table.requestFocusInWindow();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public List<Stock> getListStock() {
        return listStock;
    }

    public void setListStock(List<Stock> listStock) {
        this.listStock = listStock;
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (!listStock.isEmpty()) {
            Stock s = listStock.get(listStock.size() - 1);
            if (s.getStockCode() != null) {
                status = true;
            }
        } else {
            status = true;
        }
        return status;
    }

    public void addNewRow() {
        if (listStock != null) {
            if (hasEmptyRow()) {
                listStock.add(new Stock());
            }
        }
    }

    public void delete(int row) {
        if (listStock != null) {
            Stock s = listStock.get(row);
            if (s.getStockCode() != null) {
                listStock.remove(row);
                fireTableRowsDeleted(row, row);
                table.setRowSelectionInterval(0, 0);
            }
        }
    }

}
