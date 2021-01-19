/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Gl;
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
public class COAOpeningTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(COAOpeningTableModel.class);
    private List<VGl> listVGl = new ArrayList();
    private String[] columnNames = {"Code", "Chart Of Account", "Person Id", "Person Name", "Dept", "Currency", "Dr-Amt", "Cr-Amt"};
    private JTable parent;
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    @Autowired
    private GlService glService;
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
            VGl vgl = listVGl.get(row);

            switch (column) {
                case 0: //Id
                    return vgl.getSrcAccCode();
                case 1: //Name
                    return vgl.getSrcAccName();
                case 2:
                    return vgl.getTraderCode();
                case 3:// trader Name
                    return vgl.getTraderName();
                case 4:
                    return vgl.getDeptUsrCode();
                case 5:
                    return vgl.getFromCurId();
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
        VGl vgl = listVGl.get(row);
        switch (column) {
            case 6:
                if (value != null) {
                    vgl.setDrAmt(Util1.getFloat(value));
                    save(vgl, row);
                }
                break;
            case 7:
                if (value != null) {
                    vgl.setCrAmt(Util1.getFloat(value));
                    save(vgl, row);
                }
                break;

        }
        parent.requestFocusInWindow();
    }

    private void save(VGl vgl, int row) {

        vgl.setCompCode(Global.compCode);
        vgl.setCreatedBy(Global.loginUser.getAppUserCode());
        String strVGL = gson.toJson(vgl);
        Gl gl = gson.fromJson(strVGL, Gl.class);
        try {
            Gl save = glService.save(gl);
            if (save != null) {
                VGl saveVGl = listVGl.get(row);
                saveVGl.setGlCode(save.getGlCode());
                addNewRow();
                parent.setRowSelectionInterval(row + 1, row + 1);
                parent.setColumnSelectionInterval(6, 6);
                selectionObserver.selected("CAL-TOTAL", "-");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage(), "Save Opening", JOptionPane.ERROR_MESSAGE);
            LOGGER.error("Save Gl :" + ex.getMessage());
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
            if (vgl.getGlCode() == null) {
                status = false;
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
            listVGl.remove(row);
            fireTableRowsDeleted(0, listVGl.size());
        }

    }

    public void addVGl(VGl vgl) {
        listVGl.add(vgl);
        fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void setVGl(int row, VGl vgl) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, vgl);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        listVGl.clear();
        fireTableDataChanged();
    }

}
