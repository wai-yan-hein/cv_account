/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VTriBalance;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface VTriBalanceDao {
    public List<VTriBalance> getTriBalance(String userId, String compCode);
}
