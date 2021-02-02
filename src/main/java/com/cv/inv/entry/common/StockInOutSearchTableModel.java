/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.StockInOut;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author lenovo
 */
@Component
public class StockInOutSearchTableModel extends AbstractTableModel {

    private final static Logger log = Logger.getLogger(StockInOutSearchTableModel.class.getName());
    private List<StockInOut> listStock = new ArrayList();
    private final String[] columnNames = {"Date", "Batch No", "User", "Desp :", "Remark", "In-Toal", "Out-Toal"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listStock == null) {
            return 0;
        }
        return listStock.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {

            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStock == null) {
            return null;
        }

        if (listStock.isEmpty()) {
            return null;
        }
        try {
            StockInOut stock = listStock.get(row);

            switch (column) {
                case 0://date
                    return Util1.toDateStr(stock.getTranDate(), "dd/MM/yyyy");
                case 1://vou-no
                    return stock.getBatchCode();
                case 2://user
                    return stock.getCreatedBy().getUserName();
                case 3://phone
                    return stock.getDescription();
                case 4://user
                    return stock.getRemark();
                case 5:
                    return stock.getInTotal();
                case 6:
                    return stock.getOutTotal();

            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<StockInOut> getListStock() {
        return listStock;
    }

    public void setListStock(List<StockInOut> listStock) {
        this.listStock = listStock;
        fireTableDataChanged();
    }

    public StockInOut getStock(int row) {
        if (listStock != null) {
            return listStock.get(row);
        } else {
            return null;
        }
    }

}
