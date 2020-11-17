/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockReceiveDetailHis;
import com.cv.inv.entity.StockUnit;
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
public class StockReceiveTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockReceiveTableModel.class);
    private String[] columnNames = {"Option", "Vou No", "Order Item", "Item Code", "Rcv-Item",
        "Outstanding", "Exp-Date", "Qty", "Unit", "Balance"};
    private JTable parent;
    private List<StockReceiveDetailHis> listDetail = new ArrayList();
    private List<String> delList = new ArrayList();
    private SelectionObserver callBack;
    private String receiveId;

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public List<String> getDelList() {
        return delList;
    }

    public void setCallBack(SelectionObserver callBack) {
        this.callBack = callBack;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 2 || column == 5);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Option
            case 1: //Vou No
            case 2: //Order Medicine
            case 3: //Med Code
            case 4: //Rec-Medicine
            case 5: //Outstanding
            case 9: //Balance
                return String.class;
            case 6: //Exp-Date
                return String.class;
            case 7: //Qty
                return Float.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }

        try {
            StockReceiveDetailHis srdh = listDetail.get(row);

            switch (column) {
                case 0: //Option
                    return srdh.getRecOption();
                case 1: //Vou No
                    return srdh.getRefVou();
                case 2: //Order Medicine
                    if (srdh.getOrderMed() != null) {
                        return srdh.getOrderMed().getStockName();
                    } else {
                        return null;
                    }
                case 3: //Med Code
                    if (srdh.getRecMed() != null) {
                        return srdh.getRecMed().getStockCode();
                    } else {
                        return null;
                    }
                case 4: //Rec-Medicine
                    return srdh.getRecMed().getStockName();
                case 5: //Outstanding
                    if (srdh.getRecMed() != null) {
                        return srdh.getStrOutstanding();
                    } else {
                        return null;
                    }
                case 6: //Exp-Date
                    if (srdh.getExpDate() != null) {
                        return Util1.toDateStr(srdh.getExpDate(), "dd/MM/yyyy");
                    } else {
                        return null;
                    }
                case 7: //Qty
                    return srdh.getUnitQty();
                case 8: //Unit
                    if (srdh.getUnit() != null) {
                        return srdh.getUnit().getItemUnitCode();
                    } else {
                        return null;
                    }
                case 9: //Balance
                    return srdh.getBalance();
                default:
                    return null;
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
            StockReceiveDetailHis record = listDetail.get(row);
            switch (column) {
                case 0: //Option
                    //srdh.setRecOption((String)value);
                    break;
                case 1: //Vou No
                    //srdh.setRefVou((String)value);
                    break;
                case 2: //Order Medicine
                    //srdh.getOrderMed().setMedId((String)value);
                    break;
                case 3: //Med Code
                    if (value != null) {
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            record.setRecMed(stock);
                            record.setUnitQty(1.0f);
                            record.setUnit(stock.getSaleUnit());
                            record.setRecOption("Receive");
                            record.setRefVou(getReceiveId());

                            addEmptyRow();
                            parent.setColumnSelectionInterval(7, 7);
                        }
                    }
                    break;
                case 4: //Rec-Medicine
                    break;
                case 5: //Outstanding
                    break;
                case 6: //Exp-Date

                    break;
                case 7: //Qty
                    if (value != null) {
                        Float qty = NumberUtil.NZeroFloat(value);
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);

                        } else {
                            record.setUnitQty(qty);
                            if ((row + 1) <= listDetail.size()) {
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            }
                            parent.setColumnSelectionInterval(3, 3); //Move to Code

                        }
                    }
                    break;
                case 8: //Unit
                    if (value != null) {
                        if (value instanceof StockUnit) {
                            StockUnit st = (StockUnit) value;

                            parent.setColumnSelectionInterval(5, 5);
                        }
                    }
                    break;
                case 9: //Balance
                    break;
                default:
                    System.out.println("ReceiveTableModel invalid index.");
            }
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        parent.requestFocusInWindow();
        fireTableCellUpdated(row, column);
//        if (column != 0) {
//            fireTableCellUpdated(row, column);
//        }
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addEmptyRow();
        fireTableDataChanged();
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                StockReceiveDetailHis record = new StockReceiveDetailHis();

                record.setRecMed(new Stock());
                listDetail.add(record);

                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    public boolean hasEmptyRow() {
        if (listDetail == null) {
            return false;
        }

        if (listDetail.isEmpty()) {
            return false;
        }

        StockReceiveDetailHis record = listDetail.get(listDetail.size() - 1);

        if (record.getRecMed().getStockCode() != null) {
            return false;
        } else {
            return true;
        }
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        StockReceiveDetailHis sdh = listDetail.get(row);
        if (sdh.getTranId() != null) {
            delList.add(sdh.getTranId().toString());
        }

        listDetail.remove(row);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        //  callBack.selected("STM-TOTAL", "STM-TOTAL");
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
    }

    public List<StockReceiveDetailHis> getDetail() {
        List<StockReceiveDetailHis> listRetInDetail = new ArrayList();
        for (StockReceiveDetailHis pdh2 : listDetail) {
            if (pdh2.getRecMed() != null) {
                if (pdh2.getRecMed().getStockCode() != null) {
                    listRetInDetail.add(pdh2);
                }
            }
        }

        return listRetInDetail;
    }

    public void setStockReceiveDetailList(List<StockReceiveDetailHis> listIssueDetail) {
        this.listDetail = listIssueDetail;

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
        fireTableCellUpdated(listDetail.size() - 1, listDetail.size() - 1);

        fireTableDataChanged();
    }
}
