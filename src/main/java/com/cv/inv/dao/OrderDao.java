/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.Order;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface OrderDao {

    public Order save(Order order);

    public List<Order> findActiveOrder(String fromDate, String toDate, String cusId, String orderCode);

    public int delete(String id);
}
