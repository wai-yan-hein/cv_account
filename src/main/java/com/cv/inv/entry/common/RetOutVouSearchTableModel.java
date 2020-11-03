/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RetOutHis;
import com.cv.inv.entity.view.VRetOut;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author lenovo
 */
@Component
public class RetOutVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetInVouSearchTableModel.class.getName());
    private List<RetOutHis> listVRetOut = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "User", "V-Total"};
    private JTable parent;

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
            case 2: //Customer
                return String.class;
            case 3: //User
                return String.class;
            case 4: //V-Total
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVRetOut == null) {
            return null;
        }

        if (listVRetOut.isEmpty()) {
            return null;
        }

        try {
            RetOutHis vRetOut = listVRetOut.get(row);

            switch (column) {
                case 0: //Date
                    return Util1.toDateStr(vRetOut.getRetOutDate(), "dd/MM/yyyy");
                case 1: //Vou No

                    return vRetOut.getRetOutId();

                case 2: //Customer
                    return vRetOut.getCustomer();
                case 3: //User
                    return vRetOut.getCreatedBy();
                case 4: //V-Total
                    return vRetOut.getVouTotal();
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
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<RetOutHis> getListVRetOuts() {
        return listVRetOut;
    }

    public void setListGl(List<RetOutHis> listVRetIns) {
        this.listVRetOut = listVRetIns;
        fireTableDataChanged();
    }

    public RetOutHis getSelectVou(int row) {
        if (listVRetOut != null) {
            if (!listVRetOut.isEmpty()) {
                return listVRetOut.get(row);
            }
        }
        return null;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void clearList() {
        this.listVRetOut.clear();
        fireTableDataChanged();
    }
    public void setListReturnHis(List<RetOutHis> list){
        listVRetOut=list;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if (listVRetOut == null) {
            return 0;
        }
        return listVRetOut.size();
    }

}
