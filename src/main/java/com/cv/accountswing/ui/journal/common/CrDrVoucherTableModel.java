/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal.common;

import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.view.VCrDrVoucher;
import com.cv.accountswing.entity.view.VCrDrVoucher;
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
public class CrDrVoucherTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrDrVoucherTableModel.class);
    private List<VCrDrVoucher> listVCD = new ArrayList();
    String[] columnNames = {"Date", "Department", "Voucher", "Account", "From", "Currency", "Amount"};
    private JTable parent;
    private String splidId;

    public void setSplidId(String splidId) {
        this.splidId = splidId;
    }

    @Override
    public int getRowCount() {
        if (listVCD == null) {
            return 0;
        }
        return listVCD.size();
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
            VCrDrVoucher vgl = listVCD.get(row);

            switch (column) {
                case 0: //Date
                    return Util1.toDateStr(vgl.getGlDate(), "dd/MM/yyyy");
                case 1: //Dep
                    return vgl.getDeptName();
                case 2://Vou
                    return vgl.getVoucherNo();
                case 3://Desp
                    return vgl.getSourceAccName();
                case 4://From
                    return vgl.getFromDesp();
                case 5:
                    return vgl.getCurrency();
                case 6:
                    return splidId.equals("8") ? vgl.getTtlDr() : vgl.getTtlCr();
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

    public VCrDrVoucher getVGl(int row) {
        return listVCD.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 6:
                return Double.class;
            default:
                return String.class;
        }
    }

    public List<VCrDrVoucher> getListVCD() {
        return listVCD;
    }

    public void setListVCD(List<VCrDrVoucher> listVCD) {
        this.listVCD = listVCD;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;

    }

    public VCrDrVoucher getChartOfAccount(int row) {
        return listVCD.get(row);
    }

    public void addGV(VCrDrVoucher cd) {
        listVCD.add(cd);
        fireTableRowsInserted(listVCD.size() - 1, listVCD.size() - 1);
    }

    public void setGVGroup(int row, VCrDrVoucher cd) {
        if (!listVCD.isEmpty()) {
            listVCD.set(row, cd);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void saveGV(VCrDrVoucher cd, String status) {
        //if (isValidCOA(cd, Global.compId.toString(), Global.loginUser.getUserId().toString(), status)) {
        // coaService.save(cd);
        if (status.equals("NEW")) {
            listVCD.add(new VCrDrVoucher());
            addEmptyRow();
        }

        //}
    }

    public void addEmptyRow() {
        if (listVCD != null) {
            VCrDrVoucher record = new VCrDrVoucher();
            listVCD.add(record);
            fireTableRowsInserted(listVCD.size() - 1, listVCD.size() - 1);
        }

    }

    public int getListSize() {
        return listVCD.size();
    }

}
