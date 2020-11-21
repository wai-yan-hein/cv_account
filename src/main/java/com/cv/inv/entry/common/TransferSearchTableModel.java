/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.TransferHis;
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
public class TransferSearchTableModel extends AbstractTableModel {

    static Logger LOGGER = Logger.getLogger(TransferSearchTableModel.class.getName());
    private List<TransferHis> listTransferHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location", "Remark", "User"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
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
            case 2: //Location
                return String.class;
            case 3: //Remark
                return String.class;
            case 4: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listTransferHis == null) {
            return null;
        }

        if (listTransferHis.isEmpty()) {
            return null;
        }

        try {
            TransferHis dmgh = listTransferHis.get(row);

            switch (column) {
                case 0: //Date
                    // if (dmgh.getDmgDate() != null) {
                    return Util1.toDateStr(dmgh.getTranDate(), "dd/MM/yyyy");
                // }
                case 1: //Vou No
                    if (Util1.getNullTo(dmgh.isDeleted())) {
                        return dmgh.getTranVouId() + "**";
                    } else {
                        return dmgh.getTranVouId();
                    }
                case 2: //Location
                    return dmgh.getFromLocation().getLocationName();
                case 3: //Remark
                    return dmgh.getRemark();
                case 4: //User
                    return dmgh.getCreatedBy().getUserShort();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listTransferHis == null) {
            return 0;
        }
        return listTransferHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TransferHis> getListTransferHis() {
        return listTransferHis;
    }

    public void setListTransferHis(List<TransferHis> listTransferHis) {
        this.listTransferHis = listTransferHis;
        fireTableDataChanged();
    }

    public TransferHis getSelectVou(int row) {
        if (listTransferHis != null) {
            if (!listTransferHis.isEmpty()) {
                return listTransferHis.get(row);
            }
        }
        return null;
    }
}
