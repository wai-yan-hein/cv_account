/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Order;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author lenovo
 */
@Component
public class OrderSearchByCustomerTableModel extends AbstractTableModel {

    private final static Logger log = Logger.getLogger(OrderSearchByCustomerTableModel.class.getName());
    private List<Order> listOrder = new ArrayList();
    private final String[] columnNames = {"Date", "Order No", "Amount"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
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

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Remark
                return Float.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listOrder == null) {
            return null;
        }

        if (listOrder.isEmpty()) {
            return null;
        }
        try {
            Order order = listOrder.get(row);

            switch (column) {
                case 0://date
                    return Util1.toDateStr(order.getOrderDate(), "dd/MM/yyyy");
                case 1://vou-no
                    return order.getOrderCode();
                case 2://remark
                    return order.getOrderTotal();

            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<Order> getListOrder() {
        return listOrder;
    }

    public void setListOrder(List<Order> listOrder) {
        this.listOrder = listOrder;
        fireTableDataChanged();
    }

    public Order getOrder(int row) {
        if (listOrder != null) {
            if (!listOrder.isEmpty()) {
                return listOrder.get(row);
            }
        }
        return null;
    }

}
