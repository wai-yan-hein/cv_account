/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.RetOutHisDetail;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.UnitRelation;
import com.cv.inv.service.RelationService;
import java.awt.HeadlessException;
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
 * @author Mg Kyaw Thura Aung
 */
@Component
public class ReturnOutTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(ReturnOutTableModel.class);
    private String[] columnNames = {"Code", "Description", "Exp-Date",
        "Qty", "Std-W", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<RetOutHisDetail> listDetail;
    private String deletedList;
    private final List<String> delList = new ArrayList();
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    @Autowired
    private RelationService relationService;

    public ReturnOutTableModel(List<RetOutHisDetail> listDetail) {
        this.listDetail = listDetail;

    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 4 || column == 5);
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
    public Class getColumnClass(int column) {
        switch (column) {
            case 3: //Qty
                return Float.class;
            case 4: //Std-Wt
                return Float.class;
            case 6: //Price
                return Float.class;
            case 7: //Amt
                return Float.class;
            default:
                return String.class;
        }

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
        RetOutHisDetail record;
        try {
            record = listDetail.get(row);
        } catch (Exception ex) {
            return null;
        }

        if (record == null) {
            return null;
        }

        switch (column) {
            case 0: //Code
                if (record.getStock() == null) {
                    return null;
                } else {
                    return record.getStock().getStockCode();
                }
            case 1: //Description
                if (record.getStock() == null) {
                    return null;
                } else {
                    return record.getStock().getStockName();
                }
            case 2: //Exp-Date
                if (record.getExpireDate() == null) {
                    return null;
                } else {
                    return record.getExpireDate();
                }
            case 3: //Qty
                if (record.getQty() == null) {
                    return null;
                } else {
                    return record.getQty();
                }
            case 4: //Std-Wt
                if (record.getStdWt() == null) {
                    return null;
                } else {
                    return record.getStdWt();
                }
            case 5: //Unit
                if (record.getStockUnit() == null) {
                    return null;
                } else {
                    return record.getStockUnit();
                }
            case 6: //Price
                if (record.getPrice() == null) {
                    return null;
                } else {
                    return record.getPrice();
                }
            case 7: //Amount
                if (record.getAmount() == null) {
                    return null;
                } else {
                    return record.getAmount();
                }
            default:
                return new Object();

        }
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
            RetOutHisDetail record = listDetail.get(row);

            switch (column) {
                case 0://code
                    if (value != null) {
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setPrice(Util1.getFloat(stock.getPurPrice()));
                            record.setStdWt(stock.getPurWeight());
                            record.setStockUnit(stock.getPurUnit());
                            addNewRow();
                            parent.setColumnSelectionInterval(3, 3);
                        }
                    }

                    break;
                case 1://Description
                    if (value != null) {
                        record.setStock((Stock) value);
                    }
                    break;
                case 2://Ex-date
                    if (value != null) {
                        record.setExpireDate(((RetOutHisDetail) value).getExpireDate());
                    }
                    // parent.setColumnSelectionInterval(3, 3);
                    break;
                case 3://Qty
                    if (value != null) {
                        float qty = Util1.getFloat(value);
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);

                        } else {
                            record.setQty(qty);
                        }
                    }
                    parent.setColumnSelectionInterval(6, 6);
                    break;
                case 4://Std-w
                    if (Util1.isNumber(value.toString())) {
                        record.setStdWt(Util1.getFloat(value));
                        parent.setColumnSelectionInterval(4, 4);
                    }
                    break;
                case 5://Unit
                    if (value != null) {
                        if (value instanceof StockUnit) {
                            StockUnit st = (StockUnit) value;
                            record.setStockUnit(st);
                            parent.setColumnSelectionInterval(5, 5);
                        }
                    }
                    break;
                case 6://Price

                    if (value != null) {
                        float price = Util1.getFloat(value);
                        if (price <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Price must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                            parent.requestFocusInWindow();
                            parent.setColumnSelectionInterval(6, 6);

                        } else {
                            record.setPrice(price);
                            parent.setColumnSelectionInterval(6, 6);
                        }
                    }

                    break;
                case 7://Amount
                    if (value != null) {
                        record.setAmount(Util1.getFloat(value));
                    }
                    break;

            }
            calAmt(record);
            fireTableRowsUpdated(row, row);
            observer.selected("CAL-TOTAL", "CAL-TOTAL");
        } catch (HeadlessException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
    }

    private void calAmt(RetOutHisDetail rOut) {
        float amt;
        if (rOut.getStock() != null) {
            float stdWt = Util1.getFloat(rOut.getStdWt());
            String fromUnit = rOut.getStockUnit().getItemUnitCode();
            String toUnit = rOut.getStock().getPurUnit().getItemUnitCode();
            String pattern = rOut.getStock().getPattern().getPatternCode();
            rOut.setSmallWeight(getSmallestWeight(stdWt, fromUnit, toUnit, pattern));
            rOut.setSmallUnit(rOut.getStock().getPurUnit());
            amt = Util1.getFloat(rOut.getQty()) * Util1.getFloat(rOut.getPrice());
            rOut.setAmount(amt);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.isEmpty() || listDetail == null) {
            status = true;
        } else {
            RetOutHisDetail detailHis = listDetail.get(listDetail.size() - 1);
            if (detailHis.getStock() == null) {
                status = false;
            }
        }

        return status;
    }

    private Float getSmallestWeight(Float weight, String fromUnit, String toUnit, String pattern) {
        float sWt = 0.0f;
        if (!fromUnit.equals(toUnit)) {
            RelationKey key = new RelationKey(fromUnit, toUnit, pattern);
            Float factor = Global.hmRelation.get(key);
            if (factor != null) {
                sWt = factor * weight;
            } else {
                key = new RelationKey(toUnit, fromUnit, pattern);
                factor = Global.hmRelation.get(key);
                if (factor != null) {
                    sWt = weight / factor;
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, String.format("Need Relation  %s with Smallest Unit", toUnit));
                    listDetail.remove(parent.getSelectedRow());
                }
            }
        } else {
            sWt = weight;
        }
        return sWt;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            RetOutHisDetail record = new RetOutHisDetail();
            record.setStock(new Stock());
            listDetail.add(record);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            RetOutHisDetail detailHis = new RetOutHisDetail();
            detailHis.setStock(new Stock());
            listDetail.add(detailHis);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }

    }

    public void clearRetOutTable() {
        this.listDetail.clear();
        addEmptyRow();
    }

    public List<RetOutHisDetail> getRetOutDetailHis() {
        return this.listDetail;
    }

    private Float calPrice(RetOutHisDetail pd, String toUnit) {
        Stock stock = pd.getStock();
        float purAmt = 0.0f;
        float stdPurPrice = stock.getPurPrice();
        float stdPrice = stock.getPurPrice();
        float userWt = pd.getStdWt();
        float stdWt = stock.getSaleWeight();
        String fromUnit = stock.getPurUnit().getItemUnitCode();
        String pattern = stock.getPattern().getPatternCode();

        if (!fromUnit.equals(toUnit)) {
            RelationKey key = new RelationKey(fromUnit, toUnit, pattern);
            UnitRelation unitRelation = relationService.findByKey(key);
            if (unitRelation != null) {
                float factor = unitRelation.getFactor();
                float convertWt = (userWt / factor); //unit change
                purAmt = (convertWt / stdWt) * stdPrice; // cal price

            } else {
                key = new RelationKey(toUnit, fromUnit, pattern);
                Float factor = Global.hmRelation.get(key);
                if (factor != null) {
                    float convertWt = userWt * factor; // unit change
                    purAmt = (convertWt / stdWt) * stdPurPrice; // cal price
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "Mapping units in Relation Setup.");
                }
            }
        } else {
            purAmt = (userWt / stdPrice) * stdPurPrice;
        }
        return purAmt;
    }

    public void setRetOutDetailList(List<RetOutHisDetail> listDetail) {
        this.listDetail = listDetail;

        if (hasEmptyRow()) {
            addEmptyRow();
        }
        fireTableCellUpdated(listDetail.size() - 1, listDetail.size() - 1);
        fireTableDataChanged();
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }
        if (listDetail.isEmpty()) {
            return;
        }

        RetOutHisDetail record = listDetail.get(row);

        if (record != null) {
            if (record.getOutCompoundKey() != null) {
                delList.add(record.getOutCompoundKey().getRetOutDetailId());
//                if (deletedList == null) {
//                    deletedList = "'" + record.getOutCompoundKey().getRetOutDetailId() + "'";
//                } else {
//                    deletedList = deletedList + "," + "'" + record.getOutCompoundKey().getRetOutDetailId() + "'";
//                }
            }
        }

        listDetail.remove(row);

        if (hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);
    }

    public String getDeleteListStr() {
        String deletedListStr;
        if (deletedList == null || deletedList.isEmpty()) {
            return null;
        } else {
            deletedListStr = deletedList;
        }
        return deletedListStr;
    }

    public List<String> getDelList() {
        return delList;
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (RetOutHisDetail retOut : listDetail) {
            if (retOut.getStock().getStockCode() != null) {
                if (Util1.getFloat(retOut.getAmount()) <= 0) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Could not saved because Return In amount can't not be zero");
                }
            }

        }
        return status;
    }

    public List<RetOutHisDetail> getListRetInDetail() {
        List<RetOutHisDetail> listRetInDetailhis = new ArrayList();
        listDetail.stream().filter(pdh2 -> (pdh2.getStock() != null)).filter(pdh2 -> (pdh2.getStock().getStockCode() != null)).forEachOrdered(pdh2 -> {
            listRetInDetailhis.add(pdh2);
        });
        return listRetInDetailhis;
    }
}
