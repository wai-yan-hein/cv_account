/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report.common;

import com.cv.accountswing.entity.Region;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class RegionFilterTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionFilterTableModel.class);
    private final String[] columnNames = {"Region Code", "Region Name"};
    private List<Region> listRegion = new ArrayList<>();
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return listRegion.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Region region = listRegion.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return region.getRegId();
            case 1:
                return region.getRegionName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Region region = listRegion.get(rowIndex);
            if (aValue != null) {
                switch (columnIndex) {
                    case 0:
                        if (aValue instanceof Region) {
                            Region s = (Region) aValue;
                            region.setRegId(s.getRegId());
                            region.setRegionName(s.getRegionName());
                        }
                        break;
                    case 1:
                        if (aValue instanceof Region) {
                            Region s = (Region) aValue;
                            region.setRegId(s.getRegId());
                            region.setRegionName(s.getRegionName());
                        }
                        break;

                }
                addNewRow();
                setSelection(rowIndex + 1, 0);
            }
        } catch (Exception e) {
            LOGGER.error("setValueAt :" + e.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);
        table.requestFocusInWindow();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public List<Region> getListRegion() {
        return listRegion;
    }

    public void setListRegion(List<Region> listRegion) {
        this.listRegion = listRegion;
        fireTableDataChanged();
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (!listRegion.isEmpty()) {
            Region s = listRegion.get(listRegion.size() - 1);
            if (s.getRegId() != null) {
                status = true;
            }
        } else {
            status = true;
        }
        return status;
    }

    public void addNewRow() {
        if (listRegion != null) {
            if (hasEmptyRow()) {
                listRegion.add(new Region());
            }
        }
    }

    public void delete(int row) {
        if (listRegion != null) {
            Region region = listRegion.get(row);
            if (region.getRegId() != null) {
                listRegion.remove(row);
                fireTableRowsDeleted(row, row);
                table.setRowSelectionInterval(0, 0);
            }
        }
    }

}
