/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.entity.Supplier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class SupplierImportTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupplierImportTableModel.class);
    private List<Supplier> listSupplier = new ArrayList();
    private String[] columnNames = {"Code", "Usr-Code", "Name", "Address", "Phone", "Account Code"};

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
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Supplier customer = listSupplier.get(row);

            switch (column) {
                case 0: //Id
                    return customer.getMigCode();
                case 1: //Name
                    return customer.getUserCode();
                case 2:
                    return customer.getTraderName();
                case 3:
                    return customer.getAddress();
                case 4:
                    return customer.getPhone();
                case 5:
                    if (customer.getAccount() != null) {
                        return customer.getAccount().getCode();
                    } else {
                        return null;
                    }
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

    @Override
    public int getRowCount() {
        if (listSupplier == null) {
            return 0;
        }
        return listSupplier.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Supplier> getListSupplier() {
        return listSupplier;
    }

    public void setListSupplier(List<Supplier> listSupplier) {
        this.listSupplier = listSupplier;
        fireTableDataChanged();
    }

    public Supplier getCustomer(int row) {
        return listSupplier.get(row);
    }

    public void deleteCustomer(int row) {
        if (!listSupplier.isEmpty()) {
            listSupplier.remove(row);
            fireTableRowsDeleted(0, listSupplier.size());
        }

    }

    public void addCustomer(Supplier customer) {
        listSupplier.add(customer);
        fireTableRowsInserted(listSupplier.size() - 1, listSupplier.size() - 1);
    }

    public void setCustomer(int row, Supplier customer) {
        if (!listSupplier.isEmpty()) {
            listSupplier.set(row, customer);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        if (listSupplier != null) {
            listSupplier.clear();
            fireTableDataChanged();
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
