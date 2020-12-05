/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog.common;

import com.cv.inv.entity.Category;
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
public class CategoryTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTableModel.class);
    private final String[] columnNames = {"Category"};
    private List<Category> listCategory = new ArrayList<>();

    public CategoryTableModel() {
    }

    public CategoryTableModel(List<Category> listCategory) {
        this.listCategory = listCategory;
    }

    @Override
    public int getRowCount() {
        return listCategory.size();
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
        Category category = listCategory.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return category.getCatName();
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

    public List<Category> getListCategory() {
        return listCategory;
    }

    public void setListCategory(List<Category> listCategory) {
        this.listCategory = listCategory;
        fireTableDataChanged();
    }

    public Category getCategory(int row) {
        return listCategory.get(row);
    }

    public void setCategory(Category category, int row) {
        if (!listCategory.isEmpty()) {
            listCategory.set(row, category);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addCategory(Category item) {
        if (!listCategory.isEmpty()) {
            listCategory.add(item);
            fireTableRowsInserted(listCategory.size() - 1, listCategory.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
