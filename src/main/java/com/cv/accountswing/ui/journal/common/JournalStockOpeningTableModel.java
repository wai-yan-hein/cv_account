/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal.common;

import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.view.VStockOpValue;
import com.cv.accountswing.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author MyoGyi
 */
@Component
public class JournalStockOpeningTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(JournalStockOpeningTableModel.class);
    private List<VStockOpValue> listStockOpening = new ArrayList();
    String[] columnNames = {"Date", "Dept", "Code", "COA Name", "Currency", "OP-Amount"};
    private JTable parent;

    @Override
    public int getRowCount() {
        if (listStockOpening == null) {
            return 0;
        }
        return listStockOpening.size();
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
    public Object getValueAt(int row, int column) {
        try {
            VStockOpValue op = listStockOpening.get(row);

            switch (column) {
                case 0: //Date
                    return Util1.toDate(op.getKey().getTranDate(), "dd/MM/yyyy");
                case 1: //Dept
                    return op.getDeptCodeUsr();
                case 2://Code
                    return op.getCoaCodeUsr();
                case 3://COA name
                    return op.getCoaNameEng();
                case 4://Currency
                    return op.getCurrName();
                case 5://Op -amount
                    return op.getAmount();
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
        VStockOpValue op = listStockOpening.get(row);
        switch (column) {
            case 0:
                if (value != null) {
                    if (value instanceof Department) {
                        Department dep = (Department) value;
                        op.setDeptName(dep.getDeptName());
                    }
                }
                parent.setColumnSelectionInterval(1, 1);
                break;
            case 1:
                if (value instanceof Department) {
                    Department dep = (Department) value;
                    op.setDeptName(dep.getDeptName());
                }
                parent.setColumnSelectionInterval(2, 2);

                break;
            case 2:
                if (value != null) {
                    op.setCoaCodeUsr(value.toString());
                }
                parent.setColumnSelectionInterval(3, 3);
                break;
            case 3:
                if (value instanceof ChartOfAccount) {
                    ChartOfAccount coa = (ChartOfAccount) value;
                    op.setCoaNameEng(coa.getCoaNameEng());
                }
                parent.setColumnSelectionInterval(4, 4);
                break;
            case 4:
                if (value != null) {
                    if (value instanceof Currency) {
                        Currency curr = (Currency) value;
                        op.setCurrName(curr.getCurrencyName());
                        parent.setColumnSelectionInterval(5, 5);
                    }
                }
                break;

            case 5:
                if (value != null) {
                    op.setAmount(Util1.getDouble(value));
                    save(row);
                }
                break;
        }
        parent.requestFocus();
    }

    private void save(int row) {
        parent.setRowSelectionInterval(row + 1, row + 1);
        parent.setColumnSelectionInterval(0, 0);
        // Save object ? List ?
    }

    public VStockOpValue getVGl(int row) {
        return listStockOpening.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 5:
                return Double.class;
            default:
                return String.class;
        }
    }

    public List<VStockOpValue> getListStockOpening() {
        return listStockOpening;
    }

    public void setListStockOpening(List<VStockOpValue> listStockOpening) {
        this.listStockOpening = listStockOpening;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;

    }

    public VStockOpValue getChartOfAccount(int row) {
        return listStockOpening.get(row);
    }

    public void addGV(VStockOpValue op) {
        listStockOpening.add(op);
        fireTableRowsInserted(listStockOpening.size() - 1, listStockOpening.size() - 1);
    }

    public void setGVGroup(int row, VStockOpValue op) {
        if (!listStockOpening.isEmpty()) {
            listStockOpening.set(row, op);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void saveGV(VStockOpValue op, String status) {
        //if (isValidCOA(op, Global.compId.toString(), Global.loginUser.getUserId().toString(), status)) {
        // coaService.save(op);
        if (status.equals("NEW")) {
            listStockOpening.add(new VStockOpValue());
            addEmptyRow();
        }

        //}
    }

    public void addEmptyRow() {
        if (listStockOpening != null) {
            VStockOpValue record = new VStockOpValue();
            listStockOpening.add(record);
            fireTableRowsInserted(listStockOpening.size() - 1, listStockOpening.size() - 1);
        }

    }

    public int getListSize() {
        return listStockOpening.size();
    }

}
