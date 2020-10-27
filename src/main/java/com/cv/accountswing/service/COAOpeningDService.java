/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.AccOpeningD;
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VAccOpeningD;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface COAOpeningDService {
    public AccOpeningD save(AccOpeningD aod);
    public AccOpeningD findById(Long id);
    public List<VAccOpeningD> search(String tranIdH);
    public int delete(String tranId);
    public void insertFilter(String coaCode, int level, String opDate, 
            String curr, String userId) throws Exception;
    public List<TmpOpeningClosing> getOpBalance(String coaCode, int level, String opDate, 
            String curr, String userId) throws Exception;
    public void deleteTmp(String userId) throws Exception;
    
    public void insertFilterGL(String coaCode, String opDate, int level,
            String curr, String userId) throws Exception;
    public List<TmpOpeningClosing> getOpBalanceGL(String coaCode, String opDate, 
            String clDae, int level, String curr, String userId, String dept) throws Exception;
    public void genTriBalance(String compCode, String fromDate, String opDate, String tranDate, 
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception;
    public void genTriBalance1(String compCode, String fromDate, String opDate, String tranDate, 
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception;
    public void genArAp(String compCode, String fromDate, String opDate, String tranDate, 
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception;
    public void genArAp1(String compCode, String fromDate, String opDate, String tranDate, 
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception;
}
