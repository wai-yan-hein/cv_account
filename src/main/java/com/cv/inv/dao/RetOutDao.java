/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.RetOutDetailHis;

/**
 *
 * @author lenovo
 */
public interface RetOutDao {

    public RetOutDetailHis save(RetOutDetailHis retOutDetailHis);

    public void delete(String retOutId);

}
