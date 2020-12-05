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
import com.cv.inv.entity.PurHisDetail;
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
    private List<PurHisDetail> listPurDetail = new ArrayList();
    private final List<String> delList = new ArrayList();
    private LocationAutoCompleter locationAutoCompleter;
    private JFormattedTextField txtTotalAmt;

    private SelectionObserver callBack;

    public JFormattedTextField getTxtTotalAmt() {
        return txtTotalAmt;
    }

    public void setTxtTotalAmt(JFormattedTextField txtTotalAmt) {
        this.txtTotalAmt = txtTotalAmt;
    }

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
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
        return columnIndex != 9 && columnIndex != 1; //To change body of generated methods, choose Tools | Templates.
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
            case 2:
                return Department.class;
            case 3:
                return Location.class;
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
        PurHisDetail pur = listPurDetail.get(rowIndex);
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
                } else {
                    return null;
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
            PurHisDetail pur = listPurDetail.get(rowIndex);
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
                        pur.setDepartment(Global.defaultDepartment);
                        pur.setLocation(Global.defaultLocation);
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
                    if (aValue != null) {
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
                            isAmount = true;
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
                            isAmount = true;
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
                            parent.setColumnSelectionInterval(0, 0);
                            parent.setRowSelectionInterval(rowIndex + 1, rowIndex + 1);
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
            if (!isAmount) {
                calculateAmount(pur);
            }

            //   fireTableCellUpdated(rowIndex, 9);
            //    calTotalAmount(pur);
            fireTableRowsUpdated(rowIndex, rowIndex);
            callBack.selected("STM-TOTAL", "STM-TOTAL");
            parent.requestFocusInWindow();

        } catch (Exception e) {
            LOGGER.error("setValueAt :" + e.getStackTrace()[0].getLineNumber() + " - " + e.getMessage());
        }
    }

    private void calculateAmount(PurHisDetail pur) {
        if (pur.getStock() != null) {
            float avgWt;
            float purQty = pur.getQty();
            float purPrice = Util1.getFloat(pur.getPurPrice());
            float userWt = pur.getStdWeight();
            String fromUnit = pur.getPurUnit().getItemUnitCode();
            String toUnit = pur.getStock().getPurPriceUnit().getItemUnitCode();
            Integer pattern = pur.getStock().getPattern().getPatternId();
            pur.setStdSmallWeight(getSmallestWeight(userWt, fromUnit, toUnit, pattern) * purQty);
            if (Util1.getFloat(pur.getAvgWeight()) > 0) {
                avgWt = pur.getAvgWeight();
            } else {
                avgWt = pur.getStdWeight();
            }
            pur.setSmallestWT(getSmallestWeight(avgWt, fromUnit, toUnit, pattern) * purQty);
            pur.setSmallestUnit(pur.getStock().getPurPriceUnit().getItemUnitCode());
            float amount = purQty * purPrice;
            pur.setPurAmt(amount);
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
    public List<PurHisDetail> getListPurDetail() {
        List<PurHisDetail> listDetail = new ArrayList();
        listPurDetail.stream().filter(pdh2 -> (pdh2.getStock() != null)).filter(pdh2 -> (pdh2.getStock().getStockCode() != null)).forEachOrdered(pdh2 -> {
            listDetail.add(pdh2);
        });

        return listDetail;
    }

    private Float calPrice(PurHisDetail pd, String toUnit) {
        Stock stock = pd.getStock();
        float purAmt = 0.0f;
        float stdPurPrice = stock.getPurPrice();
        float stdPrice = stock.getPurPrice();
        float userWt = pd.getStdWeight();
        float stdWt = stock.getPurPriceMeasure();
        String fromUnit = stock.getPurPriceUnit().getItemUnitCode();
        Integer pattern = stock.getPattern().getPatternId();

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
                    listPurDetail.remove(parent.getSelectedRow());
                }
            }
        } else {
            sWt = weight;
        }
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
                PurHisDetail pd = new PurHisDetail();
                pd.setStock(new Stock());
                pd.setLocation(locationAutoCompleter.getLocation());
                listPurDetail.add(pd);
                fireTableRowsInserted(listPurDetail.size() - 1, listPurDetail.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listPurDetail.size() > 1) {
            PurHisDetail get = listPurDetail.get(listPurDetail.size() - 1);
            if (get.getStock() == null) {
                status = false;
            }
        }
        return status;
    }

    public void clear() {
        if (listPurDetail != null) {
            listPurDetail.clear();
            addNewRow();
            fireTableDataChanged();
        }

    }

    public void setListPurDetail(List<PurHisDetail> listPurDetail) {
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
        for (PurHisDetail sdh2 : listPurDetail) {
            if (sdh2.getStock().getStockCode() != null) {
                if (sdh2.getLocation() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location");
                    status = false;
                }
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

        PurHisDetail sdh = listPurDetail.get(row);
        if (sdh.getPurDetailKey() != null) {
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

    public void setCallBack(SelectionObserver callBack) {
        this.callBack = callBack;
    }

}
