/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.TraderOpeningD;
import com.cv.accountswing.entity.view.VTmpTraderBalance;
import com.cv.accountswing.entity.view.VTraderOpeningDetail;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface TraderOpeningDService {
    public TraderOpeningD save(TraderOpeningD tod);
    public TraderOpeningD findById(Long id);
    public List<VTraderOpeningDetail> search(String tranIdH);
    public int delete(String tranId);
    public List<VTmpTraderBalance> getTraderBalance(String compCode, String traderId, String opDate, 
            String curr, String userId) throws Exception;
}
