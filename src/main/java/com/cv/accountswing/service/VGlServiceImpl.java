/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.VGlDao;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author WSwe
 */
@Service
@Transactional
public class VGlServiceImpl implements VGlService {

    @Autowired
    private VGlDao dao;

    @Override
    public List<VGl> find(String glVouNo) {
        List<VGl> listVGL = dao.find(glVouNo);
        return listVGL;
    }

    @Override
    public List<VGl> search(String from, String to, String desp, String sourceAcId,
            String acId, String frmCurr, String toCurr, String reference, String dept,
            String vouNo, String cvId, String chequeNo, String compCode, String tranSource,
            String glVouNo, String deptName, String traderName, String splitId,
            String projectId, String debAmt, String crdAmt) {
        List<VGl> listVGL = dao.search(from, to, desp, sourceAcId, acId, frmCurr,
                toCurr, reference, dept, vouNo, cvId, chequeNo, compCode, tranSource,
                glVouNo, deptName, traderName, splitId, projectId, debAmt, crdAmt);
        return listVGL;
    }

    @Override
    public List<VGl> searchGlDrCr(String from, String to, String sourceAcId,
            String frmCurr, String dept, String cvId, String compCode, String option) {
        return dao.searchGlDrCr(from, to, sourceAcId, frmCurr, dept, cvId, compCode, option);
    }

    @Override
    public List<VGl> getCrDrVoucher(String vouNo, String compCode) {
        return dao.getCrDrVoucher(vouNo, compCode);
    }

    @Override
    public VGl findById(Long id) {
        return dao.findById(id);
    }

}
