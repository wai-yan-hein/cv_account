/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog.common;

import com.cv.inv.entity.VouStatus;
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
public class VouStatusTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(VouStatusTableModel.class);
    private final String[] columnNames = {"Description"};
    private List<VouStatus> listVou = new ArrayList<>();
    private JTable table;

    public List<VouStatus> getListVou() {
        return listVou;
    }

    public void setListVou(List<VouStatus> listVou) {
        this.listVou = listVou;
        fireTableDataChanged();
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return listVou.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VouStatus vouStatus = listVou.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return vouStatus.getStatusDesp();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public VouStatus getVouStatus(int row) {
        return listVou.get(row);
    }

    public void setVouStatus(VouStatus vouStatus, int row) {
        if (!listVou.isEmpty()) {
            listVou.set(row, vouStatus);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addVouStatus(VouStatus vouStatus) {
        if (listVou != null) {
            listVou.add(vouStatus);
            fireTableRowsInserted(listVou.size() - 1, listVou.size() - 1);
        }
    }

    public void deleteVouStatus(int row) {
        if (listVou != null) {
            if (!listVou.isEmpty()) {
                listVou.remove(row);
                fireTableRowsDeleted(0, listVou.size());
            }
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            VouStatus vou = new VouStatus();
            listVou.add(vou);
            fireTableRowsInserted(listVou.size() - 1, listVou.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listVou.isEmpty() || listVou == null) {
            status = true;
        } else {
            VouStatus vou = listVou.get(listVou.size() - 1);
            if (vou.getVouStatusCode() == null) {
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
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!listVou.isEmpty()) {
                if (value != null) {
                    switch (column) {
                        case 0:
                            if (value instanceof VouStatus) {
                                VouStatus trader = (VouStatus) value;
                                listVou.set(row, trader);

                            }
                            break;
                        case 1:
                            if (value instanceof VouStatus) {
                                VouStatus loc = (VouStatus) value;
                                VouStatus trader = (VouStatus) value;
                                listVou.set(row, trader);
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

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void delete(int row) {
        if (!listVou.isEmpty()) {
            VouStatus t = listVou.get(row);
            if (t.getVouStatusCode() != null) {
                listVou.remove(row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }

}
