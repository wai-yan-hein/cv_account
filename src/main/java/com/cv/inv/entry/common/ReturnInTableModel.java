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
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.RetInHisDetail;
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
public class ReturnInTableModel extends AbstractTableModel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReturnInTableModel.class);
    private String[] columnNames = {"Code", "Description", "Exp-Date",
        "Qty", "Std-W", "Unit", "Price", "Amount"};
    private JTable parent;
    private final List<String> delList = new ArrayList();
    private List<RetInHisDetail> listRetInDtail = new ArrayList();
    private String deletedList;
    private SelectionObserver observer;
    
    public SelectionObserver getObserver() {
        return observer;
    }
    
    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }
    
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
        RetInHisDetail record;
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
                    if (Util1.isNull(Global.sysProperties.get("system.use.usercode"), "0").equals("1")) {
                        return record.getStock().getUserCode();
                    } else {
                        return record.getStock().getStockCode();
                    }
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
                return Float.class;
            case 7: //Amt
                return Float.class;
            default:
                return Object.class;
        }
        
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 4 || column == 5 || column == 7);
    }
    
    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            RetInHisDetail record = listRetInDtail.get(row);
            switch (column) {
                case 0://code
                    if (value != null) {
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setPrice(stock.getSalePriceN());
                            record.setStdWt(stock.getSaleWeight());
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
                        record.setExpireDate(((RetInHisDetail) value).getExpireDate());
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    break;
                case 3://Qty
                    if (value != null) {
                        if (NumberUtil.isNumber(value)) {
                            Float qty = NumberUtil.NZeroFloat(value);
                            if (qty <= 0) {
                                JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                        "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                                
                            } else {
                                record.setQty(qty);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Global.parentForm, "Invalid Qty.");
                    }
                    
                    parent.setColumnSelectionInterval(3, 3);
                    break;
                case 4://Std-w
                    if (NumberUtil.isNumber(value)) {
                        record.setStdWt(Util1.getFloat(value));
                    } else {
                        JOptionPane.showMessageDialog(Global.parentForm, "Invalid Weight");
                    }
                    break;
                case 5://Unit
                    if (value != null) {
                        if (value instanceof StockUnit) {
                            StockUnit st = (StockUnit) value;
                            record.setStockUnit(st);
                            String toUnit = record.getStockUnit().getItemUnitCode();
                            Float calAmount = calPrice(record, toUnit);
                            record.setPrice(Util1.getFloat(calAmount));
                            parent.setColumnSelectionInterval(5, 5);
                        }
                    }
                    
                    break;
                case 6://Price
                    if (value != null) {
                        if (NumberUtil.isNumber(value)) {
                            Float price = Util1.getFloat(value);
                            if (price <= 0) {
                                JOptionPane.showMessageDialog(Global.parentForm, "Price must be positive value.",
                                        "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                                parent.requestFocusInWindow();
                                parent.setColumnSelectionInterval(column, column);
                                
                            } else {
                                record.setPrice(price);
                                parent.setColumnSelectionInterval(column, column);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Global.parentForm, "Invalid Price.");
                    }
                    
                    break;
            }
            calAmt(record);
            fireTableRowsUpdated(row, row);
            parent.requestFocusInWindow();
            observer.selected("TOTAL-AMT", "TOTAL-AMT");
            
        } catch (HeadlessException ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            
        }
        
    }
    
    public List<RetInHisDetail> getCurrentRow() {
        return this.listRetInDtail;
    }
    
    private void calAmt(RetInHisDetail rd) {
        if (rd.getStock() != null) {
            float amt;
            float stdWt = Util1.getFloat(rd.getStdWt());
            float qty = Util1.getFloat(rd.getQty());
            String fromUnit = rd.getStockUnit().getItemUnitCode();
            String toUnit = rd.getStock().getPurUnit().getItemUnitCode();
            String pattern = rd.getStock().getPattern().getPatternCode();
            rd.setSmallWeight(getSmallestWeight(stdWt, fromUnit, toUnit, pattern) * qty);
            rd.setSmallUnit(rd.getStock().getPurUnit());
            amt = qty * Util1.getFloat(rd.getPrice());
            rd.setAmount(amt);
        }
    }
    
    public List<RetInHisDetail> getRetInDetailHis() {
        return this.listRetInDtail;
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
                    listRetInDtail.remove(parent.getSelectedRow());
                }
            }
        } else {
            sWt = weight;
        }
        return sWt;
    }
    
    public void addNewRow() {
        if (listRetInDtail != null) {
            if (hasEmptyRow()) {
                RetInHisDetail pd = new RetInHisDetail();
                pd.setStock(new Stock());
                listRetInDtail.add(pd);
                fireTableRowsInserted(listRetInDtail.size() - 1, listRetInDtail.size() - 1);
            }
        }
    }
    
    private boolean hasEmptyRow() {
        boolean status = true;
        if (listRetInDtail.size() > 1) {
            RetInHisDetail get = listRetInDtail.get(listRetInDtail.size() - 1);
            if (get.getStock().getStockCode() == null) {
                status = false;
            }
        }
        return status;
    }
    
    public void clearRetInTable() {
        this.listRetInDtail.clear();
        addNewRow();
    }
    
    private Float calPrice(RetInHisDetail pd, String toUnit) {
        Stock stock = pd.getStock();
        float purAmt = 0.0f;
        float stdPurPrice = Util1.getFloat(stock.getPurPrice());
        float stdPrice = Util1.getFloat(stock.getSalePriceN());
        float userWt = pd.getStdWt();
        float stdWt = stock.getSaleWeight();
        String fromUnit = stock.getSaleUnit().getItemUnitCode();
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
    
    public void setRetInDetailList(List<RetInHisDetail> listDetail) {
        this.listRetInDtail = listDetail;
        addNewRow();
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
        
        RetInHisDetail record = listRetInDtail.get(row);
        
        if (record != null) {
            if (record.getRetInKey() != null) {
                delList.add(record.getRetInKey().getRetInDetailId());
//                if (deletedList == null) {
//                    deletedList = "'" + record.getInCompoundKey().getRetInDetailId() + "'";
//                } else {
//                    deletedList = deletedList + "," + "'" + record.getInCompoundKey().getRetInDetailId() + "'";
//                }
            }
        }
        
        listRetInDtail.remove(row);
        
        addNewRow();
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
    
    public List<RetInHisDetail> getListRetInDetail() {
        List<RetInHisDetail> listRetInDetailhis = new ArrayList();
        listRetInDtail.stream().filter(pdh2 -> (pdh2.getStock() != null)).filter(pdh2 -> (pdh2.getStock().getStockCode() != null)).forEachOrdered(pdh2 -> {
            listRetInDetailhis.add(pdh2);
        });
        return listRetInDetailhis;
    }
    
    public boolean isValidEntry() {
        boolean status = true;
        for (RetInHisDetail sdh2 : listRetInDtail) {
            if (sdh2.getStock().getStockCode() != null) {
                if (Util1.getFloat(sdh2.getAmount()) <= 0) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Could not saved because Return In amount can't not be zero");
                }
            }
            
        }
        return status;
    }
}
