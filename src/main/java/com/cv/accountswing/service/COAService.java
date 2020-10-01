/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.COALevel;
import com.cv.accountswing.entity.ChartOfAccount;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface COAService {
    public ChartOfAccount save(ChartOfAccount coa);
    public ChartOfAccount save(ChartOfAccount coa, String opDate) throws Exception;
    public ChartOfAccount findById(String id);
    public List<ChartOfAccount> search(String code, String name, String compCode,
            String level, String parent, String userParent, String usrCoaCode);
    public int delete(String code, String compCode);
    public List<ChartOfAccount> getParent(String compCode);
    public List<COALevel> getParentChildCOA(String compCode);
    public List<ChartOfAccount> getCOALevel3Above(String compCode);
    public List<ChartOfAccount> getCOALevel2Above(String compCode);
    public List<ChartOfAccount> getAllChild(String parent, String compCode);
    public List<ChartOfAccount> getCompanyCOA(String compCode);
    public List<ChartOfAccount> getCompanyCOA(String compCode, String deptId, String projectId);
    public List<ChartOfAccount> getChild(String compCode, String parent);
}
