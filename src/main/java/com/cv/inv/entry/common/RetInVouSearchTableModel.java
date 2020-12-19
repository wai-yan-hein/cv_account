/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RetInHis;
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
    private List<RetInHis> listVRetIns = new ArrayList();
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
            RetInHis vRetIn = listVRetIns.get(row);

            switch (column) {
                case 0: //Date
                    return Util1.toDateStr(vRetIn.getRetInDate(), "dd/MM/yyyy");
                case 1: //Vou No

                    return vRetIn.getRetInId();

                case 2: //Customer
                    return vRetIn.getCustomer();
                case 3: //User
                    return vRetIn.getCreatedBy();
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

    public List<RetInHis> getListVRetIns() {
        return listVRetIns;
    }

   public void setListRetInHis(List<RetInHis> listVRetIns) {
        this.listVRetIns = listVRetIns;
        fireTableDataChanged();
    }

    public RetInHis getSelectVou(int row) {
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
