/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.TransferDetailHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
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
public class TransferTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferTableModel.class);
    private String[] columnNames = {"Code", "Description", "Relation-Str", "Exp-Date", "In Hand",
        "Qty", "Unit", "Balance", "Price", "Amount"};
    private JTable parent;
    private SelectionObserver callBack;
    private List<TransferDetailHis> listDetail = new ArrayList<>();
    private Location location;
    private List<String> delList = new ArrayList();

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setCallBack(SelectionObserver callBack) {
        this.callBack = callBack;
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

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
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 2 || column == 3 || column == 7
                || column == 9 || column == 4 || column == 6);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
            case 1: //Medicine Name
            case 2: //Relation-Str
            case 4: //In hand
            case 7: //Balance
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 5: //Qty
                return Integer.class;
            case 8: //Price
                return Double.class;
            case 9: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }

        try {
            TransferDetailHis record = listDetail.get(rowIndex);

            switch (columnIndex) {
                case 0: //Code
                    if (record.getMedicineId() == null) {
                        return null;
                    } else {
                        return record.getMedicineId().getStockCode();
                    }
                case 1: //Medicine Name
                    if (record.getMedicineId() == null) {
                        return null;
                    } else {
                        return record.getMedicineId().getStockName();
                    }
                case 2: //Relation-Str
                    if (record.getMedicineId() == null) {
                        return null;
                    } else {
                        return null;
                    }
                case 3: //Exp-Date
                    return record.getExpireDate();
                case 4: //In Hand
                    return record.getInHandQtyStr();
                case 5: //Qty
                    return record.getQty();
                case 6: //Unit
                    return record.getUnit();
                case 7: //Balance
                    return record.getBalQtyStr();
                case 8: //Price
                    return record.getPrice();
                case 9: //Amount
                    return record.getAmount();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        try {
            TransferDetailHis record = listDetail.get(row);
            switch (column) {
                case 0: //Code
                    if (value != null) {
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            record.setMedicineId(stock);
                            record.setQty(1.0f);
                            record.setPrice(stock.getSalePriceN());
                            record.setUnit(stock.getSaleUnit());
                            addEmptyRow();

                        }
                    }
                    parent.setColumnSelectionInterval(5, 5);
                    break;
                case 1: //Medicine Name
                    break;
                case 2: //Relation-Str
                    break;
                case 3: //Exp-Date

                    break;
                case 4: //In Hand
                    //record.setInHandQtyStr((String) value);
                    break;
                case 5: //Qty
                    if (value != null) {
                        Float qty = NumberUtil.NZeroFloat(value);
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);

                        } else {
                            record.setQty(qty);

                        }
                    }
                    parent.setColumnSelectionInterval(5, 5);
                    break;
                case 6: //Unit
                    if (value != null) {
                        if (value instanceof StockUnit) {
                            StockUnit st = (StockUnit) value;
                            record.setUnit(st);
                            String toUnit = record.getUnit().getItemUnitCode();

                        }
                    }
                    break;
                case 7: //Balance
                    //record.setPrice(Double.valueOf(value.toString()));
                    break;
                case 8: //Cost Price
                    if (value != null) {
                        Double price = Util1.NZeroDouble(value);
                        if (price <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Price must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);

                        } else {
                            record.setPrice(price);
                        }
                    }
                    break;

                default:
                    System.out.println("invalid index");
            }

            calculateAmount(record);
            fireTableCellUpdated(row, 9);

        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        parent.requestFocusInWindow();
        fireTableCellUpdated(row, column);
        callBack.selected("STM-TOTAL", "STM-TOTAL");

    }

    public void clearData() {
        listDetail = new ArrayList();
        //delList = new ArrayList();
        location = null;
        addEmptyRow();
    }

    private void calculateAmount(TransferDetailHis tran) {
        if (tran.getMedicineId() != null) {
            Double amount = tran.getQty() * tran.getPrice();
            tran.setAmount(amount);
        }
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                TransferDetailHis record = new TransferDetailHis();

                record.setMedicineId(new Stock());
                listDetail.add(record);

                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    public boolean hasEmptyRow() {
        if (listDetail == null) {
            return false;
        }

        if (listDetail.isEmpty()) {
            return false;
        }

        TransferDetailHis record = listDetail.get(listDetail.size() - 1);

        if (record.getMedicineId().getStockCode() != null) {
            return false;
        } else {
            return true;
        }
    }
    public List<TransferDetailHis> getDetail() {
        List<TransferDetailHis> listRetInDetail = new ArrayList();
        for (TransferDetailHis pdh2 : listDetail) {
            if (pdh2.getMedicineId()!= null) {
                if (pdh2.getMedicineId().getStockCode() != null) {
                    listRetInDetail.add(pdh2);
                }
            }
        }

        return listRetInDetail;
    }

    public List<String> getDelList() {
        return delList;
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        TransferDetailHis sdh = listDetail.get(row);
        if (sdh.getTranDetailId() != null) {
            delList.add(sdh.getTranDetailId().toString());
        }

        listDetail.remove(row);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        //  callBack.selected("STM-TOTAL", "STM-TOTAL");
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
    }
    public void setTransferDetailList(List<TransferDetailHis> listIssueDetail) {
        this.listDetail = listIssueDetail;

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
        fireTableCellUpdated(listDetail.size() - 1, listDetail.size() - 1);

        fireTableDataChanged();
    }
}
