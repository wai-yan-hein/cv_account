/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.entity.Project;
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
public class ProjectTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTableModel.class);
    private List<Project> listProject = new ArrayList();
    private String[] columnNames = {"Code", "Name", "Active"};

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
                return Boolean.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Project project = listProject.get(row);

            switch (column) {
                case 0: //Id
                    return project.getProjectCode();
                case 1: //Name
                    return project.getProjectName();
                case 2:
                    return project.getProjectStatus();
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

    public List<Project> getlistProject() {
        return listProject;
    }

    public void setlistProject(List<Project> listProject) {
        this.listProject = listProject;
        fireTableDataChanged();
    }

    public Project getProject(int row) {
        return listProject.get(row);
    }

    public void deleteProject(int row) {
        if (!listProject.isEmpty()) {
            listProject.remove(row);
            fireTableRowsDeleted(0, listProject.size());
        }

    }

    public void addProject(Project project) {
        listProject.add(project);
        fireTableRowsInserted(listProject.size() - 1, listProject.size() - 1);
    }

    public void setProject(int row, Project project) {
        if (!listProject.isEmpty()) {
            listProject.set(row, project);
            fireTableRowsUpdated(row, row);
        }
    }

}
