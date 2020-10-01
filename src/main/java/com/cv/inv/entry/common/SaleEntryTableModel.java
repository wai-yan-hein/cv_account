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
import com.cv.accountswing.util.UnitAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.SaleDetailHis;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
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
        "Qty", "Std-Wt", "Unit", "Sale Price", "Discount", "Discount Type", "Amount", "Location"};

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
                return record.getStockCode().getStockCode() != null;
            case 3://Qty
                return record.getStockCode().getStockCode() != null;
            case 4://Std-Wt
                return true;
            case 5://Unit
                return false;
            case 10://Loc
                return record.getStockCode().getStockCode() != null;
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
                    if (record.getStockCode() == null) {
                        return null;
                    } else {
                        return record.getStockCode().getStockCode();
                    }
                case 1://Name
                    if (record.getStockCode() == null) {
                        return null;
                    } else {
                        return record.getStockCode().getStockName();
                    }
                case 2://exp-date
                    return Util1.toDateStr(record.getExpDate(), "dd/MM/yyyy");
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
                    return record.getDiscount();
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
                    if (!((String) value).isEmpty()) {
                        String[] strList = value.toString().split("@");
                        stockInfo.getStockInfo(strList[0]);
                        parent.setColumnSelectionInterval(3, 3);
                    }
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
                    float qty = Util1.getFloat(value);
                    record.setQuantity(qty);
                    if (listDetail.get(parent.getSelectedRow()).getStockCode() != null) {
                        String stockId = listDetail.get(parent.getSelectedRow()).getStockCode().getStockCode();
                        int x = Global.x + (Global.width / 2);
                        int y = Global.x + (Global.height / 2) - 200;
                        if (Global.listStockUnit != null && Global.listStockUnit.size() > 1) {
                            UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y, Global.listStockUnit,
                                    Global.parentForm);
                            if (unitPopup.isSelected()) {
                                record.setItemUnit(unitPopup.getSelUnit());
                                if (record.getItemUnit() != null) {
                                    String key = stockId;
                                    record.setPrice(stockUp.getPrice(key, getCusType(), qty));
                                   
                                }
                            }
                        } else {
                            record.setItemUnit(Global.listStockUnit.get(0));
                        }

                    }
                    parent.setColumnSelectionInterval(6, 6);
                    break;

                case 4://Std-Wt
                    if (value != null) {
                        record.setStdWeight(Util1.getFloat(value));
                    }
                    parent.setColumnSelectionInterval(6, 6);
                    break;

                case 5://Unit
                    if (value != null) {
                        record.setItemUnit((StockUnit) value);
                    } else {
                        record.setItemUnit(null);
                    }
                    break;
                case 6://Discount
                    record.setDiscount(NumberUtil.NZero(value));
                case 9: //Amount
                    if (value != null) {
                        if (NumberUtil.NZeroInt(record.getQuantity()) != 0) {
                            double amount = NumberUtil.FloatZero(value);
                            record.setPrice(amount / NumberUtil.NZeroInt(record.getQuantity()));
                            record.setDiscount(null);
                            record.setAmount(amount);
                            isAmount = true;
                            fireTableCellUpdated(row, 6);
                        }
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
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        parent.requestFocusInWindow();

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
        if (detailHis.getStockCode() != null) {
            return detailHis.getStockCode().getStockCode() == null;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleDetailHis detailHis = new SaleDetailHis();
                detailHis.setStockCode(new Stock());
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
    public void setStock(Stock stock, int pos) {
        if (listDetail == null) {
            return;
        }
        if (listDetail.isEmpty()) {
            return;
        }
        SaleDetailHis record = listDetail.get(pos);
        record.setStockCode(stock);
        record.setStdWeight(stock.getSaleMeasure());
        //record.setPrice(stock.getSalePriceN());
        if (Util1.getPropValue("system.sale.detail.location").equals("Y")) {
            record.setLocation(location);
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableCellUpdated(pos, 0);
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

}
