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
import com.cv.inv.entity.StockIssueDetailHis;
import com.cv.inv.entity.StockOutstanding;
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
public class IssueTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueTableModel.class);
    private String[] columnNames = {"Option", "Vou No", "Item Code", "Description",
        "T-Code", "Trader Name", "Outstanding", "Qty", "Unit", "Balance"};
    private JTable parent;
    private SelectionObserver callBack;
    private List<StockIssueDetailHis> listDetail = new ArrayList();
    private String issueId;
    private List<String> delList = new ArrayList();

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
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
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0: //Option
                return false;
            case 1: //Vou No
                return false;
            case 2: //Med Code
                return true;//isEditable(getValueAt(row, 0).toString());
            case 3: //Medicine
                return false;
            case 4: //T-Code
                return isEditable(getValueAt(row, 0).toString());
            case 5: //Trader Name
                return false;
            case 6: //Outstanding
                return false;
            case 7: //Exp-Date
                return true;
            case 8: //Qty
                return true;
            case 9: //Unit
                return false;
            case 10: //Balance
                return false;
            default:
                return false;
        }
    }

    private boolean isEditable(String recOption) {
        return recOption.equals("Borrow");
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
            StockIssueDetailHis sidh = listDetail.get(row);
            switch (column) {
                case 0://option
                    return sidh.getIssueOpt();
                case 1://vouno
                    return sidh.getRefVou();
                case 2://item code
                    return sidh.getIssueStock().getStockCode();
                case 3://desc
                    return sidh.getIssueStock().getStockName();
                case 4://t-code
                //  return sidh.getTrader().getTraderId();
                case 5://trader name
                //  return sidh.getTrader().getTraderName();
                case 6://outstand
                    return sidh.getStrOutstanding();
                case 7://qty
                    return sidh.getUnitQty();
                case 8://unit
                    return sidh.getItemUnit();
                case 9://bal
                    return sidh.getBalance();
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Option
            case 1: //Vou No
            case 2: //Med Code
            case 3: //Medicine
            case 4: //T-Code
            case 5: //Trader Name
            case 6: //Outstanding
            case 9: //Balance
                return String.class;
            case 7: //Qty
                return Float.class;
            default:
                return Object.class;
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
            StockIssueDetailHis record = listDetail.get(row);
            switch (column) {
                case 0: //Option
                    //srdh.setRecOption((String)value);
                    break;
                case 1: //Vou No
                    //srdh.setRefVou((String)value);
                    break;
                case 2: //Med Code
                    if (value != null) {
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            record.setIssueStock(stock);
                            record.setUnitQty(1.0f);
                            record.setItemUnit(stock.getSaleUnit());
                            record.setIssueOpt("Issue");
                            record.setRefVou(getIssueId());
                            Global.hasQtyInSmallest.put(record.getIssueStock().getStockCode() + "-"
                                    + record.getItemUnit().getItemUnitCode(), record.getSmallestQty());
                            addEmptyRow();
                            parent.setColumnSelectionInterval(7, 7);
                        }
                    }
                    break;
                case 3: //Medicine
                    break;
                case 4: //T-Code                    

                    break;
                case 5: //Trader Name
                    break;
                case 6: //Outstanding
                    break;
                case 7: //Qty
                    if (value != null) {
                        Float qty = NumberUtil.NZeroFloat(value);
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(Global.parentForm, "Qty must be positive value.",
                                    "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);

                        } else {
                            record.setUnitQty(qty);
//                            if (record.getItemUnit() != null) {
//                                String unit = record.getItemUnit().getItemUnitCode();
//                                Float smallqty = (float) Global.hasQtyInSmallest.get(record.getIssueStock().getStockCode() + "-" + unit);
//                                record.setSmallestQty(record.getUnitQty() * smallqty);
//                                float tmpBalance = NumberUtil.NZeroFloat(record.getOutsBalance())
//                                        - (record.getUnitQty() * smallqty);
//                                record.setBalance(tmpBalance + unit);
//                            }
                            //  record.setAmount(Util1.getFloat(record.getUnitQty()) * Util1.getDouble(record.getCostPrice()));
                            if ((row + 1) <= listDetail.size()) {
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            }
                            parent.setColumnSelectionInterval(0, 0); //Move to Code

                        }
                    }
                    break;
                case 9: //Unit
                    if (value != null) {
                        if (value instanceof StockUnit) {
                            StockUnit st = (StockUnit) value;
                            record.setItemUnit(st);
                            ///

                            //    String toUnit = record.getUnit().getItemUnitCode();
                            //   Float calAmount = calPrice(record, toUnit);
                            //   record.setCostPrice(Util1.getDouble(calAmount));
                            parent.setColumnSelectionInterval(5, 5);
                        }
                    }
                    break;

                case 10: //Balance
                    break;
                default:
                    System.out.println("IssueTableModel invalid index.");
            }
        } catch (Exception ex) {
            LOGGER.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        parent.requestFocusInWindow();
        fireTableCellUpdated(row, column);
        callBack.selected("STM-TOTAL", "STM-TOTAL");
    }

//    public void add(StockOutstanding outs) {
//        if (listDetail != null) {
//            StockIssueDetailHis sidh = new StockIssueDetailHis();
//
//            sidh.setBalance(outs.getQtyStr());
//            sidh.setOutsBalance(outs.getBalanceQty());
//            sidh.setIssueStock(outs.getStock());
//            sidh.setIssueOpt(outs.getTranOption());
//            sidh.setRefVou(outs.getInvId());
//            sidh.setStrOutstanding(outs.getQtyStr());
//
//            listDetail.add(sidh);
//            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
//        }
//    }
    public void removeListDetail() {
        this.listDetail.clear();
        addEmptyRow();
        fireTableDataChanged();
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                StockIssueDetailHis record = new StockIssueDetailHis();

                record.setIssueStock(new Stock());
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

        StockIssueDetailHis record = listDetail.get(listDetail.size() - 1);

        if (record.getIssueStock().getStockCode() != null) {
            return false;
        } else {
            return true;
        }
    }

    public void setCallBack(SelectionObserver callBack) {
        this.callBack = callBack;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public List<StockIssueDetailHis> getDetail() {
        List<StockIssueDetailHis> listRetInDetail = new ArrayList();
        for (StockIssueDetailHis pdh2 : listDetail) {
            if (pdh2.getIssueStock() != null) {
                if (pdh2.getIssueStock().getStockCode() != null) {
                    listRetInDetail.add(pdh2);
                }
            }
        }

        return listRetInDetail;
    }

    public List<String> getDelList() {
        return delList;
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        StockIssueDetailHis sdh = listDetail.get(row);
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

    public void setStockIssueDetailList(List<StockIssueDetailHis> listIssueDetail) {
        this.listDetail = listIssueDetail;

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
        fireTableCellUpdated(listDetail.size() - 1, listDetail.size() - 1);

        fireTableDataChanged();
    }
}
