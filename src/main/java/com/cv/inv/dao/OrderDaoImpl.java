/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.Order;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class OrderDaoImpl extends AbstractDao<String, Order> implements OrderDao {

    @Override
    public Order save(Order order) {
        persist(order);
        return order;
    }

    @Override
    public int delete(String id) {
        return 0;
    }

    @Override
    public List<Order> findActiveOrder(String fromDate, String toDate, String cusId, String orderCode) {
        String hsql = "select o from Order o ";
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.orderDate) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.orderDate) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.orderDate) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and date(o.orderDate) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.orderDate) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.orderDate) <= '" + toDate + "'";
            }
        }
        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.trader= '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.trader= '" + cusId + "'";
            }
        }
        if (!orderCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.orderCode = '" + orderCode + "'";
            } else {
                strFilter = strFilter + " and o.orderCode= '" + orderCode + "'";
            }
        }
        if (!strFilter.isEmpty()) {
            hsql = hsql + " where " + strFilter + " and o.isOrder = true";
        }
        return findHSQL(hsql);

    }

}
