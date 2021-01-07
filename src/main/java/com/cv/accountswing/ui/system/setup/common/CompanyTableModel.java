/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.common;

import com.cv.accountswing.entity.CompanyInfo;
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
public class CompanyTableModel extends AbstractTableModel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyTableModel.class);
    private List<CompanyInfo> listCompany = new ArrayList();
    private String[] columnNames = {"Code", "Company Name", "Parent"};
    
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
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            default:
                return Object.class;
        }
        
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        
        try {
            CompanyInfo company = listCompany.get(row);
            
            switch (column) {
                case 0: //Id
                    return company.getUserCode();
                case 1: //Name
                    return company.getName();
                case 2:
                    return company.getParent();
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
        if (listCompany == null) {
            return 0;
        }
        return listCompany.size();
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
    
    public List<CompanyInfo> getListCompany() {
        return listCompany;
    }
    
    public void setListCompany(List<CompanyInfo> listCompany) {
        this.listCompany = listCompany;
        fireTableDataChanged();
    }
    
    public CompanyInfo getCompany(int row) {
        return listCompany.get(row);
    }
    
    public void deleteCompany(int row) {
        if (!listCompany.isEmpty()) {
            listCompany.remove(row);
            fireTableRowsDeleted(0, listCompany.size());
        }
        
    }
    
    public void addCompany(CompanyInfo company) {
        if (listCompany != null) {
            listCompany.add(company);
            fireTableRowsInserted(listCompany.size() - 1, listCompany.size() - 1);
        }
    }
    
    public void setCompany(int row, CompanyInfo company) {
        if (!listCompany.isEmpty()) {
            listCompany.set(row, company);
            fireTableRowsUpdated(row, row);
        }
    }
    
}
