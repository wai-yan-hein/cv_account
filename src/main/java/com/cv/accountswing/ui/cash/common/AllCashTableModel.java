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
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAService;
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
    private String[] columnNames = {"Date", "Dept:", "Description", "Ref", "Person", "Account", "Curr", "Cash In / Dr", "Cash Out / Cr"};
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

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
        /*if (column == 6) {
        if (Global.sysProperties.get("system.multi.currency.flag").equals("0")) {
        return false;
        }
        }*/
        return column == 6 ? false : true;
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
                    return vgi.getDeptName();
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
                    return vgi.getDrAmt();
                case 8:
                    return vgi.getCrAmt();

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
                        trader = (Trader) value;
                        vgl.setTraderId(Util1.getLong(trader.getId()));
                        vgl.setTraderName(trader.getTraderName());
                        if (trader.getAccount() != null) {
                            vgl.setAccountId(trader.getAccount().getCode());
                            vgl.setAccName(trader.getAccount().getCoaNameEng());
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
                    vgl.setDrAmt(Util1.getDouble(value));
                }
                break;
            case 8:
                if (value != null) {
                    vgl.setCrAmt(Util1.getDouble(value));
                }
                break;
        }
        save(vgl, row, column);
        parent.requestFocus();

    }

    private void save(VGl vgl, int row, int column) {
        if (isValidEntry(vgl, row, column)) {
            vgl.setSourceAcId(sourceAccId);
            vgl.setCompId(Global.compId);
            vgl.setCreatedBy(Global.loginUser.getUserId().toString());
            String strVGL = gson.toJson(vgl);
            Gl gl = gson.fromJson(strVGL, Gl.class);

            try {
                Gl glSave = glService.save(gl);
                if (glSave != null) {
                    VGl saveVGl = listVGl.get(row);
                    saveVGl.setGlId(glSave.getGlId());
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
        if (vgl.getDrAmt() == null && vgl.getCrAmt() == null) {
            status = false;
            /*JOptionPane.showMessageDialog(Global.parentForm, "Dr / Cr missing.");
            parent.setColumnSelectionInterval(7, 7);
            parent.setRowSelectionInterval(row, row);*/
        }

        return status;
    }

    private void sendPaymentToInv(Gl gl) {
        taskExecutor.execute(() -> {
            try {
                if (Global.useActiveMQ) {
                    if (gl.getTraderId() != null) {
                        Trader trader = traderService.findById(gl.getTraderId().intValue());
                        if (trader != null) {

                            if (trader.getAppShortName() != null) {
                                if (trader.getAppShortName().equals("INVENTORY")) {
                                    //Need to sent to inventory
                                    messagingService.sendPaymentToInv(gl, trader);
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
                int delete = glService.delete(vgl.getGlId());
                if (delete == 1) {
                    listVGl.remove(row);
                    sendDeletePaymentToInv(trader, vgl.getGlId());
                    fireTableRowsDeleted(0, listVGl.size());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                LOGGER.error("Delete GL :" + ex.getMessage());
            }

        }

    }

    private void sendDeletePaymentToInv(Trader trader, long glId) {
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
            if (vgl.getGlId() == null) {
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
