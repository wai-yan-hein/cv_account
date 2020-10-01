/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.AssignDept;
import com.cv.accountswing.entity.AssignDeptKey;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface AssignDeptDao {
    public AssignDept save(AssignDept ad);
    public AssignDept findById(AssignDeptKey key);
    public List search(String compId, String roleId);
    public int delete(String compId, String roleId, String deptCode);
    public void updateNew(String compId, String roleId) throws Exception;
}
