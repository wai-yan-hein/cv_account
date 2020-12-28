/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.StockIssueHis;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author lenovo
 */
@Component
public class IssueSearchTableModel extends AbstractTableModel {

    static Logger LOGGER = Logger.getLogger(IssueSearchTableModel.class.getName());
    private List<StockIssueHis> listStockIssueHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location", "Remark", "User"};
     private JTable parent;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Location
                return String.class;
            case 3: //Remark
                return String.class;
            case 4: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStockIssueHis == null) {
            return null;
        }

        if (listStockIssueHis.isEmpty()) {
            return null;
        }

        try {
            StockIssueHis dmgh = listStockIssueHis.get(row);

            switch (column) {
                case 0: //Date
                    // if (dmgh.getDmgDate() != null) {
                    return Util1.toDateStr(dmgh.getIssueDate(), "dd/MM/yyyy");
                // }
                case 1: //Vou No
                    if (Util1.getNullTo(dmgh.isDeleted())) {
                        return dmgh.getIssueId()+ "**";
                    } else {
                        return dmgh.getIssueId();
                    }
                case 2: //Location
                    return dmgh.getLocation().getLocationName();
                case 3: //Remark
                    return dmgh.getRemark();
                case 4: //User
                    return dmgh.getCreatedBy().getUserShort();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listStockIssueHis == null) {
            return 0;
        }
        return listStockIssueHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<StockIssueHis> getListStockIssueHis() {
        return listStockIssueHis;
    }

    public void setListStockIssueHis(List<StockIssueHis> listStockIssueHis) {
        this.listStockIssueHis = listStockIssueHis;
        fireTableDataChanged();
    }

    public StockIssueHis getSelectVou(int row) {
        if (listStockIssueHis != null) {
            if (!listStockIssueHis.isEmpty()) {
                return listStockIssueHis.get(row);
            }
        }
        return null;
    }
    public void setParent(JTable parent) {
        this.parent = parent;
    }
}
