/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.OrderDetail;
import com.cv.inv.entity.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class OrderTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderTableModel.class);
    private List<OrderDetail> listOrder = new ArrayList();
    private String[] columnNames = {"Code", "Discription", "Qty", "Price", "Amount"};
    private JTable table;
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }
    

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 1 && column != 4;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            default:
                return Float.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            OrderDetail order = listOrder.get(row);

            switch (column) {
                case 0: //Name
                    return order.getStock().getStockCode();
                case 1:
                    return order.getStock().getStockName();
                case 2: //qty
                    return order.getQty();
                case 3:
                    return order.getPrice();
                case 4:
                    return order.getAmount();
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
        OrderDetail od = listOrder.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0:
                        if (value instanceof Stock) {
                            Stock stock = (Stock) value;
                            od.setStock(stock);
                            od.setQty(1.0f);
                            table.setColumnSelectionInterval(2, 2);
                            addNewRow();
                        }
                        break;
                    case 2:
                        if (Util1.getFloat(value) > 0) {
                            od.setQty(Util1.getFloat(value));
                            table.setColumnSelectionInterval(3, 3);
                        }
                        break;
                    case 3:
                        if (Util1.getFloat(value) > 0) {
                            od.setPrice(Util1.getFloat(value));
                            table.setRowSelectionInterval(row + 1, row + 1);
                            table.setColumnSelectionInterval(0, 0);
                        }

                        break;

                }
                calAmount(od);
                observer.selected("CAL-TOTAL", "CAL-TOTAL");
                fireTableRowsUpdated(row, row);
                table.requestFocusInWindow();
            }
        } catch (Exception e) {
            LOGGER.error("setValueAT :" + e.getMessage());
        }
    }

    private void calAmount(OrderDetail od) {
        float amt = Util1.getFloat(od.getQty()) * Util1.getFloat(od.getPrice());
        od.setAmount(amt);
    }

    @Override
    public int getRowCount() {
        if (listOrder == null) {
            return 0;
        }
        return listOrder.size();
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

    public List<OrderDetail> getListOrder() {
        return listOrder;
    }

    public void setListOrder(List<OrderDetail> listOrder) {
        this.listOrder = listOrder;
        fireTableDataChanged();
    }

    public OrderDetail getOrder(int row) {
        return listOrder.get(row);
    }

    public void deleteOrder(int row) {
        if (!listOrder.isEmpty()) {
            listOrder.remove(row);
            fireTableRowsDeleted(0, listOrder.size());
        }

    }

    public void addOrder(OrderDetail order) {
        listOrder.add(order);
        fireTableRowsInserted(listOrder.size() - 1, listOrder.size() - 1);
    }

    public void setOrder(int row, OrderDetail order) {
        if (!listOrder.isEmpty()) {
            listOrder.set(row, order);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            OrderDetail od = new OrderDetail();
            od.setStock(new Stock());
            listOrder.add(od);
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (!listOrder.isEmpty()) {
            OrderDetail get = listOrder.get(listOrder.size() - 1);
            if (get.getStock() == null) {
                status = false;
            }
        }
        return status;

    }

    public void clear() {
        if (listOrder != null) {
            listOrder.clear();
        }
    }

}
