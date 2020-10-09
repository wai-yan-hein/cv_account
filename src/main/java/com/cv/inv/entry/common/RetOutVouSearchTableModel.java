/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
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
    private List<VRetOut> listVRetOut = new ArrayList();
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
            VRetOut vRetOut = listVRetOut.get(row);

            switch (column) {
                case 0: //Date
                    return Util1.toDateStr(vRetOut.getGlDate(), "dd/MM/yyyy");
                case 1: //Vou No

                    return vRetOut.getKey().getVouNo();

                case 2: //Customer
                    return vRetOut.getTraderName();
                case 3: //User
                    return vRetOut.getUserName();
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
        if (listVRetOut == null) {
            return;
        }

        if (listVRetOut.isEmpty()) {
            return;
        }

        VRetOut record = listVRetOut.get(row);
        VRetOut vRetOut = (VRetOut) value;
        switch (column) {

            case 0: //Date
                if (value != null) {

                    record.setGlDate(vRetOut.getGlDate());

                }
                break;
            case 1: //VouNo
                record.setKey(vRetOut.getKey());
                break;
            case 2://CusName
                record.setTraderName(vRetOut.getTraderName());
            case 3:
                record.setUserName(vRetOut.getUserName());
            case 4:
                record.setVouTotal(vRetOut.getVouTotal());
            default:
                System.out.println("invalid index");
        }
        fireTableRowsUpdated(row, row);
        parent.requestFocusInWindow();
    }

    @Override
    public int getRowCount() {
        if (listVRetOut == null) {
            return 0;
        }
        return listVRetOut.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VRetOut> getListVRetOuts() {
        return listVRetOut;
    }

    public void setListGl(List<VRetOut> listVRetIns) {
        this.listVRetOut = listVRetIns;
        fireTableDataChanged();
    }

    public VRetOut getSelectVou(int row) {
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

}
