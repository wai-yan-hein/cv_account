/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.common;

import com.cv.inv.entity.VouStatus;
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
public class VouStatusTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(VouStatusTableModel.class);
    private String[] columnNames = {"Description"};
    private List<VouStatus> listVou = new ArrayList<>();

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

    public void setListVouStatus(List<VouStatus> listVou) {
        this.listVou = listVou;
        fireTableDataChanged();
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

}
