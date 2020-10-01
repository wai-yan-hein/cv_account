/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VApar;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface VAParDao {
    public List<VApar> getApAr(String userId, String compCode);
}
