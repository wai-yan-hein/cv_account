/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.PurHis;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author lenovo
 */
@Component
public class PurVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurVouSearchTableModel.class.getName());
    private List<PurHis> listPurHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Ref. Vou.", "Customer", "User", "V-Total"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listPurHis == null) {
            return 0;
        }
        return listPurHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Remark
                return String.class;
            case 3: //Customer
                return String.class;
            case 4: //User
                return String.class;
            case 5: //V-Total
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPurHis == null) {
            return null;
        }

        if (listPurHis.isEmpty()) {
            return null;
        }
        try {
            PurHis his = listPurHis.get(row);

            switch (column) {
                case 0://date
                    return Util1.toDateStr(his.getPurDate(), "dd/MM/yyyy");
                case 1://vou-no
                    return his.getPurInvId();
                case 2://remark
                    return his.getRemark();
                case 3://customer
                    return his.getCustomerId();
                case 4://user
                    return his.getCreatedBy();
                case 5://v-total
                    return his.getVouTotal();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<PurHis> getListPurHis() {
        return listPurHis;
    }

    public void setListPurHis(List<PurHis> listPurHis) {
        this.listPurHis = listPurHis;
        fireTableDataChanged();
    }

    public PurHis getSelectVou(int row) {
        if (listPurHis != null) {
            if (!listPurHis.isEmpty()) {
                return listPurHis.get(row);
            }
        }
        return null;
    }
}
