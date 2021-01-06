/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.entity.view.VGl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.text.DateFormat;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 *
 * @author Lenovo
 */
public class AllCashTableHandler extends TransferHandler {

    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final AllCashTableModel allCashTableModel;

    public AllCashTableHandler(AllCashTableModel allCashTableModel) {
        this.allCashTableModel = allCashTableModel;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return DnDConstants.ACTION_COPY_OR_MOVE;
    }

    @Override
    public Transferable createTransferable(JComponent comp) {
        JTable table = (JTable) comp;
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        VGl vgl = allCashTableModel.getVGl(row);
        StringSelection transferable = new StringSelection(gson.toJson(vgl));
        return transferable;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        return info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {

        if (!support.isDrop()) {
            return false;
        }

        if (!canImport(support)) {
            return false;
        }
        JTable table = (JTable) support.getComponent();
        JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();

        int row = dl.getRow();

        String data;
        try {
            data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            VGl vgl = gson.fromJson(data, VGl.class);
            vgl.setGlCode(null);
            vgl.setDrAmt(null);
            vgl.setCrAmt(null);
            VGl getVGl = allCashTableModel.getVGl(row);
            if (getVGl.getGlCode() == null) {
                allCashTableModel.setVGl(row, vgl);
                table.setRowSelectionInterval(row, row);
                table.setColumnSelectionInterval(7, 7);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }

        return true;
    }

}
