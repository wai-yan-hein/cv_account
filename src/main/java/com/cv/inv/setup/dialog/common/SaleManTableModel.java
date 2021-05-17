/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.SaleMan;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
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

    private static final Logger log = LoggerFactory.getLogger(SaleManTableModel.class);
    private String[] columnNames = {"Code", "Name"};
    private List<SaleMan> listSaleMan = new ArrayList<>();
    private JTable table;

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

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
                    return Util1.isNull(saleMan.getUserCode(), saleMan.getSaleManCode());
                case 1:
                    return saleMan.getSaleManName();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!listSaleMan.isEmpty()) {
                if (value != null) {
                    switch (column) {
                        case 0:
                            if (value instanceof SaleMan) {
                                SaleMan loc = (SaleMan) value;
                                listSaleMan.set(row, loc);

                            }
                            break;
                        case 1:
                            if (value instanceof SaleMan) {
                                SaleMan loc = (SaleMan) value;
                                listSaleMan.set(row, loc);
                            }
                            break;
                    }
                    addNewRow();
                    reqTable();

                }
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
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

    public List<SaleMan> getListSaleMan() {
        return listSaleMan;
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

    public void addNewRow() {
        if (hasEmptyRow()) {
            SaleMan sm = new SaleMan();
            listSaleMan.add(sm);
            fireTableRowsInserted(listSaleMan.size() - 1, listSaleMan.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listSaleMan.isEmpty() || listSaleMan == null) {
            status = true;
        } else {
            SaleMan sm = listSaleMan.get(listSaleMan.size() - 1);
            if (sm.getSaleManCode() == null) {
                status = false;
            }
        }

        return status;
    }

    private void reqTable() {
        int row = table.getRowCount();
        if (row >= 0) {
            table.setRowSelectionInterval(row - 1, row - 1);
            table.setColumnSelectionInterval(0, 0);
            table.requestFocus();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; //To change body of generated methods, choose Tools | Templates.
    }

    public void delete(int row) {
        if (!listSaleMan.isEmpty()) {
            SaleMan t = listSaleMan.get(row);
            if (t.getSaleManCode() != null) {
                listSaleMan.remove(row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }

}
