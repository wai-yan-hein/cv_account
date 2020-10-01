/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.inv.entity.SaleMan;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class SaleManCompleterTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleManCompleterTableModel.class);
    private List<SaleMan> listSaleMan = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};

    public SaleManCompleterTableModel(List<SaleMan> listSaleMan) {
        this.listSaleMan = listSaleMan;
    }

    @Override
    public int getRowCount() {
        if (listSaleMan == null) {
            return 0;
        } else {
            return listSaleMan.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listSaleMan == null) {
            return null;
        }
        if (listSaleMan.isEmpty()) {
            return null;
        }
        try {
            SaleMan saleMan = listSaleMan.get(row);
            switch (column) {
                case 0:
                    return saleMan.getSaleManId();
                case 1:
                    return saleMan.getSaleManName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    public SaleMan getSaleMan(int row) {
        return listSaleMan.get(row);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
