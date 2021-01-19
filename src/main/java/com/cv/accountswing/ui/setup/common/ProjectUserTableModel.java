/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.AppUser;
import com.cv.accountswing.entity.ProjectUserMapping;
import com.cv.accountswing.entity.view.VProjectUserMapping;
import com.cv.accountswing.service.ProjectUserMappingService;
import com.cv.accountswing.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class ProjectUserTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectUserTableModel.class);
    private List<VProjectUserMapping> listProject = new ArrayList();
    private String[] columnNames = {"Short Name", "Name"};
    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Autowired
    private ProjectUserMappingService projectUserMappingService;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            VProjectUserMapping user = listProject.get(row);

            switch (column) {
                case 0: //Id
                    return user.getUserShort();
                case 1: //Name
                    return user.getUserName();

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
        AppUser appUser = null;
        if (!listProject.isEmpty()) {
            VProjectUserMapping vPTM = listProject.get(row);
            switch (column) {
                case 0:
                    if (value != null) {
                        if (value instanceof AppUser) {
                            appUser = (AppUser) value;
                            vPTM.setUserShort(appUser.getUserShort());
                            vPTM.setUserName(appUser.getUserName());

                        }
                    }
                    break;
            }

        }
        saveUser(appUser);
        addNewRow();

    }

    private void saveUser(AppUser appUser) {
        String userCode = appUser.getUserCode();
        if (isValidUser(Util1.getString(projectId), appUser.getUserCode())) {
            ProjectUserMapping userMapping = new ProjectUserMapping();
            userMapping.setProjecCode(projectId);
            userMapping.setUserCode(userCode);
            ProjectUserMapping save = projectUserMappingService.save(userMapping);
            if (save != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "User  Added.");

            }

        }

    }

    private boolean isValidUser(String projectId, String userCode) {
        boolean status = true;
        List listPTM = projectUserMappingService.search(projectId, userCode);
        if (listPTM != null) {
            if (!listPTM.isEmpty()) {
                status = false;
                JOptionPane.showMessageDialog(Global.parentForm, "Duplicate User");
            }
        }

        return status;
    }

    @Override
    public int getRowCount() {
        if (listProject == null) {
            return 0;
        }
        return listProject.size();
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

    public List<VProjectUserMapping> getlistProject() {
        return listProject;
    }

    public void setlistProject(List<VProjectUserMapping> listProject) {
        this.listProject = listProject;
        fireTableDataChanged();
    }

    public VProjectUserMapping getProject(int row) {
        return listProject.get(row);
    }

    public void deleteProject(int row) {
        if (!listProject.isEmpty()) {
            listProject.remove(row);
            fireTableRowsDeleted(0, listProject.size());
        }

    }

    public void addProject(VProjectUserMapping user) {
        listProject.add(user);
        fireTableRowsInserted(listProject.size() - 1, listProject.size() - 1);
    }

    public void setProject(int row, VProjectUserMapping user) {
        if (!listProject.isEmpty()) {
            listProject.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addNewRow() {
        if (!listProject.isEmpty()) {
            listProject.add(new VProjectUserMapping());
        }
    }

}
