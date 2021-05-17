/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class LocationTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(Location.class);
    private List<Location> listLocation = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public LocationTableModel(List<Location> listLocation) {
        this.listLocation = listLocation;
    }

    public LocationTableModel() {
    }

    public List<Location> getListLocation() {
        return listLocation;
    }

    public void setListLocation(List<Location> listLocation) {
        this.listLocation = listLocation;
        fireTableDataChanged();

    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listLocation == null) {
            return null;
        }

        if (listLocation.isEmpty()) {
            return null;
        }

        try {
            Location loc = listLocation.get(row);

            switch (column) {
                case 0: //Code
                    return Util1.isNull(loc.getUserCode(), loc.getLocationCode());
                case 1: //Description
                    return loc.getLocationName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!listLocation.isEmpty()) {
                if (value != null) {
                    switch (column) {
                        case 0:
                            if (value instanceof Location) {
                                Location loc = (Location) value;
                                listLocation.set(row, loc);

                            }
                            break;
                        case 1:
                            if (value instanceof Location) {
                                Location loc = (Location) value;
                                listLocation.set(row, loc);
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
    public int getRowCount() {
        if (listLocation == null) {
            return 0;
        } else {
            return listLocation.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Location getDepatment(int row) {
        if (listLocation == null) {
            return null;
        } else if (listLocation.isEmpty()) {
            return null;
        } else {
            return listLocation.get(row);
        }
    }

    public int getSize() {
        if (listLocation == null) {
            return 0;
        } else {
            return listLocation.size();
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            Location loc = new Location();
            listLocation.add(loc);
            fireTableRowsInserted(listLocation.size() - 1, listLocation.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listLocation.isEmpty() || listLocation == null) {
            status = true;
        } else {
            Location loc = listLocation.get(listLocation.size() - 1);
            if (loc.getLocationCode() == null) {
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

    public void delete(int row) {
        if (!listLocation.isEmpty()) {
            Location loc = listLocation.get(row);
            if (loc.getLocationCode() != null) {
                listLocation.remove(row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }
}
