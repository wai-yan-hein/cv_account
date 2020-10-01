/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.entity.Gl;
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
public class IssueTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueTableModel.class);
    private String[] columnNames = {"Option", "Vou No", "Item Code", "Description",
        "T-Code", "Trader Name", "Outstanding", "Exp-Date", "Qty", "Unit", "Balance"};
    private JTable parent;
    private List<Gl> listGl = new ArrayList();

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        if (listGl == null) {
            return 0;
        }
        return listGl.size();
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

}
