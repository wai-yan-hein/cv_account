/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.GlService;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class SalePurchaseTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalePurchaseTableModel.class);
    private List<VGl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Dept:", "Description", "Ref", "Person", "Account", "Curr", "Total-Amt"};
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    @Autowired
    private GlService glService;
    private String sourceAccId;
    private JTable parent;
    private SelectionObserver selectionObserver;
    private String sourceName;

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        VGl vgl = listVGl.get(row);
        Integer splidId = vgl.getSplitId();
        if (splidId != null) {
            if (splidId == 2 || splidId == 3 || splidId == 5 || splidId == 6) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 7:
                return Double.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            VGl vgi = listVGl.get(row);

            switch (column) {
                case 0: //Id
                    if (vgi.getGlDate() != null) {
                        return Util1.toDateStr(vgi.getGlDate(), "dd/MM/yyyy");
                    }
                case 1: //Department
                    return vgi.getDeptUsrCode();
                case 2://Desp
                    return vgi.getDescription();
                case 3://Ref
                    return vgi.getReference();
                case 4://Person
                    return vgi.getTraderName();
                case 5:
                    //Account
                    return vgi.getAccName();
                case 6:
                    return vgi.getFromCurId();
                case 7:
                    return sourceName.equals("Sale") ? vgi.getCrAmt() : vgi.getDrAmt();

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
        VGl vgl = listVGl.get(row);
        switch (column) {
            case 0:
                if (value != null) {
                    if (Util1.isValidDateFormat(value.toString(), "dd/MM/yyyy")) {
                        vgl.setGlDate(Util1.toDate(value, "dd/MM/yyyy"));
                    } else {
                        if (value.toString().length() == 8) {
                            String toFormatDate = Util1.toFormatDate(value.toString());
                            vgl.setGlDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        } else {
                            vgl.setGlDate(Util1.getTodayDate());
                            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date");
                        }
                    }
                }
                parent.setColumnSelectionInterval(1, 1);
                break;
            case 1:
                if (value != null) {
                    if (value instanceof Department) {
                        Department dep = (Department) value;
                        vgl.setDeptUsrCode(dep.getUsrCode());
                        vgl.setDeptName(dep.getDeptName());
                        vgl.setDeptId(dep.getDeptCode());
                    }
                }
                parent.setColumnSelectionInterval(2, 2);
                break;
            case 2:
                if (value != null) {
                    vgl.setDescription(value.toString());
                }
                parent.setColumnSelectionInterval(3, 3);

                break;
            case 3:
                if (value != null) {
                    vgl.setReference(value.toString());
                }
                parent.setColumnSelectionInterval(4, 4);
                break;
            case 4:
                if (value != null) {
                    if (value instanceof Trader) {
                        Trader trader = (Trader) value;
                        vgl.setTraderCode(trader.getCode());
                        vgl.setTraderName(trader.getTraderName());
                        ChartOfAccount account = trader.getAccount();
                        if (account != null) {
                            vgl.setAccountId(account.getCode());
                            vgl.setAccName(account.getCoaNameEng());
                            parent.setColumnSelectionInterval(7, 7);
                        } else {
                            parent.setColumnSelectionInterval(5, 5);
                        }
                    }
                }
                break;
            case 5:
                if (value != null) {
                    if (value instanceof ChartOfAccount) {
                        ChartOfAccount coa = (ChartOfAccount) value;
                        vgl.setAccountId(coa.getCode());
                        vgl.setAccName(coa.getCoaNameEng());

                    }
                }
                parent.setColumnSelectionInterval(6, 6);
                break;
            case 6:
                if (value != null) {
                    if (value instanceof Currency) {
                        Currency curr = (Currency) value;
                        String cuCode = curr.getKey().getCode();
                        vgl.setFromCurId(cuCode);
                    }
                }
                parent.setColumnSelectionInterval(7, 7);
                break;
            case 7:
                if (value != null) {
                    if (sourceName.equals("Sale")) {
                        vgl.setCrAmt(Util1.getDouble(value));
                    } else {
                        vgl.setDrAmt(Util1.getDouble(value));
                    }
                }
                break;
        }
        save(vgl, row);
        parent.requestFocus();

    }

    private void save(VGl vgl, int row) {
        if (isValidEntry(vgl, row, row)) {
            String strVGL = gson.toJson(vgl);
            Gl gl = gson.fromJson(strVGL, Gl.class);
            gl.setSourceAcId(sourceAccId);
            if (Util1.isNull(gl.getGlCode())) {
                gl.setMacId(Global.machineId);
                gl.setCompCode(Global.compCode);
                gl.setCreatedBy(Global.loginUser.getAppUserCode());
                gl.setCreatedDate(Util1.getTodayDate());
            } else {
                gl.setModifyBy(Global.loginUser.getAppUserCode());
            }
            try {
                Gl glSave = glService.save(gl);
                if (glSave != null) {
                    VGl saveVGl = listVGl.get(row);
                    saveVGl.setGlCode(glSave.getGlCode());
                    saveVGl.setCreatedBy(glSave.getCreatedBy());
                    saveVGl.setModifyBy(glSave.getModifyBy());
                    saveVGl.setCompCode(glSave.getCompCode());
                    saveVGl.setMacId(glSave.getMacId());
                    saveVGl.setCreatedDate(glSave.getCreatedDate());
                    addNewRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(1, 1);
                    selectionObserver.selected("CAL-TOTAL", "-");
                }
            } catch (Exception ex) {
                LOGGER.error("Save Gl :" + ex.getMessage());
            }
        }

    }

    private boolean isValidEntry(VGl vgl, int row, int column) {
        boolean status = true;
        if (Util1.isNull(vgl.getAccountId())) {
            status = false;
            if (column > 5) {
                JOptionPane.showMessageDialog(Global.parentForm, "Account missing.");
                parent.setColumnSelectionInterval(5, 5);
                parent.setRowSelectionInterval(row, row);
            }
        }
        if (Util1.getDouble(vgl.getDrAmt()) + Util1.getDouble(vgl.getCrAmt()) <= 0) {
            status = false;
            /*JOptionPane.showMessageDialog(Global.parentForm, "Dr / Cr missing.");
            parent.setColumnSelectionInterval(7, 7);
            parent.setRowSelectionInterval(row, row);*/
        }
        if (vgl.getDeptId() == null) {
            status = false;
            if (column > 1) {
                JOptionPane.showMessageDialog(Global.parentForm, "Missing Department.");
                parent.setColumnSelectionInterval(1, 1);
                parent.setRowSelectionInterval(row, row);

            }
        }

        return status;
    }

    @Override
    public int getRowCount() {
        if (listVGl == null) {
            return 0;
        }
        return listVGl.size();
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

    public List<VGl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<VGl> listVGl) {
        this.listVGl = listVGl;
        fireTableDataChanged();
    }

    public VGl getVGl(int row) {
        return listVGl.get(row);
    }

    public void deleteVGl(int row) {
        if (!listVGl.isEmpty()) {
            VGl vgl = listVGl.get(row);
            try {
                if (vgl.getGlCode() != null) {
                    String userCode = Global.loginUser.getAppUserCode();
                    int delete = glService.delete(vgl.getGlCode(), "GL-DELETE", userCode, Global.machineId);
                    if (delete == 1) {
                        listVGl.remove(row);
                        fireTableRowsDeleted(row, row);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                LOGGER.error("Delete GL :" + ex.getMessage());
            }

        }

    }

    public void addVGl(VGl vgi) {
        listVGl.add(vgi);
        fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void setVGl(int row, VGl vgi) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, vgi);
            fireTableRowsUpdated(row, row);
        }
    }

    private void addEmptyRow() {
        VGl vGl = new VGl();
        vGl.setGlDate(Util1.getTodayDate());
        vGl.setFromCurId(Global.sysProperties.get("system.default.currency"));
        listVGl.add(vGl);
        fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            VGl vGl = new VGl();
            vGl.setGlDate(Util1.getTodayDate());
            vGl.setFromCurId(Global.sysProperties.get("system.default.currency"));
            listVGl.add(vGl);
            fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listVGl.isEmpty() || listVGl == null) {
            status = true;
        } else {
            VGl vgl = listVGl.get(listVGl.size() - 1);
            if (vgl.getGlCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public int getListSize() {
        return listVGl.size();
    }

    public void setColumnName(int i, String name) {
        columnNames[i] = name;
        fireTableStructureChanged();
    }

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
        }
    }

}
