/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.SeqTableService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author MyoGyi
 */
@Component
public class COAGroupTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(COAGroupTableModel.class);
    private List<ChartOfAccount> listCOA = new ArrayList();
    String status = "NEW";
    //String  userCode=Global.loginUser.getAppUserCode();
    String[] columnNames = {"System Code", "User Code", "Name", "Active"};
    private JTable parent;
    @Autowired
    private COAService coaService;
    @Autowired
    SeqTableService seqService;
    @Autowired
    SystemPropertyService spService;
    private String coaHeadCode;

    public void setCoaHeadCode(String coaHeadCode) {
        this.coaHeadCode = coaHeadCode;
    }

    @Override
    public int getRowCount() {
        if (listCOA == null) {
            return 0;
        }
        return listCOA.size();
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
    public Object getValueAt(int row, int column) {
        try {
            ChartOfAccount coa = listCOA.get(row);

            switch (column) {
                case 0: //Code
                    return coa.getCode();
                case 1: //User Code
                    return coa.getCoaCodeUsr();
                case 2://Name
                    return coa.getCoaNameEng();
                case 3://Active
                    return coa.isActive();

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
        if (listCOA == null) {
            return;
        }

        if (listCOA.isEmpty()) {
            return;
        }
        try {

            ChartOfAccount coa = listCOA.get(row);
            if (coa.getCode() != null) {
                status = "EDIT";
            }
            switch (column) {
                case 0:
                    break;
                case 1://user code
                    if (value != null) {
                        coa.setCoaCodeUsr(value.toString());
                        parent.setColumnSelectionInterval(2, 2);
                    }
                    break;

                case 2:
                    if (value != null) {
                        coa.setCoaNameEng(value.toString());
                    }
                    break;
                case 3:
                    if (value != null) {
                        Boolean active = (Boolean) value;
                        coa.setActive(active);
                    } else {
                        coa.setActive(Boolean.TRUE);
                    }

                    break;
                default:
                    break;

            }
            save(coa, row);
            parent.requestFocus();
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

    }

    private void save(ChartOfAccount coa, int row) {
        coa.setCoaParent(coaHeadCode);
        coa.setCompCode(Global.compCode);
        coa.setActive(Boolean.TRUE);
        coa.setMacId(Global.machineId);
        if (isValidCOA(coa, Global.compCode, Global.loginUser.getAppUserCode())) {
            ChartOfAccount save = coaService.save(coa);
            if (save.getCode() != null) {
                Global.listCOA.add(save);
                addEmptyRow();
                parent.setRowSelectionInterval(row + 1, row + 1);
                parent.setColumnSelectionInterval(1, 1);
            }

        }
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
            case 3:
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column > 0;

    }

    public ChartOfAccount getChartOfAccount(int row) {
        return listCOA.get(row);
    }

    public void addCoa(ChartOfAccount coa) {
        listCOA.add(coa);
        fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
    }

    public void setCoaGroup(int row, ChartOfAccount coa) {
        if (!listCOA.isEmpty()) {
            listCOA.set(row, coa);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public List<ChartOfAccount> getListCOA() {
        return listCOA;
    }

    public void setListCOA(List<ChartOfAccount> listCOA) {
        this.listCOA = listCOA;
        fireTableDataChanged();
    }

    public boolean isValidCOA(ChartOfAccount coa, String compCode,
            String userCode) {
        boolean isTrue = true;

        if (Util1.isNull(coa.getCoaNameEng(), "-").equals("-")) {
            isTrue = false;
            //JOptionPane.showMessageDialog(Global.parentForm, "Invalid COA Name");

        } else if (coa.getCoaParent() == null) {
            isTrue = false;
            //JOptionPane.showMessageDialog(Global.parentForm, "Please Select Parent.");

        } else {
            if (coa.getCode() == null) {
                if (!Util1.isNull(coa.getCoaCodeUsr(), "-").equals("-")) {
                    List<ChartOfAccount> listSearch = coaService.search("-", "-", compCode,
                            "-", "-", "-", coa.getCoaCodeUsr());

                    if (listSearch != null) {
                        if (listSearch.size() > 0) {
                            isTrue = false;
                            JOptionPane.showMessageDialog(Global.parentForm, "Duplicate Account Code");
                        }
                    }
                    coa.setCompCode(compCode);
                    coa.setCreatedBy(userCode);
                    coa.setCreatedDate(Util1.getTodayDate());
                    coa.setActive(true);
                    if (Util1.isNull(coa.getOption(), "-").equals("-")) {
                        coa.setOption("USR");
                    }
                }
            } else {
                coa.setModifiedBy(userCode);
                coa.setModifiedDate(Util1.getTodayDate());
            }

            if (coa.getCoaParent() != null) {
                if (!coa.getCoaParent().isEmpty()) {
                    ChartOfAccount coa1 = coaService.findById(coa.getCoaParent());
                    coa.setCoaLevel(coa1.getCoaLevel() + 1);
                    coa.setParentUsrDesp(coa1.getCoaNameEng());
                }
            } else {
                coa.setCoaLevel(1);
            }
        }

        return isTrue;
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (!listCOA.isEmpty()) {
            ChartOfAccount coa = listCOA.get(listCOA.size() - 1);
            if (coa.getCode() != null) {
                status = true;
            }
        }
        return status;
    }

    public void addEmptyRow() {
        if (listCOA != null) {
            if (hasEmptyRow()) {
                ChartOfAccount coa = new ChartOfAccount();
                listCOA.add(coa);
                fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
            }

        }

    }

    public void clear() {
        if (listCOA != null) {
            listCOA.clear();
            fireTableDataChanged();
        }
    }
}
