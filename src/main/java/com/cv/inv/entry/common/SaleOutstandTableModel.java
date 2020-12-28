/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.inv.entity.SaleHisDetail;
import com.cv.inv.entity.SaleOutstand;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class SaleOutstandTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleOutstandTableModel.class.getName());
    private final List<SaleOutstand> listSaleOutstand = new ArrayList();
    private List<SaleHisDetail> listSaleDetail;
    private final String[] columnNames = {"Option", "Code", "Description", "Outs-Qty"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listSaleOutstand == null) {
            return 0;
        }
        return listSaleOutstand.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (listSaleOutstand == null) {
            return null;
        }

        if (listSaleOutstand.isEmpty()) {
            return null;
        }
        try {
            SaleOutstand saleOut = listSaleOutstand.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return saleOut.getOutsOption();
                case 1:
                    return saleOut.getStock().getStockCode();
                case 2:
                    return saleOut.getStock().getStockName();
                case 3:
                    return saleOut.getQtyStr();
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
    public boolean isCellEditable(int row, int column) {
        return column == 3;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

}
