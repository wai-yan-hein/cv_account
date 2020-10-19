/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.common;

import com.cv.inv.entity.ChargeType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class ChargeTypeTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleManTableModel.class);
    private String[] columnNames = {"Charge Type"};
    private List<ChargeType> listChargeType = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listChargeType.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            ChargeType chargeType = listChargeType.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return chargeType.getChargeTypeDesp();
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public ChargeType getChargerType(int row) {
        return listChargeType.get(row);
    }

    public void addChargeType(ChargeType chargeType) {
        if (listChargeType != null) {
            listChargeType.add(chargeType);
            fireTableRowsInserted(listChargeType.size() - 1, listChargeType.size() - 1);
        }
    }

    public void setChargeType(ChargeType chargeType, int row) {
        if (!listChargeType.isEmpty()) {
            listChargeType.set(row, chargeType);
            fireTableRowsUpdated(row, row);
        }
    }

    public void setListChargeType(List<ChargeType> listChargeType) {
        this.listChargeType = listChargeType;
        fireTableDataChanged();
    }

    public void deleteChargeType(int row) {
        if (listChargeType != null) {
            listChargeType.remove(row);
            fireTableRowsDeleted(0, listChargeType.size());
        }
    }

}
