/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VCrDrVoucher;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface VCrDrVoucherDao {
    public List<VCrDrVoucher> search(String from, String to, String sourceAccId,
            String frmCurr, String dept, String vouNo, String compCode, String deptName,
            String splitId, String fromDesp, String naration, String remark, String toDesp);
}
