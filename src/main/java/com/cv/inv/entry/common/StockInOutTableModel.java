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
import com.cv.inv.entity.StockInOut;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.service.StockInOutService;
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
public class StockInOutTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockInOutTableModel.class);
    private String[] columnNames = {"Date", "Description", "Stock Code", "Stock Name", "Location",
        "In-Qty", "In-Weight", "In-Unit", "Out-Qty", "Out-Weight", "Out-Unit", "Remark"};
    private JTable parent;
    private List<StockInOut> listStock = new ArrayList();
    @Autowired
    private StockInOutService stockInOutService;

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
        StockInOut stock = listStock.get(row);

        if (stock == null) {
            return null;
        }

        switch (column) {
            case 0:
                if (stock.getDate() != null) {
                    return Util1.toDateStr(stock.getDate(), "dd/MM/yyyy");
                } else {
                    return null;
                }
            case 1: //Option
                return stock.getDescription();
            case 2: //Code
                return stock.getStock().getStockCode();
            case 3: //Name
                return stock.getStock().getStockName();
            case 4:
                return stock.getLocation();
            case 5: //in-qty
                return stock.getInQty();
            case 6: //In-Wt
                return stock.getInWeight();
            case 7: //in-Unit
                return stock.getInUnit();
            case 8: //out-qty
                return stock.getOutQty();
            case 9: //out -wt
                return stock.getOutWeight();
            case 10://out -unit
                return stock.getOutUnit();
            case 11:
                return stock.getRemark();
            default:
                return new Object();

        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            StockInOut detailHis = new StockInOut();
            detailHis.setStock(new Stock());
            listStock.add(detailHis);
            fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
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
                return StockUnit.class;
            case 11:
                return String.class;
            default:
                return Object.class;
        }

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 3);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        StockInOut stock = listStock.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0:
                        if (Util1.isValidDateFormat(value.toString(), "dd/MM/yyyy")) {
                            stock.setDate(Util1.toDate(value, "dd/MM/yyyy"));
                        } else {
                            if (value.toString().length() == 8) {
                                String toFormatDate = Util1.toFormatDate(value.toString());
                                stock.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                            } else {
                                stock.setDate(Util1.getTodayDate());
                                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date");
                            }
                        }
                        setColumnSelection(1);
                        break;
                    case 1:
                        stock.setDescription(value.toString());
                        break;
                    case 2:
                        if (value instanceof Stock) {
                            stock.setStock((Stock) value);
                        }
                        if (stock.getLocation() == null) {
                            setColumnSelection(4);
                        } else {
                            setColumnSelection(5);
                        }
                        break;
                    case 4:
                        if (value instanceof Location) {
                            stock.setLocation((Location) value);
                        }
                        setColumnSelection(5);
                        break;
                    case 5:
                        stock.setInQty(Util1.getFloat(value));
                        break;
                    case 6:
                        stock.setInWeight(Util1.getFloat(value));
                        break;
                    case 7:
                        if (value instanceof StockUnit) {
                            stock.setInUnit((StockUnit) value);
                        }
                        setColumnSelection(8);
                        break;
                    case 8:
                        stock.setOutQty(Util1.getFloat(value));
                        break;
                    case 9:
                        stock.setOutWeight(Util1.getFloat(value));
                        break;
                    case 10:
                        if (value instanceof StockUnit) {
                            stock.setOutUnit((StockUnit) value);
                        }
                        setColumnSelection(11);
                        break;
                    case 11:
                        stock.setRemark(String.valueOf(value));
                        break;

                }
            }
            save(stock);
        } catch (HeadlessException e) {
            LOGGER.error("setValueAt :" + e.getMessage());
        }
    }

    private void setColumnSelection(int column) {
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    private void save(StockInOut sio) {
        try {
            if (isValidEntry(sio)) {
                StockInOut save = stockInOutService.save(sio);
                if (save != null) {
                    sio.setId(save.getId());
                    addNewRow();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Save StockInOut :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "ERROR SAVE", JOptionPane.ERROR_MESSAGE);
        }

    }

    private boolean isValidEntry(StockInOut sio) {
        boolean status = true;
        if (Util1.getFloat(sio.getInQty()) <= 0 && Util1.getFloat(sio.getOutQty()) <= 0) {
            status = false;
        }
        if (sio.getInUnit() == null && sio.getOutUnit() == null) {
            status = false;
        }
        if (Util1.getFloat(sio.getInWeight()) <= 0 && Util1.getFloat(sio.getOutQty()) <= 0) {
            status = false;
        }
        if (sio.getLocation() == null) {
            status = false;
        }
        if (status) {
            StockUnit toUnit = sio.getStock().getPurPriceUnit();
            Integer pattern = sio.getStock().getPattern().getPatternId();
            float inWt = Util1.getFloat(sio.getInWeight());
            float outWt = Util1.getFloat(sio.getOutWeight());

            if (sio.getInUnit() != null) {
                String inUnit = sio.getInUnit().getItemUnitCode();
                sio.setSmallInWeight(getSmallestWeight(inWt, inUnit, toUnit.getItemUnitCode(), pattern));
                sio.setSmallInUnit(toUnit);
            }
            if (sio.getOutUnit() != null) {
                String outUnit = sio.getOutUnit().getItemUnitCode();
                sio.setSamllOutWeight(getSmallestWeight(outWt, outUnit, toUnit.getItemUnitCode(), pattern));
                sio.setSmallOutUnit(sio.getStock().getPurPriceUnit());
            }
        }
        return status;

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
                    StockInOut get = listStock.get(parent.getSelectedRow());
                    if (get.getId() != null) {
                        stockInOutService.delete(get.getId());
                        listStock.remove(parent.getSelectedRow());
                    }
                }
            }
        } else {
            sWt = weight;
        }
        return sWt;
    }

    public List<StockInOut> getCurrentRow() {
        return this.listStock;
    }

    public List<StockInOut> getRetInDetailHis() {
        return this.listStock;
    }

    public void addEmptyRow() {
        if (listStock != null) {
            StockInOut stock = new StockInOut();
            stock.setDate(Util1.getTodayDate());
            stock.setLocation(Global.defaultLocation);
            stock.setStock(new Stock());
            listStock.add(stock);
            fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = false;
        if (listStock.isEmpty() || listStock == null) {
            status = true;
        } else {
            StockInOut detailHis = listStock.get(listStock.size() - 1);
            if (detailHis.getStock() == null) {
                status = false;
            }
        }

        return status;
    }

    public List<StockInOut> getListStock() {
        return listStock;
    }

    public void setListStock(List<StockInOut> listStock) {
        this.listStock = listStock;
        fireTableDataChanged();
    }

}
