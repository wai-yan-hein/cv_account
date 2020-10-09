/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.view.VRetIn;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @lenovo
 */
@Component
public class RetInVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetInVouSearchTableModel.class.getName());
    private List<VRetIn> listVRetIns = new ArrayList();
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
        if (listVRetIns == null) {
            return null;
        }

        if (listVRetIns.isEmpty()) {
            return null;
        }

        try {
            VRetIn vRetIn = listVRetIns.get(row);

            switch (column) {
                case 0: //Date
                    return Util1.toDateStr(vRetIn.getGlDate(), "dd/MM/yyyy");
                case 1: //Vou No

                    return vRetIn.getKey().getVouNo();

                case 2: //Customer
                    return vRetIn.getTraderName();
                case 3: //User
                    return vRetIn.getUserName();
                case 4: //V-Total
                    return vRetIn.getVouTotal();
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
        if (listVRetIns == null) {
            return;
        }

        if (listVRetIns.isEmpty()) {
            return;
        }

        VRetIn record = listVRetIns.get(row);
        VRetIn vRetIn = (VRetIn) value;
        switch (column) {

            case 0: //Date
                if (value != null) {

                    record.setGlDate(vRetIn.getGlDate());

                }
                break;
            case 1: //VouNo
                record.setKey(vRetIn.getKey());
                break;
            case 2://CusName
                record.setTraderName(vRetIn.getTraderName());
            case 3:
                record.setUserName(vRetIn.getUserName());
            case 4:
                record.setVouTotal(vRetIn.getVouTotal());
            default:
                System.out.println("invalid index");
        }
        fireTableRowsUpdated(row, row);
        parent.requestFocusInWindow();
    }

    @Override
    public int getRowCount() {
        if (listVRetIns == null) {
            return 0;
        }
        return listVRetIns.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VRetIn> getListVRetIns() {
        return listVRetIns;
    }

    public void setListGl(List<VRetIn> listVRetIns) {
        this.listVRetIns = listVRetIns;
        fireTableDataChanged();
    }

    public VRetIn getSelectVou(int row) {
        if (listVRetIns != null) {
            if (!listVRetIns.isEmpty()) {
                return listVRetIns.get(row);
            }
        }
        return null;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void clearList() {
        this.listVRetIns.clear();
        fireTableDataChanged();
    }
}
