/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog.common;

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
public class SaleManTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleManTableModel.class);
    private String[] columnNames = {"Code", "Name"};
    private List<SaleMan> listSaleMan = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listSaleMan.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleMan saleMan = listSaleMan.get(row);
            switch (column) {
                case 0:
                    return saleMan.getUserCode();
                case 1:
                    return saleMan.getSaleManName();
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public SaleMan getSaleMan(int row) {
        return listSaleMan.get(row);
    }

    public void addSaleMan(SaleMan saleman) {
        if (listSaleMan != null) {
            listSaleMan.add(saleman);
            fireTableRowsInserted(listSaleMan.size() - 1, listSaleMan.size() - 1);
        }
    }

    public void setSaleMan(SaleMan sale, int row) {
        if (!listSaleMan.isEmpty()) {
            listSaleMan.set(row, sale);
            fireTableRowsUpdated(row, row);
        }
    }

    public void setListSaleMan(List<SaleMan> listSaleMan) {
        this.listSaleMan = listSaleMan;
        fireTableDataChanged();
    }

    public void deleteSaleMan(int row) {
        if (listSaleMan != null) {
            listSaleMan.remove(row);
            fireTableRowsDeleted(0, listSaleMan.size());
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
