/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report.common;

import com.cv.inv.entity.StockReport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class StockReportTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockReportTableModel.class);
    private final String[] columnNames = {"Stock Report"};
    private List<StockReport> listReport = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listReport.size();
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
        StockReport report = listReport.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return report.getReportName();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<StockReport> getListReport() {
        return listReport;
    }

    public void setListReport(List<StockReport> listReport) {
        this.listReport = listReport;
        fireTableDataChanged();
    }

    public StockReport getReport(int row) {
        return listReport.get(row);
    }

    public void setReport(StockReport report, int row) {
        if (!listReport.isEmpty()) {
            listReport.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addReport(StockReport item) {
        if (!listReport.isEmpty()) {
            listReport.add(item);
            fireTableRowsInserted(listReport.size() - 1, listReport.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
