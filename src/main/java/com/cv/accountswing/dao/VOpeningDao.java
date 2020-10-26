/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VCOAOpening;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface VOpeningDao {

    public List<VCOAOpening> search(String opDate, String sourceAccId, String userId, String compId, String depId,String curCode);

}
