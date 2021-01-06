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

    private static final Logger log = LoggerFactory.getLogger(SaleEntryTableModel.class);
    private String[] columnNames = {"Code", "Description", "Department", "Location",
        "Qty", "Std-Wt", "Unit", "Sale Price", "Amount"};

    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    @Autowired
    private RelationService relationService;
    private SelectionObserver selectionObserver;
    private final StockUP stockUp;
    private Department department;
    private String cusType;
    private JTextField txtTotalItem;
    private final List<String> deleteList = new ArrayList();
    private LocationAutoCompleter locationAutoCompleter;

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
    }

    public SelectionObserver getSelectionObserver() {
        return selectionObserver;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public SaleEntryTableModel(List<SaleHisDetail> listDetail, StockUP stockUp) {
        this.listDetail = listDetail;
        this.stockUp = stockUp;
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
            case 4: //Qty
                return Float.class;
            case 5://Std-Wt
                return Float.class;
            /*case 6: //Unit
                return Object.class;*/
            case 7: //Sale Price
                return Float.class;
            case 8: //Amount
                return Float.class;
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
                    return record.getSaleUnit();
                case 7://price
                    return record.getPrice();
                case 8://amount
                    return record.getAmount();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
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
                        record.setStdWeight(stock.getSaleWeight());
                        record.setSaleUnit(stock.getSaleUnit());
                        record.setDepartment(department);
                        stockUp.add(stock);
                        if (stock.getStockCode() != null) {
                            String stockCode = stock.getStockCode();
                            Float salePrice = stockUp.getPrice(stockCode, getCusType());
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
                    parent.setColumnSelectionInterval(7, 7);
                    break;
                case 5://Std-Wt
                    if (NumberUtil.isNumber(value)) {
                        if (NumberUtil.isPositive(value)) {
                            record.setStdWeight(Util1.getFloat(value));
                            String toUnit = record.getSaleUnit().getItemUnitCode();
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
                        record.setSaleUnit((StockUnit) value);
                        String toUnit = record.getSaleUnit().getItemUnitCode();
                        Float calAmount = calPrice(record, toUnit);
                        record.setAmount(calAmount);
                    } else {
                        record.setSaleUnit(null);
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
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
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
            String saleUnit = sale.getSaleUnit().getItemUnitCode();
            String purUnit = stock.getPurUnit().getItemUnitCode();
            String pattern = stock.getPattern().getPatternCode();
            sale.setStdSmallWeight(getSmallestWeight(userWt, saleUnit, purUnit, pattern) * saleQty);
            sale.setSmallestWT(getSmallestWeight(userWt, saleUnit, purUnit, pattern) * saleQty);
            sale.setSmallestUnit(stock.getPurUnit().getItemUnitCode());
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
        float stdWt = stock.getSaleWeight();
        String fromUnit = stock.getSaleUnit().getItemUnitCode();
        String pattern = stock.getPattern().getPatternCode();

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

    private Float getSmallestWeight(Float weight, String unit, String purUnit, String pattern) {
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
        listDetail.stream().filter(pdh2 -> (pdh2.getStock() != null)).filter(pdh2 -> (pdh2.getStock().getStockCode() != null)).forEachOrdered(pdh2 -> {
            listpurDetail.add(pdh2);
        });

        return listpurDetail;
    }

    public void setTxtTotalItem(JTextField txtTtlItem) {
        this.txtTotalItem = txtTtlItem;
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (SaleHisDetail sdh : listDetail) {
            if (sdh.getStock().getStockCode() != null) {
                if (Util1.NZeroDouble(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public List<String> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        SaleHisDetail sdh = listDetail.get(row);
        if (sdh.getSaleDetailKey() != null) {
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

    public void addSale(SaleHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
        }
    }

}
