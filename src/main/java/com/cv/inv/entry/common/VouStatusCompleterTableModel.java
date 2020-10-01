/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

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
public class VouStatusCompleterTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(VouStatusCompleterTableModel.class);
    private List<VouStatus> listVou = new ArrayList();
    private String[] columnNames = {"Description"};

    public VouStatusCompleterTableModel(List<VouStatus> listVou) {
        this.listVou = listVou;
    }

    @Override
    public int getRowCount() {
        if (listVou == null) {
            return 0;
        }
        return listVou.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            VouStatus vou = listVou.get(row);
            switch (column) {
                case 0:
                    return vou.getStatusDesp();
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

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public VouStatus getVouStatus(int row) {
        return listVou.get(row);
    }

    public List<VouStatus> getlistVou() {
        return listVou;
    }

    public void setlistVou(List<VouStatus> listVou) {
        this.listVou = listVou;
        fireTableDataChanged();
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            default:
                return Object.class;
        }

    }

}
