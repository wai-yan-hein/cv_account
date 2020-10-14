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
import com.cv.inv.entity.Location;
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.SaleDetailHis;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.UnitRelation;
import com.cv.inv.service.RelationService;
import com.cv.inv.service.StockUnitService;
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
public class SaleEntryTableModel extends AbstractTableModel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleEntryTableModel.class);
    private String[] columnNames = {"Code", "Description", "Exp-Date",
        "Qty", "Std-Wt", "Unit", "Sale Price", "Discount", "Disc-Type", "Amount", "Location"};
    
    private JTable parent;
    private List<SaleDetailHis> listDetail = new ArrayList();
    private String sourceName;
    private SelectionObserver selectionObserver;
    private StockUP stockUp;
    private Location location;
    private String sourceAccId;
    private StockInfo stockInfo;
    private String cusType;
    @Autowired
    private StockUnitService unitService;
    @Autowired
    private RelationService relationService;
    
    public SaleEntryTableModel(List<SaleDetailHis> listDetail, StockInfo stockInfo, StockUP stockUp) {
        this.listDetail = listDetail;
        this.stockInfo = stockInfo;
        this.stockUp = stockUp;
    }
    
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    
    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }
    
    public void setParent(JTable parent) {
        this.parent = parent;
    }
    
    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
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
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }
    
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Name
                return String.class;
            case 2: //Exp-Date
                return String.class;
            case 3: //Qty
                return Float.class;
            case 4://Std-Wt
                return Float.class;
            case 5: //Unit
                return Object.class;
            case 6: //Sale Price
                return Double.class;
            case 7: //Discount
                return Double.class;
            case 8: //Discount Type
                return String.class;
            case 9: //Amount
                return Double.class;
            case 10: //Location
                return Location.class;
            default:
                return Object.class;
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        if (listDetail == null) {
            return false;
        }
        if (listDetail.isEmpty()) {
            return false;
        }
        
        SaleDetailHis record = listDetail.get(row);
        switch (column) {
            case 0://Code
                return true;
            case 1://Name
                return false;
            case 2://Exp Date
                return record.getStock().getStockCode() != null;
            case 3://Qty
                return record.getStock().getStockCode() != null;
            case 4://Std-Wt
                return true;
            case 5://Unit
                return true;
            case 7://Disc
                return true;
            case 10://Loc
                return record.getStock().getStockCode() != null;
            default:
                return false;
        }
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleDetailHis record = listDetail.get(row);
            switch (column) {
                case 0://code
                    if (record.getStock() == null) {
                        return null;
                    } else {
                        return record.getStock().getStockCode();
                    }
                case 1://Name
                    if (record.getStock() == null) {
                        return null;
                    } else {
                        return record.getStock().getStockName();
                    }
                case 2://exp-date
                    if (record.getExpDate() == null) {
                        return null;
                    } else {
                        return Util1.toDateStr(record.getExpDate(), "dd/MM/yyyy");
                    }
                case 3://qty
                    return record.getQuantity();
                case 4://Std-Wt
                    if (record.getStdWeight() != null) {
                        return Util1.getFloat(record.getStdWeight());
                    }
                case 5://unit
                    return record.getItemUnit();
                case 6://price
                    return record.getPrice();
                case 7://disc
                    if (record.getDiscount() == null) {
                        return null;
                    } else {
                        return record.getDiscount();
                    }
                case 8://disc type
                    return record.getDiscType();
                case 9://amount
                    return record.getAmount();
                case 10://loc
                    return record.getLocation();
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
        boolean isAmount = false;
        try {
            SaleDetailHis record = listDetail.get(row);
            switch (column) {
                case 0://Code
                    if (value != null) {
                        Stock stock = (Stock) value;
                        String stockCode = stock.getStockCode();
                        String[] strList = stockCode.split("@");
                        stockInfo.getStockInfo(strList[0]);
                        if (listDetail.get(parent.getSelectedRow()).getStock() != null) {
                            record.setPrice(stockUp.getPrice(stockCode, getCusType()));
                        }
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    break;
                case 2://Exp Date
                    if (value != null) {
                        if (Util1.isValidDateFormat(value.toString(), "dd/MM/yyyy")) {
                            record.setExpDate(Util1.toDate(value, "dd/MM/yyyy"));
                        } else {
                            if (value.toString().length() == 8) {
                                String toFormatDate = Util1.toFormatDate(value.toString());
                                record.setExpDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                            } else {
                                record.setExpDate(Util1.getTodayDate());
                                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date");
                            }
                        }
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    break;
                case 3://Qty
                    if (NumberUtil.isNumber(value)) {
                        if (NumberUtil.isPositive(value)) {
                            record.setQuantity(Util1.getFloat(value));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(column, column);
                    }
                    parent.setColumnSelectionInterval(4, 4);
                    break;
                case 4://Std-Wt
                    if (NumberUtil.isNumber(value)) {
                        if (NumberUtil.isPositive(value)) {
                            record.setStdWeight(Util1.getFloat(value));
                            String toUnit = record.getItemUnit().getItemUnitCode();
                            Double calAmount = calPrice(record, toUnit);
                            record.setAmount(calAmount);
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be positive");
                        parent.setColumnSelectionInterval(column, column);
                    }
                    break;
                
                case 5://Unit
                    if (value != null) {
                        record.setItemUnit((StockUnit) value);
                        String toUnit = record.getItemUnit().getItemUnitCode();
                        Double calAmount = calPrice(record, toUnit);
                        record.setAmount(calAmount);
                    } else {
                        record.setItemUnit(null);
                    }
                    parent.setColumnSelectionInterval(6, 6);
                    break;
                case 6://Sale Price
                    if (NumberUtil.isNumber(value)) {
                        if (NumberUtil.isPositive(value)) {
                            record.setPrice(Util1.getDouble(value));
                            parent.setColumnSelectionInterval(8, 8);
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(column, column);
                    }
                    break;
                case 7://Discount
                    if (value != null) {
                        record.setDiscount(Util1.getDouble(value));
                    }
                    parent.setColumnSelectionInterval(8, 8);
                    break;
                case 8://Disc Type
                    if (value != null) {
                        record.setDiscType(value.toString());
                    }
                    parent.setColumnSelectionInterval(9, 9);
                    break;
                case 9: //Amount
                    if (value != null) {
                        record.setAmount(Util1.getDouble(value));
                        isAmount = true;
                    }
                    break;
                case 10://Loc
                    if (value != null) {
                        record.setLocation((Location) value);
                    } else {
                        record.setLocation(null);
                    }
                    break;
            }
            if (!isAmount) {
                calculateAmount(record);
                fireTableCellUpdated(row, 9);
            }
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        fireTableCellUpdated(row, column);
    }
    
    public boolean hasEmptyRow() {
        if (listDetail == null) {
            return false;
        }
        if (listDetail.isEmpty()) {
            return false;
        }
        
        SaleDetailHis detailHis = listDetail.get(listDetail.size() - 1);
        if (detailHis.getStock() != null) {
            return detailHis.getStock().getStockCode() == null;
        } else {
            return true;
        }
    }
    
    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleDetailHis detailHis = new SaleDetailHis();
                detailHis.setStock(new Stock());
                listDetail.add(detailHis);
                
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }

    //set stock when enter stock code
    public void setStock(Stock stock, int row) {
        if (listDetail == null) {
            return;
        }
        if (listDetail.isEmpty()) {
            return;
        }
        SaleDetailHis record = listDetail.get(row);
        record.setStock(stock);
        record.setQuantity(1.0f);
        record.setStdWeight(stock.getSaleMeasure());
        record.setItemUnit(stock.getSaleUnit());
        record.setUniqueId(row + 1);
        //record.setPrice(stock.getSalePriceN());
        if (Util1.getPropValue("system.default.location").equals("23")) {
            record.setLocation(location);
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }
        
        fireTableCellUpdated(row, 0);
    }
    
    public void setListDetail(List<SaleDetailHis> listDetail) {
        this.listDetail = listDetail;
        
        if (!hasEmptyRow()) {
            addEmptyRow();
        }
        
        fireTableDataChanged();
    }
    
    private String getCusType() {
        if (cusType == null) {
            cusType = "N";
        }
        
        return cusType;
    }
    
    public void setCusType(String type) {
        cusType = type;
    }
    
    public List<SaleDetailHis> getCurrentRow() {
        return this.listDetail;
    }
    
    public void removeListDetail() {
        this.listDetail.clear();
        addEmptyRow();
    }
    
    private void calculateAmount(SaleDetailHis sale) {
        if (sale.getStock() != null) {
            Stock stock = sale.getStock();
            String stockCode = stock.getStockCode();
            float saleQty = sale.getQuantity();
            double stdSalePrice = stockUp.getPrice(stockCode, getCusType());
            double discount = Util1.getDouble(sale.getDiscount());
            double calAmount = Util1.getDouble(sale.getAmount());
            float userWt = sale.getStdWeight();
            float stdWt = stock.getSaleMeasure();
            sale.setSmallestWT(getSmallestUnit(userWt, sale.getItemUnit().getItemUnitCode()));
            sale.setSmallestUnit("oz");
            
            String discType = Util1.getPropValue("system.app.sale.discount.calculation");
            switch (discType) {
                case "Percent":
                    discount = ((saleQty * stdSalePrice) * (discount / 100));
                    break;
                case "Value":
                    break;
                case "Each":
                    discount = discount * saleQty;
                    break;
            }
            if (userWt != stdWt) {
                double amount = (saleQty * calAmount) - discount;
                sale.setAmount(amount);
            } else {
                double amount = (saleQty * stdSalePrice) - discount;
                sale.setAmount(amount);
            }
        }
    }
    
    private Double calPrice(SaleDetailHis sdh, String toUnit) {
        Stock stock = sdh.getStock();
        String stockCode = stock.getStockCode();
        double saleAmount = 0.0;
        double stdSalePrice = stockUp.getPrice(stockCode, getCusType());
        double stdPrice = stockUp.getPrice(stockCode, getCusType());
        float userWt = sdh.getStdWeight();
        float stdWt = stock.getSaleMeasure();
        String fromUnit = stock.getSaleUnit().getItemUnitCode();
        
        if (!fromUnit.equals(toUnit)) {
            RelationKey key = new RelationKey(fromUnit, toUnit);
            UnitRelation unitRelation = relationService.findByKey(key);
            if (unitRelation != null) {
                float factor = unitRelation.getFactor();
                float convertWt = (userWt / factor); //unit change
                saleAmount = (convertWt / stdWt) * stdPrice; // cal price

            } else {
                key = new RelationKey(toUnit, fromUnit);
                Float factor = Global.hmRelation.get(key);
                if (factor != null) {
                    float convertWt = userWt * factor; // unit change
                    saleAmount = (convertWt / stdWt) * stdSalePrice; // cal price
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "Mapping units in Relation Setup.");
                }
            }
        } else {
            saleAmount = (userWt / stdWt) * stdSalePrice;
        }
        return saleAmount;
    }
    
    private Float getSmallestUnit(Float weight, String unit) {
        float sWt = 0.0f;
        RelationKey key = new RelationKey(unit, "oz");
        Float factor = Global.hmRelation.get(key);
        if (factor != null) {
            sWt = factor * weight;
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, String.format("Need Relation  %s with Smallest Unit", unit));
            listDetail.remove(parent.getSelectedRow());
        }
        LOGGER.info("Smallest Weight :" + sWt + "From >>>" + unit + "<<<");
        return sWt;
    }
    
    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }
    
    public List<SaleDetailHis> getListSaleDetail() {
        return listDetail;
    }
}
