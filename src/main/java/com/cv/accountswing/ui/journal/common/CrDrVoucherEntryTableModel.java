/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
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
public class CrDrVoucherEntryTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrDrVoucherEntryTableModel.class);
    private List<VGl> listVGl = new ArrayList();
    String[] columnNames = {"Person", "Account", "Ref", "Description", "Amount"};
    private JTable parent;
    private JFormattedTextField txtFTotalAmt;
    private String vouType;

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    public void setTxtFTotalAmt(JFormattedTextField txtFTotalAmt) {
        this.txtFTotalAmt = txtFTotalAmt;
    }
    @Autowired
    private COAService cOAService;

    @Override
    public int getRowCount() {
        if (listVGl == null) {
            return 0;
        }
        return listVGl.size();
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
            VGl vgl = listVGl.get(row);

            switch (column) {
                case 0: //Trader
                    return vgl.getTraderName();
                case 1: //Acc
                    return vgl.getAccName();
                case 2://Ref
                    return vgl.getReference();
                case 3://Desp
                    return vgl.getDescription();
                case 4://Dr
                    return vouType.equals("CR") ? vgl.getDrAmt() : vgl.getCrAmt();
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
        VGl vgl = listVGl.get(row);
        switch (column) {
            case 0:
                if (value != null) {
                    if (value instanceof Trader) {
                        Trader trader = (Trader) value;
                        vgl.setTraderCode(trader.getCode());
                        vgl.setTraderName(trader.getTraderName());
                        String coaCode = trader.getAccount().getCode();
                        ChartOfAccount account = cOAService.findById(coaCode);
                        if (account != null) {
                            vgl.setAccountId(coaCode);
                            vgl.setAccName(account.getCoaNameEng());
                            parent.setColumnSelectionInterval(2, 2);
                        } else {
                            parent.setColumnSelectionInterval(1, 1);
                        }
                    }
                }
                parent.setColumnSelectionInterval(1, 1);
                break;
            case 1:
                if (value != null) {
                    if (value instanceof ChartOfAccount) {
                        ChartOfAccount coa = (ChartOfAccount) value;
                        vgl.setAccountId(coa.getCode());
                        vgl.setAccName(coa.getCoaNameEng());
                    }
                }
                parent.setColumnSelectionInterval(2, 2);
                break;
            case 2:
                vgl.setReference(Util1.getString(value));
                parent.setColumnSelectionInterval(3, 3);
                break;
            case 3:
                vgl.setDescription(Util1.getString(value));
                parent.setColumnSelectionInterval(4, 4);
                break;
            case 4:
                if (value != null) {
                    if (vouType.equals("CR")) {
                        vgl.setDrAmt(Util1.getDouble(value));
                    } else {
                        vgl.setCrAmt(Util1.getDouble(value));
                    }
                }
                break;
        }
        addNewRow(vgl, row);
    }

    public VGl getVGl(int row) {
        return listVGl.get(row);
    }

    private void addNewRow(VGl vgl, int row) {
        if (isValidEntry(vgl)) {
            VGl vGl = new VGl();
            listVGl.add(vGl);
            fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
            parent.setRowSelectionInterval(row + 1, row + 1);
            parent.setColumnSelectionInterval(0, 0);
            calAmount();
        }
    }

    private boolean isValidEntry(VGl vgl) {
        boolean status = true;
        if (Util1.isNull(vgl.getAccountId())) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Account.");
            status = false;
        } else if (Util1.getFloat(vgl.getDrAmt()) + Util1.getFloat(vgl.getCrAmt()) == 0) {
            status = false;
        }
        return status;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 4:
                return Double.class;
            default:
                return String.class;
        }
    }

    public List<VGl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<VGl> listVGl) {
        this.listVGl = listVGl;
        calAmount();
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;

    }

    public VGl getChartOfAccount(int row) {
        return listVGl.get(row);
    }

    public void addGV(VGl cd) {
        listVGl.add(cd);
        fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void remove(int row) {
        listVGl.remove(row);
        fireTableRowsDeleted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void setGVGroup(int row, VGl cd) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, cd);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void saveGV(VGl cd, String status) {
        //if (isValidCOA(cd, Global.compCode, Global.loginUser.getAppUserCode(), status)) {
        // coaService.save(cd);
        if (status.equals("NEW")) {
            listVGl.add(new VGl());
        }

        //}
    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            VGl vGl = new VGl();
            listVGl.add(vGl);
            fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
        }

    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listVGl.isEmpty() || listVGl == null) {
            status = true;
        } else {
            VGl vgl = listVGl.get(listVGl.size() - 1);
            if (vgl.getGlCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public int getListSize() {
        return listVGl.size();
    }

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
            fireTableDataChanged();
        }
    }

    private void calAmount() {
        if (listVGl != null) {
            double ttlAmt = 0.0;
            if (vouType.equals("CR")) {
                for (VGl vgl : listVGl) {
                    ttlAmt = Util1.getDouble(vgl.getDrAmt());
                }
            } else {
                for (VGl vgl : listVGl) {
                    ttlAmt = Util1.getDouble(vgl.getCrAmt());
                }
            }
            txtFTotalAmt.setValue(ttlAmt);
        }
    }
}
