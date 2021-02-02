/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.StockInOutDetail;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.swing.JFormattedTextField;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class StockInOutTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(StockInOutTableModel.class);
    private String[] columnNames = {"Description", "Remark", "Stock Code", "Stock Name", "Location",
        "In-Qty", "In-Weight", "In-Unit", "Out-Qty", "Out-Weight", "Out-Unit"};
    private JTable parent;
    private List<StockInOutDetail> listStock = new ArrayList();

    private JFormattedTextField inTotal;
    private JFormattedTextField outTotal;

    public JFormattedTextField getInTotal() {
        return inTotal;
    }

    public void setInTotal(JFormattedTextField inTotal) {
        this.inTotal = inTotal;
    }

    public JFormattedTextField getOutTotal() {
        return outTotal;
    }

    public void setOutTotal(JFormattedTextField outTotal) {
        this.outTotal = outTotal;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        if (listStock == null) {
            return 0;
        }
        return listStock.size();
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
        StockInOutDetail stockInOut = listStock.get(row);

        if (stockInOut == null) {
            return null;
        }

        switch (column) {
            case 0: //Option
                return stockInOut.getDescription();
            case 1:
                return stockInOut.getRemark();
            case 2: //Code
                if (Util1.isNull(Global.sysProperties.get("system.use.usercode"), "0").equals("1")) {
                    return stockInOut.getStock().getUserCode();
                } else {
                    return stockInOut.getStock().getStockCode();
                }
            case 3: //Name
                return stockInOut.getStock().getStockName();
            case 4:
                return stockInOut.getLocation();
            case 5: //in-qty
                return stockInOut.getInQty();
            case 6: //In-Wt
                return stockInOut.getInWeight();
            case 7: //in-Unit
                return stockInOut.getInUnit();
            case 8: //out-qty
                return stockInOut.getOutQty();
            case 9: //out -wt
                return stockInOut.getOutWeight();
            case 10://out -unit
                return stockInOut.getOutUnit();
            default:
                return null;

        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1: //option
                return String.class;
            case 2: //code
                return String.class;
            case 3: //name
                return String.class;
            case 4://loc
                return Location.class;
            case 5: //in-qty
                return Float.class;
            case 6: //in -wt
                return Float.class;
            case 7: //in-unit
                return StockUnit.class;
            case 8: //out-qty
                return Float.class;
            case 9: //out-wt
                return Float.class;
            case 10://out -wt
                return String.class;
            default:
                return Object.class;
        }

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        StockInOutDetail stockInOut = listStock.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0:
                        stockInOut.setDescription(value.toString());
                        break;
                    case 1:
                        stockInOut.setRemark(value.toString());
                        break;
                    case 2:
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            stockInOut.setStock(stock);
                            stockInOut.setLocation(Global.defaultLocation);
                            stockInOut.setInWeight(stock.getPurWeight());
                            stockInOut.setOutWeight(stock.getPurWeight());
                            stockInOut.setInUnit(stock.getPurUnit());
                            stockInOut.setOutUnit(stock.getPurUnit());
                            setColumnSelection(4);
                        }
                        addNewRow();
                        break;
                    case 4:
                        if (value instanceof Location) {
                            stockInOut.setLocation((Location) value);
                        }
                        setColumnSelection(5);
                        break;
                    case 5:
                        stockInOut.setInQty(Util1.getFloat(value));
                        stockInOut.setOutQty(null);
                        stockInOut.setOutWeight(null);
                        stockInOut.setOutUnit(null);
                        break;
                    case 6:
                        stockInOut.setInWeight(Util1.getFloat(value));
                        break;
                    case 7:
                        if (value instanceof StockUnit) {
                            stockInOut.setInUnit((StockUnit) value);
                        }
                        setColumnSelection(8);
                        break;
                    case 8:
                        stockInOut.setOutQty(Util1.getFloat(value));
                        stockInOut.setInQty(null);
                        stockInOut.setInWeight(null);
                        stockInOut.setInUnit(null);
                        break;
                    case 9:
                        stockInOut.setOutWeight(Util1.getFloat(value));
                        break;
                    case 10:
                        if (value instanceof StockUnit) {
                            stockInOut.setOutUnit((StockUnit) value);
                        }
                        break;
                }
            }
            calStock(stockInOut);
            fireTableRowsUpdated(row, row);
            if (Util1.getFloat(stockInOut.getSmallInWeight()) > 0 || Util1.getFloat(stockInOut.getSmallInWeight()) > 0) {
                if (stockInOut.getLocation() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location");
                }
                parent.setRowSelectionInterval(row + 1, row + 1);
                parent.setColumnSelectionInterval(0, 0);
            }
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    private void setColumnSelection(int column) {
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (StockInOutDetail od : listStock) {
            if (od.getStock().getStockCode() != null) {
                if (Util1.getFloat(od.getSmallInWeight()) < 0 && Util1.getFloat(od.getSmallOutWeight()) < 0) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Could no saved. Need Unit Relation.");
                }
                if (od.getLocation() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                }
            }
        }
        return status;

    }

    private void calStock(StockInOutDetail od) {
        if (od.getStock().getPurUnit() != null) {
            float inWt = Util1.getFloat(od.getInWeight());
            float inQty = Util1.getFloat(od.getInQty());
            float outWt = Util1.getFloat(od.getOutWeight());
            float outQty = Util1.getFloat(od.getOutQty());
            StockUnit purUnit = od.getStock().getPurUnit();
            String pattern = od.getStock().getPattern().getPatternCode();
            if (od.getInUnit() != null) {
                String inUnit = od.getInUnit().getItemUnitCode();
                od.setSmallInWeight(getSmallestWeight(inWt, inUnit, purUnit.getItemUnitCode(), pattern) * inQty);
                od.setSmallInUnit(purUnit);
                od.setSmallOutWeight(0.0f);
                od.setSmallOutUnit(purUnit);
            }
            if (od.getOutUnit() != null) {
                String outUnit = od.getOutUnit().getItemUnitCode();
                od.setSmallOutWeight(getSmallestWeight(outWt, outUnit, purUnit.getItemUnitCode(), pattern) * outQty);
                od.setSmallOutUnit(purUnit);
                od.setSmallInWeight(0.0f);
                od.setSmallInUnit(purUnit);

            }
            calTotal();
        }
    }

    private void calTotal() {
        float inTtl = 0.0f;
        float outTtl = 0.0f;
        for (StockInOutDetail od : listStock) {
            inTtl += Util1.getFloat(od.getInQty());
            outTtl += Util1.getFloat(od.getOutQty());
        }
        this.inTotal.setValue(inTtl);
        this.outTotal.setValue(outTtl);
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
                    StockInOutDetail get = listStock.get(parent.getSelectedRow());
                    if (get.getCode() != null) {
                        //stockInOutService.delete(get.getCode());
                        listStock.remove(parent.getSelectedRow());
                    }
                }
            }
        } else {
            sWt = weight;
        }
        return sWt;
    }

    public List<StockInOutDetail> getCurrentRow() {
        return this.listStock;
    }

    public List<StockInOutDetail> getRetInDetailHis() {
        return this.listStock;
    }

    public List<StockInOutDetail> getListStock() {
        return listStock;
    }

    public void setListStock(List<StockInOutDetail> listStock) {
        this.listStock = listStock;
        calTotal();
        fireTableDataChanged();
    }

    public StockInOutDetail getStockInout(int row) {
        if (listStock != null) {
            return listStock.get(row);
        } else {
            return null;
        }
    }

    public void addNewRow() {
        if (listStock != null) {
            if (hasEmptyRow()) {
                StockInOutDetail pd = new StockInOutDetail();
                pd.setStock(new Stock());
                pd.setLocation(Global.defaultLocation);
                listStock.add(pd);
                fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listStock.size() > 1) {
            StockInOutDetail get = listStock.get(listStock.size() - 1);
            if (get.getStock().getStockCode() == null) {
                status = false;
            }
        }
        return status;
    }

    public void clear() {
        if (listStock != null) {
            listStock.clear();
            fireTableDataChanged();
        }
    }
}
