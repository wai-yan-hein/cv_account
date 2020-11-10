/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.view.VGl;
import java.util.List;

/**
 *
 * @author WSwe
 */
public interface VGlService {

    public List<VGl> find(String glVouNo);

    public VGl findById(Long id);

    public List<VGl> search(String from, String to, String desp, String sourceAcId,
            String acId, String frmCurr, String toCurr, String reference, String dept,
            String vouNo, String cvId, String chequeNo, String compCode, String tranSource,
            String glVouNo, String deptName, String traderName, String splitId,
            String projectId, String debAmt, String crdAmt);

    public List<VGl> searchGlDrCr(String from, String to, String sourceAcId,
            String frmCurr, String dept, String cvId, String compCode, String option);

    public List<VGl> getCrDrVoucher(String vouNo, String compCode);
}
