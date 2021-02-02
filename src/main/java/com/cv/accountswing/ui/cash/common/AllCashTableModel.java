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
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang.SerializationUtils;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AllCashTableModel.class);
    private List<VGl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Dept:", "Description", "Ref :", "Person", "Account", "Curr", "Cash In / Dr", "Cash Out / Cr"};
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

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
        Integer splidId = vgl.getSplitId();
        if (splidId != null) {
            if (splidId == 2 || splidId == 3 || splidId == 5 || splidId == 6) {
                return false;
            }
        }
        return column != 6;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 7:
                return Double.class;
            case 8:
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
                    if (vgi.getDrAmt() != null) {
                        if (vgi.getDrAmt() == 0) {
                            return null;
                        } else {
                            return vgi.getDrAmt();
                        }
                    } else {
                        return vgi.getDrAmt();
                    }
                case 8:
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
                        if (!vgl.getDescription().isEmpty()) {
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
                        if (!vgl.getReference().isEmpty()) {
                            Global.listRef.add(new VRef(vgl.getReference()));
                        }
                    }

                }
                parent.setColumnSelectionInterval(4, 4);
                break;
            case 4:
                if (value != null) {
                    if (value instanceof Trader) {
                        trader = (Trader) value;
                    } else {
                        trader = getTrader(String.valueOf(value));
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
            case 5:
                if (value != null) {
                    if (value instanceof ChartOfAccount) {
                        ChartOfAccount coa = (ChartOfAccount) value;
                        vgl.setAccountId(coa.getCode());
                        vgl.setAccName(coa.getCoaNameEng());

                    }
                }
                parent.setColumnSelectionInterval(7, 7);
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
                    vgl.setDrAmt(Util1.getFloat(value));
                }
                break;
            case 8:
                if (value != null) {
                    vgl.setCrAmt(Util1.getFloat(value));
                }
                break;
        }
        save(vgl, row, column);
        parent.requestFocus();

    }

    private void save(VGl vgl, int row, int column) {
        if (isValidEntry(vgl, row, column)) {
            vgl.setSourceAcId(sourceAccId);
            vgl.setCreatedBy(Global.loginUser.getAppUserCode());
            String strVGL = gson.toJson(vgl);
            Gl gl = gson.fromJson(strVGL, Gl.class);
            if (gl.getGlCode() == null) {
                gl.setCreatedBy(Global.loginUser.getAppUserCode());
                gl.setCreatedDate(Util1.getTodayDate());
                gl.setMacId(Global.machineId);
                gl.setCompCode(Global.compCode);
            } else {
                gl.setModifyBy(Global.loginUser.getAppUserCode());
            }
            try {
                Gl glSave = glService.save(gl);
                if (glSave != null) {
                    VGl saveVGl = listVGl.get(row);
                    saveVGl.setGlCode(glSave.getGlCode());
                    addNewRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(1, 1);
                    selectionObserver.selected("CAL-TOTAL", "-");
                    //send to inventory
                    sendPaymentToInv(glSave);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                LOGGER.error("Save Gl :" + ex.getMessage());
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

    private void sendPaymentToInv(Gl gl) {
        taskExecutor.execute(() -> {
            try {
                if (Global.useActiveMQ) {
                    if (gl.getTraderCode() != null) {
                        Trader fTrader = traderService.findById(gl.getTraderCode());
                        if (fTrader != null) {

                            if (fTrader.getAppShortName() != null) {
                                if (fTrader.getAppShortName().equals("INVENTORY")) {
                                    //Need to sent to inventory
                                    messagingService.sendPaymentToInv(gl, fTrader);
                                    if (reloadData != null) {
                                        reloadData.reload("SENT-INV", "Suceesfully Sent to Inventory");
                                    }
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                if (reloadData != null) {
                    reloadData.reload("SENT-INV", "Unsent to Invenorty, Chenck Your Internet Connection...");
                }
                LOGGER.error("sendPaymentToInv :" + e.getMessage());
            }
        });

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
                    int delete = glService.delete(vgl.getGlCode(), "GL-DELETE");
                    if (delete == 1) {
                        listVGl.remove(row);
                        if (vgl.getTraderCode() != null) {
                            trader = traderService.findById(vgl.getTraderCode());
                            sendDeletePaymentToInv(trader, vgl.getGlCode());
                        }
                        fireTableRowsDeleted(0, listVGl.size());
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                LOGGER.error("Delete GL :" + ex.getMessage());
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
            vGl.setGlDate(Util1.getTodayDate());
            vGl.setFromCurId(Global.defalutCurrency.getKey().getCode());
            if (Global.defaultDepartment != null) {
                vGl.setDeptId(Global.defaultDepartment.getDeptCode());
                vGl.setDeptUsrCode(Global.defaultDepartment.getUsrCode());
            }
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

    public void copyRow() {
        int selectRow = parent.convertRowIndexToModel(parent.getSelectedRow());
        if (listVGl != null) {
            VGl newVGl;
            VGl vgl = listVGl.get(selectRow - 1);
            if (vgl.getGlCode() != null) {
                newVGl = (VGl) SerializationUtils.clone(vgl);
                newVGl.setGlCode(null);
                newVGl.setDrAmt(null);
                newVGl.setCrAmt(null);
                listVGl.set(selectRow, newVGl);
                parent.setRowSelectionInterval(selectRow, selectRow);
                parent.setColumnSelectionInterval(7, 7);
                parent.requestFocus();
            }
        }
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
