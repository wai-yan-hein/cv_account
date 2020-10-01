/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.temp.TmpProfitAndLost;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface TmpProfitAndLostDao {
    public TmpProfitAndLost save(TmpProfitAndLost pal) throws Exception;
    public TmpProfitAndLost findById(Long id);
    public List<TmpProfitAndLost> search(String userId, String compId);
    public int delete(String id, String userId, String compId) throws Exception;
}
