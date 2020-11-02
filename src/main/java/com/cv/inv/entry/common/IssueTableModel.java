/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.inv.entity.StockIssueDetailHis;
import com.cv.inv.entity.StockOutstanding;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class IssueTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueTableModel.class);
    private String[] columnNames = {"Option", "Vou No", "Item Code", "Description",
        "T-Code", "Trader Name", "Outstanding", "Qty", "Unit", "Balance"};
    private JTable parent;
    private List<StockIssueDetailHis> listDetail = new ArrayList();

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
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

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            StockIssueDetailHis sidh = listDetail.get(row);
            switch (column) {
                case 0://option
                    return sidh.getIssueOpt();
                case 1://vouno
                    return sidh.getRefVou();
                case 2://item code
                    return sidh.getIssueStock().getStockCode();
                case 3://desc
                    return sidh.getIssueStock().getStockName();
                case 4://t-code
                    return sidh.getTrader().getTraderId();
                case 5://trader name
                    return sidh.getTrader().getTraderName();
                case 6://outstand
                    return sidh.getStrOutstanding();
                case 7://qty
                    return sidh.getUnitQty();
                case 8://unit
                    return sidh.getItemUnit();
                case 9://bal
                    return sidh.getBalance();
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Option
            case 1: //Vou No
            case 2: //Med Code
            case 3: //Medicine
            case 4: //T-Code
            case 5: //Trader Name
            case 6: //Outstanding
            case 10: //Balance
                return String.class;
            case 7: //Qty
                return Float.class;
            default:
                return Object.class;
        }
    }

    public void add(StockOutstanding outs) {
        if (listDetail != null) {
            StockIssueDetailHis sidh = new StockIssueDetailHis();

            sidh.setBalance(outs.getQtyStr());
            sidh.setOutsBalance(outs.getBalanceQty());
            sidh.setIssueStock(outs.getStock());
            sidh.setIssueOpt(outs.getTranOption());
            sidh.setRefVou(outs.getInvId());
            sidh.setStrOutstanding(outs.getQtyStr());

            listDetail.add(sidh);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void removeListDetail() {
        this.listDetail.clear();
        fireTableDataChanged();
    }
}
