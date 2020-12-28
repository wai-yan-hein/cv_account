/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.entity.Region;
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
public class RegionTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionTableModel.class);
    private List<Region> listRegion = new ArrayList();
    private String[] columnNames = {"Name"};

    public RegionTableModel(List<Region> listRegion) {
        this.listRegion = listRegion;
    }

    public RegionTableModel() {
    }
    
    @Override
    public int getRowCount() {
        if (listRegion == null) {
            return 0;
        }
        return listRegion.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            Region region = listRegion.get(row);

            switch (column) {
                case 0: //Name
                    return region.getRegionName();
                default:
                    return null;
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

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
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

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Region> getListRegion() {
        return listRegion;
    }

    public void setListRegion(List<Region> listRegion) {
        this.listRegion = listRegion;
        fireTableDataChanged();
    }
    
    
    public Region getRegion(int row) {
        return listRegion.get(row);
    }

}
