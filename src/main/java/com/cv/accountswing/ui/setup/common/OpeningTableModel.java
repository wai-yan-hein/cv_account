/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.COAOpening;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.view.VCOAOpening;
import com.cv.accountswing.service.OpeningService;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.toedter.calendar.JDateChooser;
import java.awt.HeadlessException;
import java.text.DateFormat;
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
 * @author winswe
 */
@Component
public class OpeningTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpeningTableModel.class);
    private List<VCOAOpening> listOpening = new ArrayList();
    private String[] columnNames = {"Code", "Chart Of Account", "Person Id", "Person Name", "Dept", "Currency", "Dr-Amt", "Cr-Amt"};
    private JTable parent;
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    @Autowired
    private OpeningService opService;
    private SelectionObserver selectionObserver;
    private boolean isValid;
    private JDateChooser opDate;
    private DepartmentAutoCompleter depAutoCompleter;

    public DepartmentAutoCompleter getDepAutoCompleter() {
        return depAutoCompleter;
    }

    public void setDepAutoCompleter(DepartmentAutoCompleter depAutoCompleter) {
        this.depAutoCompleter = depAutoCompleter;
    }

    public JDateChooser getOpDate() {
        return opDate;
    }

    public void setOpDate(JDateChooser opDate) {
        this.opDate = opDate;
    }

    public boolean isIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 1 && column != 3;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 6:
                return Double.class;
            case 7:
                return Double.class;

            default:
                return String.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            VCOAOpening vgl = listOpening.get(row);

            switch (column) {
                case 0: //Id
                    return vgl.getCoaUsrCode();
                case 1: //Name
                    return vgl.getSourceAccName();
                case 2:
                    return vgl.getTraderCode();
                case 3:// trader Name
                    return vgl.getTraderName();
                case 4:
                    return vgl.getDepUsrCode();
                case 5:
                    return vgl.getCurCode();
                case 6:
                    return vgl.getDrAmt();
                case 7:
                    return vgl.getCrAmt();
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
        VCOAOpening vgl = listOpening.get(row);
        switch (column) {
            case 0:
                if (value != null) {
                    if (value instanceof ChartOfAccount) {
                        ChartOfAccount coa = (ChartOfAccount) value;
                        vgl.setCoaUsrCode(coa.getCoaCodeUsr());
                        vgl.setSourceAccId(coa.getCode());
                        vgl.setSourceAccName(coa.getCoaNameEng());
                        parent.setRowSelectionInterval(row, row);
                        parent.setColumnSelectionInterval(2, 2);
                    }
                }
                break;
            case 2:
                if (value != null) {
                    if (value instanceof Trader) {
                        Trader t = (Trader) value;
                        vgl.setTraderCode(t.getCode());
                        vgl.setTraderName(t.getTraderName());
                        parent.setRowSelectionInterval(row, row);
                        parent.setColumnSelectionInterval(3, 3);
                    }
                }
                break;
            case 4:
                if (value != null) {
                    if (value instanceof Department) {
                        Department dep = (Department) value;
                        vgl.setDepCode(dep.getDeptCode());
                        vgl.setDepUsrCode(dep.getUsrCode());
                        parent.setRowSelectionInterval(row, row);
                        parent.setColumnSelectionInterval(5, 5);
                    }
                }
                break;
            case 5:
                if (value != null) {
                    if (value instanceof Currency) {
                        Currency cur = (Currency) value;
                        vgl.setCurCode(cur.getKey().getCode());
                        parent.setRowSelectionInterval(row, row);
                        parent.setColumnSelectionInterval(6, 6);
                    }
                }
                break;
            case 6:
                if (value != null) {
                    vgl.setDrAmt(Util1.getDouble(value));
                    vgl.setCrAmt(0.0);
                    //save(vgl, row);
                }
                break;
            case 7:
                if (value != null) {
                    vgl.setCrAmt(Util1.getDouble(value));
                    vgl.setDrAmt(0.0);
                    //save(vgl, row);
                }
                break;

        }
        if (isValidEntry(vgl, column, row)) {
            addNewRow();
        }
        fireTableRowsUpdated(row, row);
        selectionObserver.selected("CAL-TOTAL", "-");
        parent.requestFocusInWindow();
    }

    private void save(VCOAOpening vgl, int row) {
        String strVGL = gson.toJson(vgl);
        COAOpening op = gson.fromJson(strVGL, COAOpening.class);
        op.setUserCode(Global.loginUser.getAppUserCode());
        op.setCreatedDate(Util1.getTodayDate());
        try {
            COAOpening save = opService.save(op);
            if (save != null) {
                VCOAOpening saveOpening = listOpening.get(row);
                saveOpening.setOpId(save.getOpId());
                addNewRow();
                parent.setColumnSelectionInterval(6, 6);
                selectionObserver.selected("CAL-TOTAL", "-");
            }
        } catch (HeadlessException ex) {
            LOGGER.error("Save Opening :" + ex.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage(), "Save Opening", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEntry(VCOAOpening v, int col, int row) {
        isValid = true;
        if (Util1.isNull(v.getSourceAccId())) {
            if (col > 1) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Account.");
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(0, 0);
            }
            isValid = false;
        } else if (Util1.isNull(v.getDepCode())) {
            if (col > 5) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Department.");
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(4, 4);
            }
            isValid = false;
        } else if (Util1.isNull(v.getCurCode())) {
            isValid = false;
        }
        return isValid;
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            VCOAOpening vGl = new VCOAOpening();
            vGl.setOpDate(Util1.getTodayDate());
            vGl.setCompCode(Global.compCode);
            vGl.setUserCode(Global.loginUser.getAppUserCode());
            vGl.setCurCode(Global.defalutCurrency.getKey().getCode());
            vGl.setOpDate(opDate.getDate());
            vGl.setDepCode(depAutoCompleter.getDepartment() == null ? null : depAutoCompleter.getDepartment().getDeptCode());
            vGl.setDepUsrCode(depAutoCompleter.getDepartment() == null ? null : depAutoCompleter.getDepartment().getUsrCode());
            listOpening.add(vGl);
            fireTableRowsInserted(listOpening.size() - 1, listOpening.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listOpening.isEmpty() || listOpening == null) {
            status = true;
        } else {
            VCOAOpening vgl = listOpening.get(listOpening.size() - 1);
            if (Util1.isNull(vgl.getDepCode())
                    || Util1.isNull(vgl.getSourceAccId())) {
                status = false;
            }
        }

        return status;
    }

    @Override
    public int getRowCount() {
        if (listOpening == null) {
            return 0;
        }
        return listOpening.size();
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

    public List<VCOAOpening> getListOpening() {
        return listOpening;
    }

    public void setListOpening(List<VCOAOpening> listOpening) {
        this.listOpening = listOpening;
        fireTableDataChanged();
    }

    public VCOAOpening getOpening(int row) {
        return listOpening.get(row);
    }

    public void deleteOpening(int row) {
        if (!listOpening.isEmpty()) {
            listOpening.remove(row);
            fireTableRowsDeleted(0, listOpening.size());
        }

    }

    public void addOpening(VCOAOpening vgl) {
        listOpening.add(vgl);
        fireTableRowsInserted(listOpening.size() - 1, listOpening.size() - 1);
    }

    public void setOpening(int row, VCOAOpening vgl) {
        if (!listOpening.isEmpty()) {
            listOpening.set(row, vgl);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        if (listOpening != null) {
            listOpening.clear();
            fireTableDataChanged();
        }
    }

}
