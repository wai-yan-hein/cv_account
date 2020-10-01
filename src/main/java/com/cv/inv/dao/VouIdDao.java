/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.CompoundKey;
import com.cv.inv.entity.VouId;

/**
 *
 * @author lenovo
 */
public interface VouIdDao {
    public VouId save(VouId vouId);
    public Object getMax(String machineName, String vouType, String vouPeriod) throws Exception;
    public Object find(CompoundKey key);
    
    
}
