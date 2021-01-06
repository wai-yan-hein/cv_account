/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.ProjectTraderMapping;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.view.VProjectTraderMapping;
import com.cv.accountswing.service.ProjectTraderMappingService;
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
public class ProjectTraderTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTraderTableModel.class);
    private List<VProjectTraderMapping> listProject = new ArrayList();
    private String[] columnNames = {"Code", "Name"};
    private String projectId;
    @Autowired
    private ProjectTraderMappingService projectTraderMappingService;

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
            VProjectTraderMapping project = listProject.get(row);

            switch (column) {
                case 0: //Id
                    return project.getTraderCode();
                case 1: //Name
                    return project.getTraderName();
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
        Trader trader = null;
        if (!listProject.isEmpty()) {
            VProjectTraderMapping vPTM = listProject.get(row);
            switch (column) {
                case 0:
                    if (value != null) {
                        if (value instanceof Trader) {
                            trader = (Trader) value;
                            vPTM.setTraderCode(trader.getUserCode());
                            vPTM.setTraderName(trader.getTraderName());

                        }
                    }
                    break;
            }

        }
        addNewRow();
        saveTrader(trader);

    }

    private void saveTrader(Trader trader) {
        String traderId = trader.getCode();
        if (isValidTrader(projectId, trader.getUserCode())) {
            ProjectTraderMapping ptm = new ProjectTraderMapping();
            ptm.setProjectCode(projectId);
            ptm.setProjectCode(traderId);
            ProjectTraderMapping save = projectTraderMappingService.save(ptm);
            if (save != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Person Added.");

            }
        }
    }

    private boolean isValidTrader(String projectId, String traderId) {
        boolean status = true;
        List listPTM = projectTraderMappingService.search(projectId, traderId);
        if (listPTM != null) {
            if (!listPTM.isEmpty()) {
                status = false;
                JOptionPane.showMessageDialog(Global.parentForm, "Duplicate Person");
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

    public List<VProjectTraderMapping> getlistProject() {
        return listProject;
    }

    public void setlistProject(List<VProjectTraderMapping> listProject) {
        this.listProject = listProject;
        fireTableDataChanged();
    }

    public VProjectTraderMapping getProject(int row) {
        return listProject.get(row);
    }

    public void deleteProject(int row) {
        if (!listProject.isEmpty()) {
            listProject.remove(row);
            fireTableRowsDeleted(0, listProject.size());
        }

    }

    public void addProject(VProjectTraderMapping project) {
        listProject.add(project);
        fireTableRowsInserted(listProject.size() - 1, listProject.size() - 1);
    }

    public void setProject(int row, VProjectTraderMapping project) {
        if (!listProject.isEmpty()) {
            listProject.set(row, project);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addNewRow() {
        if (!listProject.isEmpty()) {
            listProject.add(new VProjectTraderMapping());
        }
    }
}
