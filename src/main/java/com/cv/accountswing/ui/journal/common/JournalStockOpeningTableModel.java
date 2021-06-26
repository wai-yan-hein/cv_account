/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.StockOpValue;
import com.cv.accountswing.entity.StockOpValueKey;
import com.cv.accountswing.entity.view.VStockOpValue;
import com.cv.accountswing.service.StockOpValueService;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
 * @author MyoGyi
 */
@Component
public class JournalStockOpeningTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(JournalStockOpeningTableModel.class);
    private List<VStockOpValue> listStockOpening = new ArrayList();
    String[] columnNames = {"Date", "Dept", "Code", "COA Name", "Currency", "OP-Amount"};
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private JTable parent;
    @Autowired
    private StockOpValueService opValueService;

    @Override
    public int getRowCount() {
        if (listStockOpening == null) {
            return 0;
        }
        return listStockOpening.size();
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
            VStockOpValue op = listStockOpening.get(row);

            switch (column) {
                case 0: //Date
                    if (op.getKey().getTranDate() != null) {
                        return Util1.toDateStr(op.getKey().getTranDate(), "dd/MM/yyyy");
                    }
                    return null;
                case 1: //Dept
                    return op.getDeptCodeUsr();
                case 2://Code
                    return op.getCoaCodeUsr();
                case 3://COA name
                    return op.getCoaNameEng();
                case 4://Currency
                    if (op.getKey().getCurrency() != null) {
                        return op.getKey().getCurrency();
                    }
                    return null;
                case 5://Op -amount
                    return op.getAmount();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        VStockOpValue op = listStockOpening.get(row);
        switch (column) {
            case 0:
                if (value != null) {
                    if (Util1.isValidDateFormat(value.toString(), "dd/MM/yyyy")) {
                        op.getKey().setTranDate(Util1.toDate(value, "dd/MM/yyyy"));
                    } else {
                        if (value.toString().length() == 8) {
                            String toFormatDate = Util1.toFormatDate(value.toString());
                            op.getKey().setTranDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        } else {
                            op.getKey().setTranDate(Util1.toDate(value, "dd/MM/yyyy"));
                            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date");
                        }
                    }
                }
                parent.setColumnSelectionInterval(1, 1);
                break;
            case 1:
                if (value instanceof Department) {
                    Department dep = (Department) value;
                    op.getKey().setDeptCode(dep.getDeptCode());
                    op.setDeptCodeUsr(dep.getUsrCode());
                }
                parent.setColumnSelectionInterval(2, 2);

                break;
            case 2:
                if (value instanceof ChartOfAccount) {
                    ChartOfAccount coa = (ChartOfAccount) value;
                    op.setCoaCodeUsr(coa.getCoaCodeUsr());
                    op.setCoaNameEng(coa.getCoaNameEng());
                    op.getKey().setCoaCode(coa.getCode());
                    if (op.getKey().getCurrency() == null) {
                        parent.setColumnSelectionInterval(4, 4);
                    } else {
                        parent.setColumnSelectionInterval(5, 5);

                    }

                }
                break;
            case 3:
                break;
            case 4:
                if (value != null) {
                    if (value instanceof Currency) {
                        Currency curr = (Currency) value;
                        op.setCurrName(curr.getCurrencyName());
                        op.getKey().setCurrency(curr.getKey().getCode());
                        parent.setColumnSelectionInterval(5, 5);
                    }
                }
                break;

            case 5:
                if (value != null) {
                    if (Util1.isNumber(value.toString())) {
                        op.setAmount(Util1.getDouble(value));
                    } else {
                        op.setAmount(0.0);
                        parent.setColumnSelectionInterval(5, 5);
                        parent.setRowSelectionInterval(row, row);
                        JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.");
                    }
                }
                break;
        }
        save(op);
        addEmptyRow();
        parent.requestFocus();
    }

    private boolean isValidEntry(VStockOpValue sv) {
        boolean status = true;
        if (Util1.isNull(sv.getKey().getDeptCode())) {
            status = false;
        } else if (Util1.isNull(sv.getKey().getCoaCode())) {
            status = false;
        } else if (Util1.isNull(sv.getKey().getCurrency())) {
            status = false;
        } else if (Util1.isNull(sv.getKey().getTranDate())) {
            status = false;
        }
        return status;
    }

    private void save(VStockOpValue op) {
        try {
            if (isValidEntry(op)) {
                StockOpValue stockOp = gson.fromJson(gson.toJson(op), StockOpValue.class);
                if (Util1.isNull(stockOp.getKey().getCompCode())) {
                    stockOp.getKey().setCompCode(Global.compCode);
                    stockOp.setCreatedBy(Global.loginUser.getUserCode());
                    stockOp.setCreatedDate(Util1.getTodayDate());
                } else {
                    stockOp.setUpdatedBy(Global.loginUser.getUserCode());
                }
                opValueService.save(stockOp, Global.loginUser.getAppUserCode());
            }
        } catch (JsonSyntaxException e) {
            log.error("Save Stock Opening :" + e.getMessage());
        }
        // Save object ? List ?
    }

    public VStockOpValue getVGl(int row) {
        return listStockOpening.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 5:
                return Double.class;
            default:
                return String.class;
        }
    }

    public List<VStockOpValue> getListStockOpening() {
        return listStockOpening;
    }

    public void setListStockOpening(List<VStockOpValue> listStockOpening) {
        this.listStockOpening = listStockOpening;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;

    }

    public VStockOpValue getChartOfAccount(int row) {
        return listStockOpening.get(row);
    }

    public void addGV(VStockOpValue op) {
        listStockOpening.add(op);
        fireTableRowsInserted(listStockOpening.size() - 1, listStockOpening.size() - 1);
    }

    public void setGVGroup(int row, VStockOpValue op) {
        if (!listStockOpening.isEmpty()) {
            listStockOpening.set(row, op);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public int getListSize() {
        return listStockOpening.size();
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listStockOpening.isEmpty() || listStockOpening == null) {
            status = true;
        } else {
            VStockOpValue vgl = listStockOpening.get(listStockOpening.size() - 1);
            if (vgl.getKey().getCoaCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            if (listStockOpening != null) {
                VStockOpValue record = new VStockOpValue();
                StockOpValueKey key = new StockOpValueKey();
                key.setCurrency(Global.defalutCurrency.getKey().getCode());
                record.setKey(key);
                listStockOpening.add(record);
                fireTableRowsInserted(listStockOpening.size() - 1, listStockOpening.size() - 1);
            }
        }

    }

    public void delete(int row) {
        if (!listStockOpening.isEmpty()) {
            VStockOpValue op = listStockOpening.get(row);
            if (op.getKey() != null) {
                try {
                    int status = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete.");
                    if (status == JOptionPane.YES_OPTION) {
                        String tranDate = Util1.toDateStr(op.getKey().getTranDate(), "yyyy-MM-dd");
                        String coaCode = op.getKey().getCoaCode();
                        String dept = op.getKey().getDeptCode();
                        String currency = op.getKey().getCurrency();
                        String compCode = op.getKey().getCompCode();
                        opValueService.delete(tranDate, coaCode, dept, currency, compCode, Global.loginUser.getAppUserCode());
                        listStockOpening.remove(row);
                        fireTableRowsDeleted(row, row);
                        if (parent.getCellEditor() != null) {
                            parent.getCellEditor().stopCellEditing();
                        }
                        parent.requestFocus();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                    log.error("delete : " + ex.getMessage());
                }
            }
        }
    }
}
