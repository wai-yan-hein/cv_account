/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.RetInDetailHis;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.UnitRelation;
import com.cv.inv.service.RelationService;
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
public class ReturnInTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReturnInTableModel.class);
    private String[] columnNames = {"Code", "Description", "Exp-Date",
        "Qty", "Std-W", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<RetInDetailHis> listRetInDtail = new ArrayList();
    private String deletedList;

    @Autowired
    private RelationService relationService;

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        if (listRetInDtail == null) {
            return 0;
        }
        return listRetInDtail.size();
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
    public Object getValueAt(int row, int column) {
        RetInDetailHis record;
        try {
            record = listRetInDtail.get(row);
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

    public void addNewRow() {
        if (hasEmptyRow()) {
            RetInDetailHis detailHis = new RetInDetailHis();
            detailHis.setUniqueId(listRetInDtail.size() + 1);
            listRetInDtail.add(detailHis);
            fireTableRowsInserted(listRetInDtail.size() - 1, listRetInDtail.size() - 1);
        }

    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Expire-Date
                return String.class;
            case 3: //Qty
                return Float.class;
            case 4: //Std-Wt
                return Float.class;
            case 5: //Unit
                return Object.class;
            case 6: //Price
                return Double.class;
            case 7: //Amt
                return Double.class;
            default:
                return Object.class;
        }

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 2 || column == 7);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listRetInDtail == null) {
            return;
        }

        if (listRetInDtail.isEmpty()) {
            return;
        }

        try {
            RetInDetailHis record = listRetInDtail.get(row);

            switch (column) {
                case 0://code
                    if (value != null) {
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setPrice(stock.getSalePriceN());
                            record.setStdWt(stock.getSaleMeasure());
                            record.setStockUnit(stock.getSaleUnit());
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
                        record.setExpireDate(((RetInDetailHis) value).getExpireDate());
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    break;
                case 3://Qty
                    if (value != null) {
                        Float qty = NumberUtil.NZeroFloat(value);
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);

                        } else {
                            record.setQty(qty);
                            parent.setColumnSelectionInterval(4, 4);
                        }
                    }
                    break;

                case 4://Std-w
                    if (value != null) {
                        record.setStdWt(Util1.getFloat(value));
                        //calculation with unit
                        String toUnit = record.getStockUnit().getItemUnitCode();
                        Float calAmount = calPrice(record, toUnit);
                        record.setPrice(Util1.getDouble(calAmount));
                        parent.setColumnSelectionInterval(5, 5);

                    }

                    break;
                case 5://Unit
                    if (value != null) {
                        if (value instanceof StockUnit) {
                            StockUnit st = (StockUnit) value;
                            record.setStockUnit(st);
                            String toUnit = record.getStockUnit().getItemUnitCode();
                            Float calAmount = calPrice(record, toUnit);
                            record.setPrice(Util1.getDouble(calAmount));
                            parent.setColumnSelectionInterval(6, 6);
                        }
                    }

                    break;
                case 6://Price
                    if (value != null) {
                        Double price = NumberUtil.NZero(value);
                        if (price <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Price must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                            parent.requestFocusInWindow();
                            parent.setColumnSelectionInterval(6, 6);

                        } else {
                            record.setPrice(price);
                            parent.setColumnSelectionInterval(7, 7);
                        }
                    }

                    break;
                case 7://Amount
                    if (value != null) {
                        record.setAmount(Util1.getDouble(value));
                        if ((row + 1) <= listRetInDtail.size()) {
                            parent.setRowSelectionInterval(row + 1, row + 1);
                        }
                        parent.setColumnSelectionInterval(0, 0); //Move to Code
                    }
                    break;

            }
            //parent.requestFocusInWindow();
            calAmt(record);
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
    }

    public List<RetInDetailHis> getCurrentRow() {
        return this.listRetInDtail;
    }

    private void calAmt(RetInDetailHis retInDetail) {
        Double amt;
        amt = retInDetail.getQty() * retInDetail.getPrice();
        retInDetail.setAmount(amt);
    }

    public List<RetInDetailHis> getRetInDetailHis() {
        return this.listRetInDtail;
    }

    public void addEmptyRow() {
        if (listRetInDtail != null) {
            RetInDetailHis record = new RetInDetailHis();
            record.setUniqueId(listRetInDtail.size() + 1);
            listRetInDtail.add(record);
            fireTableRowsInserted(listRetInDtail.size() - 1, listRetInDtail.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listRetInDtail.isEmpty() || listRetInDtail == null) {
            status = true;
        } else {
            RetInDetailHis detailHis = listRetInDtail.get(listRetInDtail.size() - 1);
            if (detailHis.getStock() == null) {
                status = false;
            }
        }

        return status;
    }

    public void clearRetInTable() {
        this.listRetInDtail.clear();
        addEmptyRow();
    }

    private Float calPrice(RetInDetailHis pd, String toUnit) {
        Stock stock = pd.getStock();
        float purAmt = 0.0f;
        float stdPurPrice = Util1.getFloat(stock.getSalePriceN());
        float stdPrice = Util1.getFloat(stock.getSalePriceN());
        float userWt = pd.getStdWt();
        float stdWt = stock.getSaleMeasure();
        String fromUnit = stock.getSaleUnit().getItemUnitCode();

        if (!fromUnit.equals(toUnit)) {
            RelationKey key = new RelationKey(fromUnit, toUnit);
            UnitRelation unitRelation = relationService.findByKey(key);
            if (unitRelation != null) {
                float factor = unitRelation.getFactor();
                float convertWt = (userWt / factor); //unit change
                purAmt = (convertWt / stdWt) * stdPrice; // cal price

            } else {
                key = new RelationKey(toUnit, fromUnit);
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

    public void setRetInDetailList(List<RetInDetailHis> listDetail) {
        this.listRetInDtail = listDetail;

        if (hasEmptyRow()) {
            addEmptyRow();
        }
        fireTableCellUpdated(listDetail.size() - 1, listDetail.size() - 1);
       
        fireTableDataChanged();
    }

    public void delete(int row) {
        if (listRetInDtail == null) {
            return;
        }
        if (listRetInDtail.isEmpty()) {
            return;
        }

        RetInDetailHis record = listRetInDtail.get(row);

        if (record != null) {
            if (record.getInCompoundKey() != null) {
                if (deletedList == null) {
                    deletedList = "'" + record.getInCompoundKey().getRetInDetailId() + "'";
                } else {
                    deletedList = deletedList + "," + "'" + record.getInCompoundKey().getRetInDetailId() + "'";
                }
            }
        }

        listRetInDtail.remove(row);

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

}
