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
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.PurchaseDetail;
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.UnitRelation;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.service.RelationService;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
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
public class PurchaseEntryTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEntryTableModel.class);
    private String[] columnNames = {"Code", "Description", "Department", "Location",
        "Qty", "Std-Wt", "Unit", "Avg-Wt", "Pur Price", "Amount"};
    private JTable parent;
    private List<PurchaseDetail> listPurDetail = new ArrayList();
    private List<String> delList = new ArrayList();
    private LocationAutoCompleter locationCompleter;
    private JFormattedTextField txtTotalAmt;
    private Location location;
    private Department department;
    private SelectionObserver callBack;

    public void setTxtTotalAmt(JFormattedTextField txtTotalAmt) {
        this.txtTotalAmt = txtTotalAmt;
    }
//
//    public LocationAutoCompleter getLocationCompleter() {
//        return locationCompleter;
//    }
//
//    public void setLocationCompleter(LocationAutoCompleter locationCompleter) {
//        this.locationCompleter = locationCompleter;
//    }

    @Autowired
    private RelationService relationService;

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // return columnIndex == 8 ? false : true; //To change body of generated methods, choose Tools | Templates.
        if (columnIndex == 1) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public int getRowCount() {
        if (listPurDetail == null) {
            return 0;
        }
        return listPurDetail.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 4:
                return Float.class;
            case 5:
                return String.class;
            case 6:
                return Float.class;
            case 7:
                return Float.class;
            case 8:
                return Float.class;
            case 9:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PurchaseDetail pur = listPurDetail.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if (pur.getStock() != null) {
                    return pur.getStock().getStockCode();
                }
            case 1:
                if (pur.getStock() != null) {
                    return pur.getStock().getStockName();
                }
            case 2:
                if (pur.getDepartment() != null) {
                    return pur.getDepartment();
                } else {
                    return null;
                }
            case 3:
                if (pur.getLocation() != null) {
                    return pur.getLocation();
                }
            case 4:
                return pur.getQty();
            case 5:
                return pur.getStdWeight();
            case 6:
                return pur.getPurUnit();
            case 7:
                return pur.getAvgWeight();
            case 8:
                return pur.getPurPrice();
            case 9:
                return pur.getPurAmt();

            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (listPurDetail == null) {
            return;
        }

        if (listPurDetail.isEmpty()) {
            return;
        }
        try {
            PurchaseDetail pur = listPurDetail.get(rowIndex);
            boolean isAmount = false;

            switch (columnIndex) {
                case 0://Stock
                    if (aValue instanceof Stock) {
                        Stock stock = (Stock) aValue;
                        pur.setStock(stock);
                        pur.setQty(Util1.getFloat(1.0));
                        if (stock.getPurPrice() != null) {
                            pur.setPurPrice(stock.getPurPrice());
                        } else {
                            pur.setPurPrice(Util1.getFloat(0.0));
                        }
                        pur.setAvgPrice(stock.getPurPrice());
                        pur.setStdWeight(stock.getPurPriceMeasure());
                        pur.setPurUnit(stock.getPurPriceUnit());
                        pur.setDepartment(department);
                        pur.setLocation(location);
                        addNewRow();
                        parent.setColumnSelectionInterval(4, 4);
                    }
                    break;
                case 2:
                    if (aValue != null) {
                        pur.setDepartment((Department) aValue);
                        parent.setColumnSelectionInterval(3, 3);
                    }

                    break;
                case 3:
                    if (pur.getLocation() != null) {
                        pur.setLocation((Location) aValue);
                    } else {
                        pur.setLocation(null);
                    }
                    break;
                case 4://Qty
                    if (NumberUtil.isNumber(aValue)) {
                        if (NumberUtil.isPositive(aValue)) {
                            pur.setQty(Util1.getFloat(aValue));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(columnIndex, columnIndex);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(columnIndex, columnIndex);
                    }
                    parent.setColumnSelectionInterval(5, 5);
                    break;
                case 5://std wt
                    if (NumberUtil.isNumber(aValue)) {
                        if (NumberUtil.isPositive(aValue)) {
                            pur.setStdWeight(Util1.getFloat(aValue));
                            //calculation with unit
                            String toUnit = pur.getPurUnit().getItemUnitCode();
                            Float calAmount = calPrice(pur, toUnit);
                            //  pur.setPurPrice(calAmount);
                            pur.setPurAmt(calAmount);
                            parent.setColumnSelectionInterval(5, 5);
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(columnIndex, columnIndex);
                        }

                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(columnIndex, columnIndex);
                    }
                    parent.setColumnSelectionInterval(6, 6);

                    break;
                case 6:// unit
                    if (aValue instanceof StockUnit) {
                        StockUnit st = (StockUnit) aValue;
                        pur.setPurUnit(st);
                        String toUnit = pur.getPurUnit().getItemUnitCode();
                        Float calAmount = calPrice(pur, toUnit);
                        pur.setPurAmt(calAmount);
                        parent.setColumnSelectionInterval(6, 6);

                    }
                    break;
                case 7://avg-wt
                    if (NumberUtil.isNumber(aValue)) {
                        if (NumberUtil.isPositive(aValue)) {
                            Float avgWt = Util1.getFloat(aValue);
                            Float stdWt = pur.getStdWeight();
                            float avgPrice = (avgWt / stdWt) * pur.getPurPrice();
                            pur.setAvgWeight(avgWt);
                            pur.setAvgPrice(avgPrice);
                            pur.setPurAmt(avgPrice);
                            parent.setColumnSelectionInterval(7, 7);
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(columnIndex, columnIndex);
                        }

                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(columnIndex, columnIndex);
                    }

                    break;

                case 8:
                    if (NumberUtil.isNumber(aValue)) {
                        if (NumberUtil.isPositive(aValue)) {
                            pur.setPurPrice(Util1.getFloat(aValue));
                            parent.setColumnSelectionInterval(8, 8);
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(columnIndex, columnIndex);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(columnIndex, columnIndex);
                    }

                    break;
            }

            calculateAmount(pur);
            //   fireTableCellUpdated(rowIndex, 9);

            //    calTotalAmount(pur);
            fireTableRowsUpdated(rowIndex, rowIndex);
            callBack.selected("STM-TOTAL", "STM-TOTAL");
            parent.requestFocusInWindow();

        } catch (Exception e) {
            LOGGER.error("setValueAt :" + e.getStackTrace()[0].getLineNumber() + " - " + e.getMessage());
        }
    }

    private void calculateAmount(PurchaseDetail pur) {
        if (pur.getStock() != null) {
            float saleQty = pur.getQty();
            float stdSalePrice = pur.getPurPrice();
            float calAmount = Util1.getFloat(pur.getPurAmt());
            float userWt = pur.getStdWeight();
            pur.setSmallestWT(getSmallestUnit(userWt, pur.getPurUnit().getItemUnitCode()));
            pur.setSmallestUnit("oz");

            if (calAmount != 0) {
                float amount = saleQty * calAmount;
                pur.setPurAmt(amount);
            } else {
                float amount = saleQty * stdSalePrice;
                pur.setPurAmt(amount);
            }
            //  calTotalAmount(pur);
        }
    }

//    private void calTotalAmount(PurchaseDetail pur) {
//        float ttlAmt = 0.0f;
//        if (pur.getStock() != null) {
//            //cal amt
//            pur.setPurAmt(Util1.getFloat(pur.getQty()) * pur.getAvgPrice());
//            //cal smallest wt
//            //pur.setSmallestWT(getSmallestUnit(pur.getStdWeight(), pur.getPurUnit().getItemUnitCode()));
//            pur.setSmallestWT(getSmallestUnit(pur.getStdWeight(), pur.getPurUnit().getItemUnitCode()));
//            pur.setSmallestUnit("oz");
//            //cal total amount
//
//            for (PurchaseDetail pd : listPurDetail) {
//                ttlAmt += Util1.getFloat(pd.getPurAmt());
//            }
//             txtTotalAmt.setValue(ttlAmt);
//
//        }
//    }
    public List<PurchaseDetail> getListPurDetail() {
        List<PurchaseDetail> listDetail = new ArrayList();
        for (PurchaseDetail pdh2 : listPurDetail) {
            if (pdh2.getStock() != null) {
                if (pdh2.getStock().getStockCode() != null) {
                    listDetail.add(pdh2);
                }
            }
        }

        return listDetail;
    }

    private Float calPrice(PurchaseDetail pd, String toUnit) {
        Stock stock = pd.getStock();
        float purAmt = 0.0f;
        float stdPurPrice = stock.getPurPrice();
        float stdPrice = stock.getPurPrice();
        float userWt = pd.getStdWeight();
        float stdWt = stock.getPurPriceMeasure();
        String fromUnit = stock.getPurPriceUnit().getItemUnitCode();

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

    private Float getSmallestUnit(Float weight, String unit) {
        float sWt = 0.0f;
        RelationKey key = new RelationKey(unit, "oz");
        Float factor = Global.hmRelation.get(key);
        if (factor != null) {
            sWt = factor * weight;
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, String.format("Need Relation  %s with Smallest Unit", unit));
            listPurDetail.remove(parent.getSelectedRow());
        }
        LOGGER.info("Smallest Weight :" + sWt + "From >>>" + unit + "<<<");
        return sWt;
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

    public void addNewRow() {
        if (listPurDetail != null) {
            if (hasEmptyRow()) {
                PurchaseDetail pd = new PurchaseDetail();
                pd.setStock(new Stock());
                listPurDetail.add(pd);

                fireTableRowsInserted(listPurDetail.size() - 1, listPurDetail.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listPurDetail.size() > 1) {
            PurchaseDetail get = listPurDetail.get(listPurDetail.size() - 1);
            if (get.getStock() == null) {
                status = false;
            }
        }
        return status;
    }

    public void clear() {
        if (!listPurDetail.isEmpty()) {
            listPurDetail.clear();
            addNewRow();
            fireTableDataChanged();
        }

    }

    public void setListPurDetail(List<PurchaseDetail> listPurDetail) {
        this.listPurDetail = listPurDetail;
        addNewRow();
        fireTableDataChanged();
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        int uniqueId = 1;
        for (PurchaseDetail sdh2 : listPurDetail) {
            if (sdh2.getLocation() == null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location");
                status = false;
            }
            if (uniqueId != listPurDetail.size()) {
                if (Util1.NZeroDouble(sdh2.getAvgWeight()) > Util1.NZeroDouble(sdh2.getStdWeight())) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Avg cannot greater than Std.",
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

    public List<String> getDelList() {
        return delList;
    }

    public void delete(int row) {
        if (listPurDetail == null) {
            return;
        }

        if (listPurDetail.isEmpty()) {
            return;
        }

        PurchaseDetail sdh = listPurDetail.get(row);
        if (sdh.getPurDetailKey().getPurDetailId() != null) {
            delList.add(sdh.getPurDetailKey().getPurDetailId());
        }

        listPurDetail.remove(row);

        if (!hasEmptyRow()) {
            addNewRow();
        }

        //  callBack.selected("STM-TOTAL", "STM-TOTAL");
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department dept) {
        this.department = dept;
    }

    public void setCallBack(SelectionObserver callBack) {
        this.callBack = callBack;
    }

}
