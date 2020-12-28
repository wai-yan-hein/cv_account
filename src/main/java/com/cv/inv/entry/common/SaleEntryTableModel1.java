/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.StockUP;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.SaleDetailHis1;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.service.RelationService;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
public class SaleEntryTableModel1 extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleEntryTableModel1.class);
    private String[] columnNames = {"Code", "Description", "Exp-Date",
        "Qty", "Std-Wt", "Unit", "Sale Price", "Discount", "D-T", "Charge Type", "Amount", "Location"};

    private JTable parent;
    private List<SaleDetailHis1> listDetail = new ArrayList();
    @Autowired
    private RelationService relationService;
    private String sourceName;
    private SelectionObserver selectionObserver;
    private StockUP stockUp;
    private Location location;
    private String sourceAccId;
    private String cusType;
    private Department department;
    private JTextField txtTotalItem;

    public SaleEntryTableModel1(List<SaleDetailHis1> listDetail, StockUP stockUp) {
        this.listDetail = listDetail;
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
            case 9://Charge Type
                return String.class;
            case 10: //Amount
                return Double.class;
            case 11: //Location
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

        SaleDetailHis1 record = listDetail.get(row);
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
            case 8://D-T
                return true;
            case 9://C-T
                return true;
            case 11://Loc
                return record.getStock().getStockCode() != null;
            default:
                return false;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleDetailHis1 record = listDetail.get(row);
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
                case 9://disc type
                    return record.getChargeType();
                case 10://amount
                    return record.getAmount();
                case 11://loc
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
            SaleDetailHis1 record = listDetail.get(row);
            switch (column) {
                case 0://Code
                    if (value != null) {
                        Stock stock = (Stock) value;
                        record.setStock(stock);
                        record.setQuantity(1.0f);
                        record.setStdWeight(stock.getSaleMeasure());
                        record.setItemUnit(stock.getSaleUnit());
                        record.setUniqueId(row + 1);
                        record.setDepartment(department);
                        record.setLocation(location);
                        stockUp.add(stock);
                        if (stock.getStockCode() != null) {
                            String stockCode = stock.getStockCode();
                            record.setPrice(stockUp.getPrice(stockCode, getCusType()));
                        }
                    }
                    txtTotalItem.setText(Integer.toString(listDetail.size()));
                    addEmptyRow();
                    parent.setColumnSelectionInterval(4, 4);
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
                            Float calAmount = calPrice(record, toUnit);
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
                        Float calAmount = calPrice(record, toUnit);
                        record.setAmount(calAmount);
                    } else {
                        record.setItemUnit(null);
                    }
                    parent.setColumnSelectionInterval(6, 6);
                    break;
                case 6://Sale Price
                    if (NumberUtil.isNumber(value)) {
                        if (NumberUtil.isPositive(value)) {
                            record.setPrice(Util1.getFloat(value));
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
                        record.setDiscount(Util1.getFloat(value));
                        record.setDiscType("A");
                    }
                    parent.setColumnSelectionInterval(9, 9);
                    break;
                case 8://Disc Type
                    if (value != null) {
                        record.setDiscType(value.toString());
                    }
                    parent.setColumnSelectionInterval(9, 9);
                    break;
                case 10: //Amount
                    if (value != null) {
                        record.setAmount(Util1.getFloat(value));
                        isAmount = true;
                    }
                    break;
                case 11://Loc
                    if (value != null) {
                        record.setLocation((Location) value);
                    } else {
                        record.setLocation(null);
                    }
                    break;
            }
            if (!isAmount) {
                calculateAmount(record);
                fireTableCellUpdated(row, 10);
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

        SaleDetailHis1 detailHis = listDetail.get(listDetail.size() - 1);
        if (detailHis.getStock() != null) {
            return detailHis.getStock().getStockCode() == null;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleDetailHis1 detailHis = new SaleDetailHis1();
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
        SaleDetailHis1 record = listDetail.get(row);
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

    public void setListDetail(List<SaleDetailHis1> listDetail) {
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

    public List<SaleDetailHis1> getCurrentRow() {
        return this.listDetail;
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addEmptyRow();
    }

    private void calculateAmount(SaleDetailHis1 sale) {
        if (sale.getStock() != null) {
            Stock stock = sale.getStock();
            String stockCode = stock.getStockCode();
            float saleQty = sale.getQuantity();
            float stdSalePrice = stockUp.getPrice(stockCode, getCusType());
            float discount = Util1.getFloat(sale.getDiscount());
            float calAmount = Util1.getFloat(sale.getAmount());
            float userWt = sale.getStdWeight();
            float stdWt = stock.getSaleMeasure();
            sale.setSmallestWT(getSmallestUnit(userWt, sale.getItemUnit().getItemUnitCode()));
            sale.setSmallestUnit("oz");

            String discType = Util1.getStringValue(sale.getDiscType());
            switch (discType) {
                case "%":
                    discount = ((saleQty * stdSalePrice) * (discount / 100));
            }

            if (userWt != stdWt) {
                float amount = (saleQty * calAmount) - discount;
                sale.setAmount(amount);
            } else {
                float amount = (saleQty * stdSalePrice) - discount;
                sale.setAmount(amount);
            }
        }
    }

    private Float calPrice(SaleDetailHis1 sdh, String toUnit) {
        Stock stock = sdh.getStock();
        String stockCode = stock.getStockCode();
        float saleAmount = 0.0f;
        double stdSalePrice = stockUp.getPrice(stockCode, getCusType());
        double stdPrice = stockUp.getPrice(stockCode, getCusType());
        float userWt = sdh.getStdWeight();
        float stdWt = stock.getSaleMeasure();
        String fromUnit = stock.getSaleUnit().getItemUnitCode();

        /* if (!fromUnit.equals(toUnit)) {
        //RelationKey key = new RelationKey(fromUnit, toUnit);
        UnitRelation unitRelation = relationService.findByKey(key);
        if (unitRelation != null) {
        float factor = unitRelation.getFactor();
        float convertWt = (userWt / factor); //unit change
        saleAmount = (convertWt / stdWt) * stdPrice; // cal price
        
        } else {
        //key = new RelationKey(toUnit, fromUnit);
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
        }*/
        return saleAmount;
    }

    private Float getSmallestUnit(Float weight, String unit) {
        float sWt = 0.0f;
        /*RelationKey key = new RelationKey(unit, "oz");
        Float factor = Global.hmRelation.get(key);
        if (factor != null) {
        sWt = factor * weight;
        } else {
        JOptionPane.showMessageDialog(Global.parentForm, String.format("Need Relation  %s with Smallest Unit", unit));
        listDetail.remove(parent.getSelectedRow());
        }
        LOGGER.info("Smallest Weight :" + sWt + "From >>>" + unit + "<<<");*/ return sWt;
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public List<SaleDetailHis1> getListSaleDetail() {
        return listDetail;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department dept) {
        this.department = dept;
    }

    public void setTxtTotalItem(JTextField txtTtlItem) {
        this.txtTotalItem = txtTtlItem;
    }
}
