/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal.common;

import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author MyoGyi
 */
@Component
public class JournalEntryTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(JournalEntryTableModel.class);
    private List<VGl> listGV = new ArrayList();
    //String  userId=Global.loginUser.getUserId().toString();
    String[] columnNames = {"Dept:", "Descripiton", "Cus / Sup", "Account", "Dr-Amt", "Crd-Amt"};
    private JTable parent;
    JFormattedTextField ttlCrdAmt;
    JFormattedTextField ttlDrAmt;

    public JFormattedTextField getTtlCrdAmt() {
        return ttlCrdAmt;
    }

    public void setTtlCrdAmt(JFormattedTextField ttlCrdAmt) {
        this.ttlCrdAmt = ttlCrdAmt;
    }

    public JFormattedTextField getTtlDrAmt() {
        return ttlDrAmt;
    }

    public void setTtlDrAmt(JFormattedTextField ttlDrAmt) {
        this.ttlDrAmt = ttlDrAmt;
    }

    @Autowired
    private COAService cOAService;

    @Override
    public int getRowCount() {
        if (listGV == null) {
            return 0;
        }
        return listGV.size();
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
            VGl gv = listGV.get(row);

            switch (column) {
                case 0: //Deapart Id
                    return gv.getDeptName();
                case 1: //Des
                    return gv.getDescription();
                case 2://Cus Id
                    return gv.getTraderName();
                case 3://accc
                    return gv.getSrcAccName();

                case 4://dr
                    return gv.getDrAmt();
                case 5://cr
                    return gv.getCrAmt();
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
        VGl gv = listGV.get(row);
        switch (column) {
            case 0:
                if (value != null) {
                    if (value instanceof Department) {
                        Department dep = (Department) value;
                        gv.setDeptId(dep.getDeptCode());
                        gv.setDeptName(dep.getDeptName());
                        parent.setColumnSelectionInterval(1, 1);
                    }
                }
                break;
            case 1:
                if (value != null) {
                    gv.setDescription(value.toString());
                }
                parent.setColumnSelectionInterval(2, 2);
                break;
            case 2:
                if (value != null) {
                    if (value instanceof Trader) {
                        Trader trader = (Trader) value;
                        gv.setTraderId(Util1.getLong(trader.getId()));
                        gv.setTraderName(trader.getTraderName());
                        if (trader.getAccount() != null) {
                            gv.setSourceAcId(trader.getAccount().getCode());
                            gv.setSrcAccName(trader.getAccount().getCoaNameEng());
                            parent.setColumnSelectionInterval(3, 3);
                        } else {
                            parent.setColumnSelectionInterval(2, 3);
                        }

                    }
                }
                break;
            case 3:
                if (value != null) {
                    if (value instanceof ChartOfAccount) {
                        ChartOfAccount coa = (ChartOfAccount) value;
                        gv.setSourceAcId(coa.getCode());
                        gv.setSrcAccName(coa.getCoaNameEng());

                    }
                }
                parent.setColumnSelectionInterval(3, 3);
                break;

            case 4:
                if (value != null) {
                    gv.setDrAmt(Util1.getDouble(value));
                    if (gv.getCrAmt() != null) {
                        gv.setCrAmt(0.0);
                        parent.setColumnSelectionInterval(5, 5);
                        parent.setRowSelectionInterval(row + 1, row + 1);
                    }
                }
                break;
            case 5:
                if (value != null) {
                    gv.setCrAmt(Util1.getDouble(value));
                    if (gv.getDrAmt() != null) {
                        gv.setDrAmt(0.0);
                        parent.setColumnSelectionInterval(4, 4);
                        parent.setRowSelectionInterval(row + 1, row + 1);
                    }
                }
                break;
        }
        addEmptyRow();
        calTotalAmt();
        parent.requestFocusInWindow();
    }

    public VGl getVGl(int row) {
        return listGV.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 4:
                return Double.class;
            case 5:
                return Double.class;
            default:
                return String.class;
        }
    }

    public List<VGl> getListGV() {
        listGV.remove(listGV.size() - 1);
        return listGV;
    }

    public void setListGV(List<VGl> listGV) {
        this.listGV = listGV;
        calTotalAmt();
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;

    }

    public VGl getChartOfAccount(int row) {
        return listGV.get(row);
    }

    public void addGV(VGl gv) {
        listGV.add(gv);
        fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
    }

    public void setGVGroup(int row, VGl gv) {
        if (!listGV.isEmpty()) {
            listGV.set(row, gv);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void saveGV(VGl gv, String status) {
        //if (isValidCOA(gv, Global.compId.toString(), Global.loginUser.getUserId().toString(), status)) {
        // coaService.save(gv);
        if (status.equals("NEW")) {
            listGV.add(new VGl());
            addEmptyRow();
        }

        //}
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listGV.isEmpty() || listGV == null) {
            status = true;
        } else {
            VGl vgl = listGV.get(listGV.size() - 1);
            if (vgl.getSourceAcId() == null) {
                status = false;
            }
        }

        return status;
    }

    private void addRow() {
        if (listGV != null) {
            VGl record = new VGl();
            listGV.add(record);
            fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
        }
    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            if (listGV != null) {
                VGl record = new VGl();
                listGV.add(record);
                fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
            }
        }

    }

    public void clear() {
        if (listGV != null) {
            listGV.clear();
            fireTableDataChanged();
        }
    }

    public int getListSize() {
        return listGV.size();
    }

    private void calTotalAmt() {
        double crdAmt = 0.0;
        double drAmt = 0.0;
        for (VGl vgl : listGV) {
            crdAmt += Util1.getDouble(vgl.getCrAmt());
            drAmt += Util1.getDouble(vgl.getDrAmt());
        }
        ttlCrdAmt.setValue(crdAmt);
        ttlDrAmt.setValue(drAmt);
    }
}
