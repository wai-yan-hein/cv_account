/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.StockUP;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.DamageDetailHis;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
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
public class DamageTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(DamageTableModel.class);
    private String[] columnNames = {"Code", "Description",
        "Qty", "Unit", "Cost Price", "Amount"};
    private JTable parent;
    private List<String> delList = new ArrayList();
    private SelectionObserver callBack;
    private List<DamageDetailHis> listDetail = new ArrayList<>();
    private Location location;
    private StockInfo stockInfo;
    private StockUP stockUp;
    private String cusType;

    public JTable getParent() {
        return parent;
    }

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

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 5);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
            case 1: //Medicine Name
                return String.class;
            case 2: //Qty
                return Float.class;
            case 4: //Cost Price
                return Double.class;
            case 5: //Amount
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
            DamageDetailHis record = listDetail.get(rowIndex);

            switch (columnIndex) {
                case 0: //Code
                    if (record.getStock() == null) {
                        return null;
                    } else {
                        return record.getStock().getStockCode();
                    }
                case 1: //Medicine Name
                    if (record.getStock() == null) {
                        return null;
                    } else {
                        return record.getStock().getStockName();
                    }
                case 2: //Qty
                    return record.getQty();
                case 3: //Unit
                    return record.getUnit();
                case 4: //Cost Price
                    return record.getCostPrice();
                case 5: //Amount
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
            DamageDetailHis record = listDetail.get(row);
            switch (column) {
                case 0: //Code
                    if (value != null) {
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setCostPrice(stock.getSalePriceN());
                            record.setUnit(stock.getSaleUnit());
                            double amt=record.getQty()*record.getCostPrice();
                            record.setAmount(amt);
                            addEmptyRow();
                            parent.setColumnSelectionInterval(2, 2);
                        }
                    }
                    break;
                case 1: //Medicine Name
                    if (value != null) {
                        record.setStock((Stock) value);
                    }
                    break;

                case 2: //Qty
                    if (value != null) {
                        Float qty = NumberUtil.NZeroFloat(value);
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);

                        } else {
                            record.setQty(qty);
                            record.setAmount(Util1.getFloat(record.getQty()) * Util1.getDouble(record.getCostPrice()));
                            if ((row + 1) <= listDetail.size()) {
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            }
                            parent.setColumnSelectionInterval(0, 0); //Move to Code

                        }
                    }
                    break;
                case 3: //Unit
                    if (value != null) {
                        if (value instanceof StockUnit) {
                            StockUnit st = (StockUnit) value;
                            record.setUnit(st);
                            //    String toUnit = record.getUnit().getItemUnitCode();
                            //   Float calAmount = calPrice(record, toUnit);
                            //   record.setCostPrice(Util1.getDouble(calAmount));
                            parent.setColumnSelectionInterval(5, 5);
                        }
                    }
                    break;
                case 4: //Cost Price
                    //  record.setUnit((ItemUnit) value);
                    break;
                case 5: //Amount
                    if (value != null) {
                        record.setAmount(Util1.getDouble(value));
                        if ((row + 1) <= listDetail.size()) {
                            parent.setRowSelectionInterval(row + 1, row + 1);
                        }
                        parent.setColumnSelectionInterval(0, 0); //Move to Code
                    }
                    break;

                default:
                    System.out.println("invalid index");
            }
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        parent.requestFocusInWindow();
        fireTableRowsUpdated(row, column);
        callBack.selected("STM-TOTAL", "STM-TOTAL");
    }

    public void clearData() {
        listDetail = new ArrayList();
        //delList = new ArrayList();
        location = null;
        addEmptyRow();
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                DamageDetailHis record = new DamageDetailHis();

                record.setStock(new Stock());
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

        DamageDetailHis record = listDetail.get(listDetail.size() - 1);

        if (record.getStock().getStockCode() != null) {
            return false;
        } else {
            return true;
        }
    }

    private String getCusType() {
        if (cusType == null) {
            cusType = "N";
        }

        return cusType;
    }

    public boolean isValidEntry() {
        boolean status = true;
        int uniqueId = 1;
        for (DamageDetailHis sdh2 : listDetail) {
            if (uniqueId != listDetail.size()) {
                if (Util1.NZeroDouble(sdh2.getQty()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid quantity.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (Util1.NZeroDouble(sdh2.getCostPrice()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid sale price.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else {
                    sdh2.setUniqueId(uniqueId);
                    uniqueId++;
                }
            }
        }

        if (uniqueId == 1) {
            status = false;
        }

        return status;
    }

    public List<DamageDetailHis> getDetail() {
        List<DamageDetailHis> listRetInDetail = new ArrayList();
        for (DamageDetailHis pdh2 : listDetail) {
            if (pdh2.getStock() != null) {
                if (pdh2.getStock().getStockCode() != null) {
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

        DamageDetailHis sdh = listDetail.get(row);
        if (sdh.getDmgDetailId() != null) {
            delList.add(sdh.getDmgDetailId().toString());
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

    public void setListDetail(List<DamageDetailHis> listDetail) {
        this.listDetail = listDetail;

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }
    public double getTotal() {
        double total = 0.0;
        for (DamageDetailHis sdh2 : listDetail) {
            if (sdh2.getStock() != null) {
                if (sdh2.getStock().getStockCode()!= null) {
                    total += Util1.NZeroDouble(sdh2.getAmount());
                }
            }
        }

        return total;
    }
}
