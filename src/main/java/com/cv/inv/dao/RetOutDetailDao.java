/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.RetInHisDetail;
import com.cv.inv.entity.RetOutHisDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RetOutDetailDao {

    public RetOutHisDetail save(RetOutHisDetail pd);

    public List<RetOutHisDetail> search(String glId);

    public int delete(String id);

}
