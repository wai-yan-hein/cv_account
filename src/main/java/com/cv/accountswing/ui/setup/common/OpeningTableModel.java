/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.COAOpening;
import com.cv.accountswing.entity.view.VCOAOpening;
import com.cv.accountswing.service.OpeningService;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        return column == 6 || column == 7;
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
                    return vgl.getSourceAccId();
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
            case 6:
                if (value != null) {
                    vgl.setDrAmt(Util1.getDouble(value));
                    save(vgl, row);
                }
                break;
            case 7:
                if (value != null) {
                    vgl.setCrAmt(Util1.getDouble(value));
                    save(vgl, row);
                }
                break;

        }
        parent.requestFocusInWindow();
    }

    private void save(VCOAOpening vgl, int row) {

        vgl.setCompId(Global.compId);
        vgl.setUserId(Global.loginUser.getUserId());
        String strVGL = gson.toJson(vgl);
        COAOpening op = gson.fromJson(strVGL, COAOpening.class);
        try {
            COAOpening save = opService.save(op);
            if (save != null) {
                VCOAOpening saveOpening = listOpening.get(row);
                saveOpening.setOpId(save.getOpId());
                int selRow = parent.getSelectedRow();
                parent.setRowSelectionInterval(selRow + 1, selRow + 1);
                parent.setColumnSelectionInterval(6, 6);
                //JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                selectionObserver.selected("CAL-TOTAL", "-");
            }
        } catch (HeadlessException ex) {
            LOGGER.error("Save Opening :" + ex.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage(), "Save Opening", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            VCOAOpening vGl = new VCOAOpening();
            vGl.setOpDate(Util1.getTodayDate());
            vGl.setCurCode(Global.sysProperties.get("system.default.currency"));
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
            if (vgl.getOpId() == null) {
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
