/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.TraderOpeningH;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface TraderOpeningService {
    public TraderOpeningH save(TraderOpeningH toh);
    public TraderOpeningH findById(Long id);
    public List<TraderOpeningH> search(String from, String to, String compCode,
            String remark);
    public void generateZero(String tranIdH, String compCode, String currCode)throws Exception;
}
