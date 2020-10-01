/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal.common;

import com.cv.accountswing.entity.view.VGeneralVoucher;
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
public class JournalTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(JournalTableModel.class);
    private List<VGeneralVoucher> listGV = new ArrayList();
    //String  userId=Global.loginUser.getUserId().toString();
    String[] columnNames = {"Date", "Voucher", "Refrence"};
    private JTable parent;

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
            VGeneralVoucher gv = listGV.get(row);

            switch (column) {
                case 0: //Date 
                    return Util1.toDateStr(gv.getGlDate(), "dd/MM/yyyy");
                case 1: //Vou
                    return gv.getVouNo();
                case 2://Refrence
                    return gv.getReference();
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

    
    

    public VGeneralVoucher getVGl(int row) {
        return listGV.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    public List<VGeneralVoucher> getListGV() {
        return listGV;
    }

    public void setListGV(List<VGeneralVoucher> listGV) {
        this.listGV = listGV;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;

    }

    public VGeneralVoucher getChartOfAccount(int row) {
        return listGV.get(row);
    }

    public void addGV(VGeneralVoucher gv) {
        listGV.add(gv);
        fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
    }

    public void setGVGroup(int row, VGeneralVoucher gv) {
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

    public void saveGV(VGeneralVoucher gv, String status) {
        //if (isValidCOA(gv, Global.compId.toString(), Global.loginUser.getUserId().toString(), status)) {
        // coaService.save(gv);
        if (status.equals("NEW")) {
            listGV.add(new VGeneralVoucher());
            addEmptyRow();
        }

        //}
    }
    private void addRow(){
        if (listGV != null) {
                VGeneralVoucher record = new VGeneralVoucher();
                listGV.add(record);
                fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
            }
    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            if (listGV != null) {
                VGeneralVoucher record = new VGeneralVoucher();
                listGV.add(record);
                fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
            }
        }

    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listGV.isEmpty() || listGV == null) {
            status = true;
        } else {
            VGeneralVoucher vgl = listGV.get(listGV.size() - 1);
            if (vgl.getGvVouNo() == null) {
                status = false;
            }
        }

        return status;
    }

    public int getListSize() {
        return listGV.size();
    }

}
