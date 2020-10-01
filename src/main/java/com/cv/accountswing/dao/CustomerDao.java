/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Customer;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface CustomerDao {
    public Customer save(Customer cus);
    public Customer findById(Integer id);
    public List<Customer> search(String code, String name, String address, 
            String phone, String compCode);
    public int delete(Integer id);
}
