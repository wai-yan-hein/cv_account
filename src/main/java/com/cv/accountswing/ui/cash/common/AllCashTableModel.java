/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.ReloadData;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.view.VDescription;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.entity.view.VRef;
import com.cv.accountswing.service.GlService;
import com.cv.accountswing.service.MessagingService;
import com.cv.accountswing.service.RoleStatusService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.HeadlessException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class AllCashTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(AllCashTableModel.class);
    private List<VGl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Dept:", "Description", "Ref :", "No :", "Person", "Account", "Curr", "Cash In / Dr", "Cash Out / Cr"};
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    @Autowired
    private RoleStatusService statusService;
    @Autowired
    private GlService glService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private TaskExecutor taskExecutor;
    private String sourceAccId;
    private JTable parent;
    private SelectionObserver selectionObserver;
    private Trader trader;
    private ReloadData reloadData;
    private DateAutoCompleter dateAutoCompleter;
    private JFormattedTextField txtDate;

    public JFormattedTextField getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(JFormattedTextField txtDate) {
        this.txtDate = txtDate;
    }

    public DateAutoCompleter getDateAutoCompleter() {
        return dateAutoCompleter;
    }

    public void setDateAutoCompleter(DateAutoCompleter dateAutoCompleter) {
        this.dateAutoCompleter = dateAutoCompleter;
    }

    public void setReloadData(ReloadData reloadData) {
        this.reloadData = reloadData;
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
        return vgl.getVouNo() == null;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 8:
                return Double.class;
            case 9:
                return Double.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            if (!listVGl.isEmpty()) {
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
                    case 4://Ref no
                        return vgi.getRefNo();
                    case 5://Person
                        return vgi.getTraderName();
                    case 6:
                        //Account
                        return vgi.getAccName();
                    case 7:
                        return vgi.getFromCurId();
                    case 8:
                        if (vgi.getDrAmt() != null) {
                            if (vgi.getDrAmt() == 0) {
                                return null;
                            } else {
                                return vgi.getDrAmt();
                            }
                        } else {
                            return vgi.getDrAmt();
                        }
                    case 9:
                        if (vgi.getCrAmt() != null) {
                            if (vgi.getCrAmt() == 0) {
                                return null;
                            } else {
                                return vgi.getCrAmt();
                            }
                        } else {
                            return vgi.getCrAmt();
                        }
                    default:
                        return null;
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
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
                            vgl.setDeptId(dep.getDeptCode());
                        }
                    }
                    parent.setColumnSelectionInterval(2, 2);
                    break;
                case 2:
                    if (value != null) {
                        if (value instanceof VDescription) {
                            VDescription autoText = (VDescription) value;
                            vgl.setDescription(autoText.getDescription());
                        } else {
                            vgl.setDescription(value.toString());
                            if (vgl.getDescription().length() > 0) {
                                Global.listDesp.add(new VDescription(vgl.getDescription()));
                            }
                        }

                    }
                    parent.setColumnSelectionInterval(3, 3);

                    break;
                case 3:
                    if (value != null) {
                        if (value instanceof VRef) {
                            VRef autoText = (VRef) value;
                            vgl.setReference(autoText.getReference());
                        } else {
                            vgl.setReference(value.toString());
                            if (vgl.getReference().length() > 0) {
                                Global.listRef.add(new VRef(vgl.getReference()));
                            }
                        }

                    }
                    parent.setColumnSelectionInterval(4, 4);
                    break;
                case 4:
                    if (value != null) {
                        vgl.setRefNo(value.toString());
                    }
                    break;
                case 5:
                    if (value != null) {
                        if (value instanceof Trader) {
                            trader = (Trader) value;
                        }
                        if (trader != null) {
                            vgl.setTraderCode(trader.getCode());
                            vgl.setTraderName(trader.getTraderName());
                            if (trader.getAccount() != null) {
                                vgl.setAccountId(trader.getAccount().getCode());
                                vgl.setAccName(trader.getAccount().getCoaNameEng());
                                parent.setColumnSelectionInterval(7, 7);
                            } else {
                                parent.setColumnSelectionInterval(5, 5);
                            }
                        } else {
                            parent.setColumnSelectionInterval(column, column);
                        }
                    }
                    break;
                case 6:
                    if (value != null) {
                        if (value instanceof ChartOfAccount) {
                            ChartOfAccount coa = (ChartOfAccount) value;
                            vgl.setAccountId(coa.getCode());
                            vgl.setAccName(coa.getCoaNameEng());
                            vgl.setTraderCode(null);
                            vgl.setTraderName(null);
                            if (Util1.isNull(vgl.getFromCurId())) {
                                parent.setColumnSelectionInterval(7, 7);
                            } else {
                                parent.setColumnSelectionInterval(8, 8);
                            }

                        }

                    }

                    break;
                case 7:
                    if (value != null) {
                        if (value instanceof Currency) {
                            Currency curr = (Currency) value;
                            String cuCode = curr.getKey().getCode();
                            vgl.setFromCurId(cuCode);
                        }
                    }
                    parent.setColumnSelectionInterval(7, 7);
                    break;
                case 8:
                    if (value != null) {
                        vgl.setDrAmt(Util1.getDouble(value));
                    }
                    break;
                case 9:
                    if (value != null) {
                        vgl.setCrAmt(Util1.getDouble(value));
                    }
                    break;
            }
            save(vgl, row, column);
            parent.requestFocus();

        } catch (HeadlessException e) {
            log.info("setValueAt : " + e.getMessage());
        }
    }

    private void save(VGl vgl, int row, int column) {
        if (isValidEntry(vgl, row, column)) {
            try {
                String strVGL = gson.toJson(vgl);
                Gl gl = gson.fromJson(strVGL, Gl.class);
                gl.setSourceAcId(sourceAccId);
                if (Util1.isNull(gl.getGlCode())) {
                    gl.setMacId(Global.machineId);
                    gl.setCompCode(Global.compCode);
                    gl.setCreatedBy(Global.loginUser.getAppUserCode());
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setTranSource("CB");
                } else {
                    gl.setModifyBy(Global.loginUser.getAppUserCode());
                }
                Gl glSave = glService.save(gl);
                if (glSave != null) {
                    VGl saveVGl = listVGl.get(row);
                    saveVGl.setGlCode(glSave.getGlCode());
                    saveVGl.setCreatedBy(glSave.getCreatedBy());
                    saveVGl.setModifyBy(glSave.getModifyBy());
                    saveVGl.setCompCode(glSave.getCompCode());
                    saveVGl.setMacId(glSave.getMacId());
                    saveVGl.setCreatedDate(glSave.getCreatedDate());
                    saveVGl.setTranSource(gl.getTranSource());
                    addNewRow();
                    row = parent.getSelectedRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(0, 0);
                    selectionObserver.selected("CAL-TOTAL", "-");
                    //send to inventory
                    //sendPaymentToInv(glSave);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                log.error("Save Gl :" + ex.getMessage());
            }
        }

    }

    private boolean isValidEntry(VGl vgl, int row, int column) {
        boolean status = true;
        if (vgl.getAccountId() == null) {
            status = false;
            if (column > 5) {
                JOptionPane.showMessageDialog(Global.parentForm, "Account missing.");
                parent.setColumnSelectionInterval(5, 5);
                parent.setRowSelectionInterval(row, row);
            }
        }
        if (Util1.getDouble(vgl.getDrAmt()) + Util1.getDouble(vgl.getCrAmt()) <= 0) {
            status = false;
        }
        if (Util1.isNull(vgl.getDeptId())) {
            status = false;
            if (column > 1) {
                JOptionPane.showMessageDialog(Global.parentForm, "Missing Department.");
                parent.setColumnSelectionInterval(1, 1);
                parent.setRowSelectionInterval(row, row);
            }
        }
        if (!Util1.isNull(vgl.getGlCode())) {
            /*if (statusService.checkPermission(Global.roleCode, Global.CB_DEL_USR_KEY)) {
            status = vgl.getCreatedBy().equals(Global.loginUser.getAppUserCode());
            } else {
            status = statusService.checkPermission(Global.roleCode, Global.CB_DEL_KEY);
            }*/
        }
        return status;
    }

    @Override
    public int getRowCount() {
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
                log.error("Delete GL :" + ex.getMessage());
            }

        }

    }

    private void sendDeletePaymentToInv(Trader trader, String glId) {
        if (trader != null) {
            if (trader.getAppShortName() != null) {
                if (trader.getAppShortName().equals("INVENTORY")) {
                    messagingService.sendDeletePaymentToInv(glId);
                }
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

    public void addNewRow() {
        if (hasEmptyRow()) {
            VGl vGl = new VGl();
            vGl.setFromCurId(Global.defalutCurrency.getKey().getCode());
            if (Global.defaultDepartment != null) {
                vGl.setDeptId(Global.defaultDepartment.getDeptCode());
                vGl.setDeptUsrCode(Global.defaultDepartment.getUsrCode());
            }
            vGl.setGlDate(getDate());
            listVGl.add(vGl);
            fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
        }
    }

    private Date getDate() {
        Date glDate = null;
        if (listVGl.size() > 0) {
            glDate = listVGl.get(listVGl.size() - 1).getGlDate();
        } else {
            glDate = Util1.getTodayDate();
        }
        return glDate;
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

    public void copyRow() {
        try {
            int selectRow = parent.convertRowIndexToModel(parent.getSelectedRow());
            int column = parent.getSelectedColumn();
            if (listVGl != null) {
                VGl vgl = listVGl.get(selectRow - 1);
                if (vgl.getGlCode() != null) {
                    Date glDate = vgl.getGlDate();
                    VGl selGL = listVGl.get(selectRow);
                    switch (column) {
                        case 0:
                            selGL.setGlDate(glDate);
                            break;
                        case 1:
                            selGL.setDeptId(vgl.getDeptId());
                            selGL.setDeptName(vgl.getDeptName());
                            selGL.setDeptUsrCode(vgl.getDeptUsrCode());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 2);
                            break;
                        case 2:
                            selGL.setDescription(vgl.getDescription());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 3);
                            break;
                        case 3:
                            selGL.setReference(vgl.getReference());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 4);
                            break;
                        case 4:
                            selGL.setRefNo(vgl.getRefNo());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 5);
                            break;
                        case 5:
                            selGL.setTraderCode(vgl.getTraderCode());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 7);
                            break;
                        case 6:
                            selGL.setAccountId(vgl.getAccountId());
                            selGL.setAccName(vgl.getAccName());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 7);
                            break;
                        case 7:
                            selGL.setFromCurId(vgl.getFromCurId());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 8);
                            break;
                        case 8:
                            selGL.setDrAmt(vgl.getDrAmt());
                            fireTableCellUpdated(selectRow, column);
                            break;
                        case 9:
                            selGL.setDrAmt(vgl.getDrAmt());
                            fireTableCellUpdated(selectRow, column);
                            break;
                    }
                    if (parent.getCellEditor() != null) {
                        parent.getCellEditor().stopCellEditing();
                    }
                }
            }
        } catch (Exception e) {
            log.error("copyRow " + e.getMessage());
        }
    }

    private void selectTable(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
        }
    }

    private Trader getTrader(String appTraderCode) {
        Trader t = null;
        String tmpCode = "CUS".concat(appTraderCode);
        List<Trader> firstSearch = traderService.searchTrader("-", "-", "-", "-", "-", "-", tmpCode);
        if (!firstSearch.isEmpty()) {
            t = firstSearch.get(0);
        } else {
            List<Trader> secondSearch = traderService.searchTrader("-", "-", "-", "-", "-", "-", appTraderCode);
            if (!secondSearch.isEmpty()) {
                t = secondSearch.get(0);
            }
        }
        return t;
    }

}
