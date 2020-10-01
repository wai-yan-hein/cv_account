/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Supplier;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface SupplierDao {
    public Supplier save(Supplier sup);
    public Supplier findById(Integer id);
    public List<Supplier> search(String code, String name, String address, 
            String phone, String compCode);
    public int delete(Integer id);
}
