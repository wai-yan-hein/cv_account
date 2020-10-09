/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.RetInDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetInDao {

    public RetInDetailHis save(RetInDetailHis retInDetailHis);

    public List<RetInDetailHis> search(String glId, String vouNo);

    public void delete(String retInId, String glId);

}
