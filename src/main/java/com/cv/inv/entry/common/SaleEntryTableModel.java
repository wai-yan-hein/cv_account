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
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.SaleHisDetail;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.UnitRelation;
import com.cv.inv.entry.editor.LocationAutoCompleter;
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
public class SaleEntryTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleEntryTableModel.class);
    private String[] columnNames = {"Code", "Description", "Department", "Location",
        "Qty", "Std-Wt", "Unit", "Sale Price", "Amount"};

    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    @Autowired
    private RelationService relationService;
    private String sourceName;
    private SelectionObserver selectionObserver;
    private final StockUP stockUp;
    private Department department;
    private String sourceAccId;
    private String cusType;
    private JTextField txtTotalItem;
    private final List<String> deleteList = new ArrayList();
    private LocationAutoCompleter locationAutoCompleter;

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
    }

    public SaleEntryTableModel(List<SaleHisDetail> listDetail, StockUP stockUp) {
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
            case 2: //Dept
                return Department.class;
            case 3://Location
                return Location.class;
            case 4: //Qty
                return Float.class;
            case 5://Std-Wt
                return Float.class;
            case 6: //Unit
                return Object.class;
            case 7: //Sale Price
                return Double.class;
            case 8: //Amount
                return Double.class;
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

        SaleHisDetail record = listDetail.get(row);
        switch (column) {
            case 0://Code
                return true;
            case 1://Name
                return false;
            case 2://Dept
                return record.getStock().getStockCode() != null;
            case 3://Loc
                return record.getStock().getStockCode() != null;
            case 4://Qty
                return record.getStock().getStockCode() != null;
            case 5://Std-Wt
                return true;
            case 6://Unit
                return true;
            case 7://Price
                return true;
            default:
                return false;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleHisDetail record = listDetail.get(row);
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
                case 2://dept
                    return record.getDepartment();
                case 3://loc
                    return record.getLocation();
                case 4://qty
                    return record.getQuantity();
                case 5://Std-Wt
                    if (record.getStdWeight() != null) {
                        return Util1.getFloat(record.getStdWeight());
                    }
                case 6://unit
                    return record.getItemUnit();
                case 7://price
                    return record.getPrice();
                case 8://amount
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
            SaleHisDetail record = listDetail.get(row);
            switch (column) {
                case 0://Code
                    if (value != null) {
                        Stock stock = (Stock) value;
                        record.setStock(stock);
                        record.setQuantity(1.0f);
                        record.setStdWeight(stock.getSaleMeasure());
                        record.setItemUnit(stock.getSaleUnit());
                        record.setDepartment(department);
                        stockUp.add(stock);
                        if (stock.getStockCode() != null) {
                            String stockCode = stock.getStockCode();
                            Double salePrice = stockUp.getPrice(stockCode, getCusType());
                            record.setPrice(Util1.getFloat(salePrice));
                        }
                        txtTotalItem.setText(Integer.toString(listDetail.size()));
                        addEmptyRow();
                    }
                    parent.setColumnSelectionInterval(4, 4);
                    break;
                case 2://Dept
                    if (value != null) {
                        record.setDepartment((Department) value);
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    break;
                case 3://Loc
                    if (value != null) {
                        record.setLocation((Location) value);
                    } else {
                        record.setLocation(null);
                    }
                    break;
                case 4://Qty
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
                case 5://Std-Wt
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

                case 6://Unit
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
                case 7://Sale Price
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
                case 8: //Amount
                    if (value != null) {
                        record.setAmount(Util1.getFloat(value));
                    }
                    break;
            }
            calculateAmount(record);
            fireTableRowsUpdated(row, row);
            selectionObserver.selected("SALE-TOTAL", "SALE-TOTAL");
            parent.requestFocusInWindow();
            //   fireTableCellUpdated(row, 8);
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public boolean hasEmptyRow() {
        if (listDetail == null) {
            return false;
        }
        if (listDetail.isEmpty()) {
            return false;
        }

        SaleHisDetail detailHis = listDetail.get(listDetail.size() - 1);
        if (detailHis.getStock() != null) {
            return detailHis.getStock().getStockCode() == null;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleHisDetail detailHis = new SaleHisDetail();
                detailHis.setStock(new Stock());
                detailHis.setLocation(locationAutoCompleter.getLocation());
                listDetail.add(detailHis);

                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department dept) {
        this.department = dept;
    }

    public void setListDetail(List<SaleHisDetail> listDetail) {
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

    public void removeListDetail() {
        this.listDetail.clear();
        addEmptyRow();
    }

    private void calculateAmount(SaleHisDetail sale) {
        if (sale.getStock() != null) {
            Stock stock = sale.getStock();
            float saleQty = sale.getQuantity();
            float stdSalePrice = sale.getPrice();
            float userWt = sale.getStdWeight();
            String saleUnit = sale.getItemUnit().getItemUnitCode();
            String purUnit = stock.getPurPriceUnit().getItemUnitCode();
            Integer pattern = stock.getPattern().getPatternId();
            sale.setStdSmallWeight(getSmallestWeight(userWt, saleUnit, purUnit, pattern) * saleQty);
            sale.setSmallestWT(getSmallestWeight(userWt, saleUnit, purUnit, pattern) * saleQty);
            sale.setSmallestUnit(stock.getPurPriceUnit().getItemUnitCode());
            float amount = saleQty * stdSalePrice;
            sale.setAmount(amount);

        }
    }

    private Float calPrice(SaleHisDetail sdh, String toUnit) {
        Stock stock = sdh.getStock();
        float saleAmount = 0.0f;
        float stdSalePrice = sdh.getPrice();
        float stdPrice = sdh.getPrice();
        float userWt = sdh.getStdWeight();
        float stdWt = stock.getSaleMeasure();
        String fromUnit = stock.getSaleUnit().getItemUnitCode();
        Integer pattern = stock.getPattern().getPatternId();

        if (!fromUnit.equals(toUnit)) {
            RelationKey key = new RelationKey(fromUnit, toUnit, pattern);
            UnitRelation unitRelation = relationService.findByKey(key);
            if (unitRelation != null) {
                float factor = unitRelation.getFactor();
                float convertWt = (userWt / factor); //unit change
                saleAmount = (convertWt / stdWt) * stdPrice; // cal price

            } else {
                key = new RelationKey(toUnit, fromUnit, pattern);
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

    private Float getSmallestWeight(Float weight, String unit, String purUnit, Integer pattern) {
        float sWt = 0.0f;
        if (!unit.equals(purUnit)) {
            RelationKey key = new RelationKey(unit, purUnit, pattern);
            Float factor = Global.hmRelation.get(key);
            if (factor != null) {
                sWt = factor * weight;
            } else {
                key = new RelationKey(purUnit, unit, pattern);
                factor = Global.hmRelation.get(key);
                if (factor != null) {
                    sWt = weight / factor;
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, String.format("Need Relation  %s with Smallest Unit", unit));
                    listDetail.remove(parent.getSelectedRow());
                }
            }
        } else {
            sWt = weight;
        }
        return sWt;
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public List<SaleHisDetail> getListSaleDetail() {
        List<SaleHisDetail> listpurDetail = new ArrayList();
        for (SaleHisDetail pdh2 : listDetail) {
            if (pdh2.getStock() != null) {
                if (pdh2.getStock().getStockCode() != null) {
                    listpurDetail.add(pdh2);
                }
            }
        }

        return listpurDetail;
    }

    public void setTxtTotalItem(JTextField txtTtlItem) {
        this.txtTotalItem = txtTtlItem;
    }

    public boolean isValidEntry() {
        boolean status = true;
        int uniqueId = 1;
        for (SaleHisDetail sdh : listDetail) {
            if (uniqueId != listDetail.size()) {
                if (Util1.NZeroDouble(sdh.getQuantity()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid quantity.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (Util1.NZeroDouble(sdh.getPrice()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid sale price.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else {
                    sdh.setUniqueId(uniqueId);
                    uniqueId++;
                }
            }
        }

        if (uniqueId == 1) {
            status = false;
        }

        return status;
    }

    public List<String> getDelList() {
        return deleteList;
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        SaleHisDetail sdh = listDetail.get(row);
        if (sdh.getSaleDetailKey().getSaleDetailId() != null) {
            deleteList.add(sdh.getSaleDetailKey().getSaleDetailId());
        }

        listDetail.remove(row);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
    }

}
