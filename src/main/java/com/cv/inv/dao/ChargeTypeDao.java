/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.ChargeType;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface ChargeTypeDao {

    public ChargeType save(ChargeType chargeType);

    public List<ChargeType> findAll();

    public int delete(String id);
}
