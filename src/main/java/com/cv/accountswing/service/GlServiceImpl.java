/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.GlDao;
import com.cv.accountswing.entity.Gl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class GlServiceImpl implements GlService {

    @Autowired
    private GlDao dao;

    @Override
    public Gl save(Gl gl) throws Exception {
        dao.save(gl);
        return gl;
    }

    @Override
    public List<Gl> saveBatchGL(List<Gl> listGL) throws Exception {
        dao.saveBatchGL(listGL);
        return listGL;
    }

    @Override
    public Gl findById(Long glId) {
        Gl gl = dao.findById(glId);
        return gl;
    }

    @Override
    public List<Gl> search(String from, String to, String desp, String sourceAcId,
            String acId, String frmCurr, String toCurr, String reference, String dept,
            String vouNo, String cvId, String chequeNo, String compCode, String tranSource,
            String glVouNo, String splitId, String projectId) {
        List<Gl> listGL = dao.search(from, to, desp, sourceAcId, acId, frmCurr, toCurr,
                reference, dept, vouNo, cvId, chequeNo, compCode, tranSource, glVouNo,
                splitId, projectId);
        return listGL;
    }

    @Override
    public int delete(Long glId, String option) throws Exception {
        int cnt = dao.delete(glId, option);
        return cnt;
    }
}
