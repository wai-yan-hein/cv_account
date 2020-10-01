/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.util.NumberUtil;
import com.cv.inv.entity.RetInDetailHis;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entry.editor.UnitAutoCompleter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private List<RetInDetailHis> listRetInDtail = new ArrayList();

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
        RetInDetailHis record;
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
                    return record.getStock().getStockCode();
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

    public void addNewRow() {
        if (hasEmptyRow()) {
            RetInDetailHis detailHis = new RetInDetailHis();
            listRetInDtail.add(detailHis);
            fireTableRowsInserted(listRetInDtail.size() - 1, listRetInDtail.size() - 1);
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
                return Double.class;
            case 7: //Amt
                return Double.class;
            default:
                return Object.class;
        }

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 2 || column == 7);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listRetInDtail == null) {
            return;
        }

        if (listRetInDtail.isEmpty()) {
            return;
        }

        try {
            RetInDetailHis record = listRetInDtail.get(row);

            switch (column) {
                case 0://code
                    if (value != null) {
                        record.setStock((Stock) value);
                        //record.setStockUnit(((Stock) value).getSaleUnit());
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    addNewRow();
                    break;
                case 1://Description
                    if (value != null) {
                        record.setStock((Stock) value);
                    }
                    break;
                case 2://Ex-date
                    if (value != null) {
                        record.setExpireDate(((RetInDetailHis) value).getExpireDate());
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    break;
                case 3://Qty
                    if (value != null) {
                        float qty = NumberUtil.NZeroFloat(value);
                        record.setQty(qty);
                        int x = Global.x + (Global.width / 2);
                        int y = Global.y + (Global.height / 2) - 200;
                        //Need to change
                        /*if (Global.listStockUnit != null && Global.listStockUnit.size() > 1) {
                            UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                    Global.listStockUnit, null);

                            if (unitPopup.isSelected()) {
                                record.setStockUnit(unitPopup.getSelUnit());
                            }
                        } else {
                            record.setStockUnit(Global.listStUnit.get(0));
                        }*/
                        if (record.getQty() != null && record.getPrice() != null) {
                            record.setAmount(calAmt(record));
                        }
                    }
                    if ((row + 1) <= listRetInDtail.size()) {
                        parent.setRowSelectionInterval(row + 1, row + 1);
                    }
                    parent.setColumnSelectionInterval(0, 0); //Move to Code
                    break;
                case 4://Std-w
                    if (value != null) {
                        float stdW = NumberUtil.NZeroFloat(value);
                        record.setStdWt(stdW);
                    }
                    parent.setColumnSelectionInterval(5, 5);
                    break;
                case 5://Unit
                    if (value != null) {
                        record.setStockUnit(((StockUnit) value));
                    } else {
                        record.setStockUnit(null);
                    }
                    parent.setColumnSelectionInterval(6, 6);
                    break;
                case 6://Price

                    if (value != null) {
                        Double price = NumberUtil.NZero(value);
                        record.setPrice(price);
                        if (record.getQty() != null && record.getPrice() != null) {
                            record.setAmount(calAmt(record));
                        }
                    }
                    parent.setColumnSelectionInterval(7, 7);
                    break;
                case 7://Amount
                    if (value != null) {
                        record.setAmount(NumberUtil.NZero(value));
                    }
                    break;

            }
            //parent.requestFocusInWindow();
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
    }

    public List<RetInDetailHis> getCurrentRow() {
        return this.listRetInDtail;
    }

    private Double calAmt(RetInDetailHis retInDetail) {
        Double amt;
        amt = retInDetail.getQty() * retInDetail.getPrice();
        return amt;
    }

    public List<RetInDetailHis> getRetInDetailHis() {
        return this.listRetInDtail;
    }

    public void addEmptyRow() {
        if (listRetInDtail != null) {
            RetInDetailHis record = new RetInDetailHis();
            listRetInDtail.add(record);
            fireTableRowsInserted(listRetInDtail.size() - 1, listRetInDtail.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listRetInDtail.isEmpty() || listRetInDtail == null) {
            status = true;
        } else {
            RetInDetailHis detailHis = listRetInDtail.get(listRetInDtail.size() - 1);
            if (detailHis.getStock() == null) {
                status = false;
            }
        }

        return status;
    }
    public void clearRetInTable() {
        this.listRetInDtail.clear();
        addEmptyRow();
    }
    
    public boolean isValidEntry() {
        boolean status = true;
        int row = 0;

        if (listRetInDtail != null) {
            for (RetInDetailHis record : listRetInDtail) {
                if (row < listRetInDtail.size() - 1) {
                    if (record.getStock().getStockCode() != null) {
                        if (NumberUtil.NZero(record.getQty()) <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        } else if (NumberUtil.NZero(record.getPrice()) <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Price must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        } else {
                            record.setUniqueId(row + 1);
                        }

                        row++;
                    }
                }
            }
        }

        if (row == 0 || listRetInDtail == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "No Sale record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        parent.setRowSelectionInterval(row, row);
        return status;
    }

}
