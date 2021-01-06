/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.ProjectCOAMapping;
import com.cv.accountswing.entity.view.VProjectCOAMapping;
import com.cv.accountswing.service.ProjectCOAMappingService;
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
public class ChartOfAmountTabelModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartOfAmountTabelModel.class);
    private List<VProjectCOAMapping> listProject = new ArrayList();
    private String[] columnNames = {"Code", "Name", "Active"};
    private String projectId;
    @Autowired
    private ProjectCOAMappingService pcoamService;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

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
            VProjectCOAMapping project = listProject.get(row);

            switch (column) {
                case 0: //Id
                    return project.getCoaCode();
                case 1: //Name
                    return project.getCoaNameEng();

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
        ChartOfAccount coa = null;
        if (!listProject.isEmpty()) {
            VProjectCOAMapping vCOA = listProject.get(row);
            switch (column) {
                case 0:
                    if (value != null) {
                        if (value instanceof ChartOfAccount) {
                            coa = (ChartOfAccount) value;
                            vCOA.setCoaCode(coa.getCode());
                            vCOA.setCoaNameEng(coa.getCoaNameEng());
                            addNewRow();
                            saveCOA(coa);
                        }
                    }
                    break;
            }
        }

    }

    private void saveCOA(ChartOfAccount coa) {
        if (isValidCOAAdd(Util1.getString(projectId), coa.getCode())) {
            ProjectCOAMapping pcm = new ProjectCOAMapping();
            pcm.setProjectId(projectId);
            pcm.setCoaCode(coa.getCode());
            ProjectCOAMapping save = pcoamService.save(pcm);
            if (save != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Chart of Amount Added.");
            }
        }
    }

    private boolean isValidCOAAdd(String projectId, String coaCode) {
        boolean status = true;
        List listPCM = pcoamService.search(projectId, coaCode);
        if (listPCM != null) {
            if (!listPCM.isEmpty()) {
                status = false;
                JOptionPane.showMessageDialog(Global.parentForm, "Duplicate chart of account.");

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

    public List<VProjectCOAMapping> getlistProject() {
        return listProject;
    }

    public void setlistProject(List<VProjectCOAMapping> listProject) {
        this.listProject = listProject;
        fireTableDataChanged();
    }

    public VProjectCOAMapping getProject(int row) {
        return listProject.get(row);
    }

    public void deleteProject(int row) {
        if (!listProject.isEmpty()) {
            listProject.remove(row);
            fireTableRowsDeleted(0, listProject.size());
        }

    }

    public void addProject(VProjectCOAMapping project) {
        listProject.add(project);
        fireTableRowsInserted(listProject.size() - 1, listProject.size() - 1);
    }

    public void setProject(int row, VProjectCOAMapping project) {
        if (!listProject.isEmpty()) {
            listProject.set(row, project);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addNewRow() {
        if (!listProject.isEmpty()) {
            listProject.add(new VProjectCOAMapping());
        }
    }

}
