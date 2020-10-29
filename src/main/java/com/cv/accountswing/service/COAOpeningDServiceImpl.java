/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.COAOpeningDaoD;
import com.cv.accountswing.entity.AccOpeningD;
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VAccOpeningD;
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
public class COAOpeningDServiceImpl implements COAOpeningDService {

    @Autowired
    private COAOpeningDaoD dao;

    @Override
    public AccOpeningD save(AccOpeningD aod) {
        return dao.save(aod);
    }

    @Override
    public AccOpeningD findById(Long id) {
        AccOpeningD aod = dao.findById(id);
        return aod;
    }

    @Override
    public List<VAccOpeningD> search(String tranIdH) {
        List<VAccOpeningD> listAOD = dao.search(tranIdH);
        return listAOD;
    }

    @Override
    public int delete(String tranId) {
        int cnt = dao.delete(tranId);
        return cnt;
    }

    @Override
    public void insertFilter(String coaCode, int level, String opDate,
            String curr, String userId) throws Exception {
        dao.insertFilter(coaCode, level, opDate, curr, userId);
    }

    @Override
    public List<TmpOpeningClosing> getOpBalance(String coaCode, int level, String opDate,
            String curr, String userId) throws Exception {
        List<TmpOpeningClosing> listTOC = dao.getOpBalance(coaCode, level, opDate,
                curr, userId);
        return listTOC;
    }

    @Override
    public void deleteTmp(String userId) throws Exception {
        dao.deleteTmp(userId);
    }

    @Override
    public void insertFilterGL(String coaCode, String opDate, int level,
            String curr, String userId) throws Exception {
        dao.insertFilterGL(coaCode, opDate, level, curr, userId);
    }

    @Override
    public List<TmpOpeningClosing> getOpBalanceGL(String coaCode, String opDate,
            String clDae, int level, String curr, String userId, String dept) throws Exception {
        dao.genOpBalanceGL(coaCode, opDate, clDae, level, curr, userId, dept);
        List<TmpOpeningClosing> listTOC = dao.getOpBalanceGL(userId);
        return listTOC;
    }

    @Override
    public void genTriBalance(String compCode, String fromDate, String opDate, String tranDate,
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception {

        dao.genTriBalance(compCode, fromDate, opDate, tranDate, coaCode, currency, dept, cvId, userId);
    }

    @Override
    public void genArAp(String compCode, String fromDate, String opDate, String tranDate,
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception {
        dao.genArAp(compCode, fromDate, opDate, tranDate, coaCode, currency, dept, cvId, userId);
    }

    @Override
    public void genArAp1(String compCode, String fromDate, String opDate, String tranDate, String coaCode, String currency, String dept, String cvId, String userId) throws Exception {
        dao.genArAp1(compCode, fromDate, opDate, tranDate, coaCode, currency, dept, cvId, userId);
    }

    @Override
    public void genTriBalance1(String compCode, String fromDate, String opDate, String tranDate, String coaCode, String currency, String dept, String cvId, String userId) throws Exception {
        dao.genTriBalance1(compCode, fromDate, opDate, tranDate, coaCode, currency, dept, cvId, userId);
    }

    @Override
    public List<TmpOpeningClosing> getOpBalanceGL1(String coaCode, String opDate, String clDae, int level, String curr, String userId, String dept) throws Exception {
        dao.genOpBalanceGL1(coaCode, opDate, clDae, level, curr, userId, dept);
        List<TmpOpeningClosing> listTOC = dao.getOpBalanceGL(userId);
        return listTOC;
    }
}
