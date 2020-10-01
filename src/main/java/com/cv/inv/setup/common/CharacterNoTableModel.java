/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.common;

import com.cv.inv.entity.CharacterNo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class CharacterNoTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterNoTableModel.class);
    private String[] columnNames = {"Char", "No"};
    private List<CharacterNo> listCharNo = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listCharNo.size();
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        CharacterNo chNo = listCharNo.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return chNo.getCh();
            case 1:
                return chNo.getStrNumber();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<CharacterNo> getListCharNo() {
        return listCharNo;
    }

    public void setListCharNo(List<CharacterNo> listCharNo) {
        this.listCharNo = listCharNo;
        fireTableDataChanged();
    }

    public CharacterNo getCharacterNo(int row) {
        return listCharNo.get(row);
    }

    public void setCharacterNo(CharacterNo chNo, int row) {
        if (!listCharNo.isEmpty()) {
            listCharNo.set(row, chNo);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addCharacterNo(CharacterNo item) {
        if (!listCharNo.isEmpty()) {
            listCharNo.add(item);
            fireTableRowsInserted(listCharNo.size() - 1, listCharNo.size() - 1);
        }
    }

}
