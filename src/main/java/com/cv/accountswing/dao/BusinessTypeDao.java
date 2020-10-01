/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.BusinessType;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface BusinessTypeDao {
    public BusinessType save(BusinessType bt);
    public BusinessType findById(Integer id);
    public List<BusinessType> getAllBusinessType();
    public int delete(String id);
}
