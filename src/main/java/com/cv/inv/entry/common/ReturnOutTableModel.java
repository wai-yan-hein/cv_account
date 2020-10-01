/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.util.NumberUtil;
import com.cv.inv.entity.RetOutDetailHis;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entry.editor.UnitAutoCompleter;
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
public class ReturnOutTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReturnOutTableModel.class);
    private String[] columnNames = {"Code", "Description", "Exp-Date",
        "Qty", "Std-W", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<RetOutDetailHis> listDetail;

    public ReturnOutTableModel(List<RetOutDetailHis> listDetail) {
        this.listDetail = listDetail;

    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 2 || column == 7);
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
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
        RetOutDetailHis record;
        try {
            record = listDetail.get(row);
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

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        try {
            RetOutDetailHis record = listDetail.get(row);

            switch (column) {
                case 0://code
                    if (value != null) {
                        record.setStock((Stock) value);
                        //record.setStockUnit(((Stock) value).getSaleUnit());
                    }
                    parent.setColumnSelectionInterval(3, 3);
                    addEmptyRow();
                    break;
                case 1://Description
                    if (value != null) {
                        record.setStock((Stock) value);
                    }
                    break;
                case 2://Ex-date
                    if (value != null) {
                        record.setExpireDate(((RetOutDetailHis) value).getExpireDate());
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
                        /*if (Global.listStUnit != null && Global.listStUnit.size() > 1) {
                            UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                    Global.listStUnit, Global.parentForm);

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
                    if ((row + 1) <= listDetail.size()) {
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

    private Double calAmt(RetOutDetailHis retInDetail) {
        Double amt;
        amt = retInDetail.getQty() * retInDetail.getPrice();
        return amt;
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.isEmpty() || listDetail == null) {
            status = true;
        } else {
            RetOutDetailHis detailHis = listDetail.get(listDetail.size() - 1);
            if (detailHis.getStock() == null) {
                status = false;
            }
        }

        return status;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            RetOutDetailHis record = new RetOutDetailHis();
            listDetail.add(record);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            RetOutDetailHis detailHis = new RetOutDetailHis();
            listDetail.add(detailHis);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }

    }

    public boolean isValidEntry() {
        boolean status = true;
        int row = 0;

        if (listDetail != null) {
            for (RetOutDetailHis record : listDetail) {
                if (row < listDetail.size() - 1) {
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

        if (row == 0) {
            JOptionPane.showMessageDialog(Global.parentForm,
                    "No purchase record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        parent.setRowSelectionInterval(row, row);
        return status;
    }

    public void clearRetOutTable() {
        this.listDetail.clear();
        addEmptyRow();
    }
     public List<RetOutDetailHis> getRetOutDetailHis() {
        return this.listDetail;
     }
    

}
