/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.COAOpeningDao;
import com.cv.accountswing.entity.AccOpeningH;
import com.cv.accountswing.util.Util1;
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
public class COAOpeningServiceImpl implements COAOpeningService {

    @Autowired
    COAOpeningDao dao;

    @Override
    public AccOpeningH save(AccOpeningH aoh) {
        return dao.save(aoh);
    }

    @Override
    public AccOpeningH findById(Long Id) {
        AccOpeningH aoh = dao.findById(Id);
        return aoh;
    }

    @Override
    public List<AccOpeningH> search(String from, String to, String compCode,
            String currency, String remark) {
        List<AccOpeningH> listAOH = dao.search(from, to, compCode, currency,
                remark);
        return listAOH;
    }

    @Override
    public void GenerateZero(String tranIdH, String compCode, String currCode) throws Exception {
        dao.GenerateZero(tranIdH, compCode, currCode);
    }

    @Override
    public void deleteOpening(Long id) throws Exception {
        dao.deleteOpening(id);
    }

    @Override
    public void GenerateZeroGL(String opDate, String userId, String compCode,
            String currCode, String dept, String coaGroup) throws Exception {
        opDate = Util1.toDateStrMYSQL(opDate, "dd/MM/yyyy");
        dao.deleteOpeningGL(opDate, compCode, currCode, dept);
        dao.GenerateZeroGL(opDate, userId, compCode, currCode, dept, coaGroup);

    }

    @Override
    public void generateZeroOpening(String opDate, String userId, String compCode, String currCode, String dept, String coaGroup) throws Exception {
        opDate = Util1.toDateStrMYSQL(opDate, "dd/MM/yyyy");
        dao.deleteOpening(opDate, compCode, currCode, dept);
        dao.generateZeroOpening(opDate, userId, compCode, currCode, dept, coaGroup);
    }
}
