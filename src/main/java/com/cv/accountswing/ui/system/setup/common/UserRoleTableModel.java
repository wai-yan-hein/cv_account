/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.common;

import com.cv.accountswing.entity.UserRole;
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
public class UserRoleTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleTableModel.class);
    private List<UserRole> listRole = new ArrayList();
    private String[] columnNames = {"Role Name"};

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
        return String.class;

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            UserRole user = listRole.get(row);

            switch (column) {
                case 0: //Id
                    return user.getRoleName();
               
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
        if (listRole == null) {
            return 0;
        }
        return listRole.size();
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

    public List<UserRole> getListRole() {
        return listRole;
    }

    public void setListRole(List<UserRole> listRole) {
        this.listRole = listRole;
        fireTableDataChanged();
    }

   
  

    public UserRole getRole(int row) {
        return listRole.get(row);
    }

    public void deleteRole(int row) {
        if (!listRole.isEmpty()) {
            listRole.remove(row);
            fireTableRowsDeleted(0, listRole.size());
        }

    }

    public void addRole(UserRole user) {
        listRole.add(user);
        fireTableRowsInserted(listRole.size() - 1, listRole.size() - 1);
    }

    public void setRole(int row, UserRole user) {
        if (!listRole.isEmpty()) {
            listRole.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

}
