/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.CurrencyDao;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
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
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    CurrencyDao dao;

    @Override
    public Currency save(Currency cur) {
        cur = dao.save(cur);
        return cur;
    }

    @Override
    public Currency findById(CurrencyKey id) {
        Currency cur = dao.findById(id);
        return cur;
    }

    @Override
    public List<Currency> search(String code, String name, String compCode) {
        List<Currency> listCur = dao.search(code, name, compCode);
        return listCur;
    }

    @Override
    public int delete(String code, String compId) {
        int cnt = dao.delete(code, compId);
        return cnt;
    }

    @Override
    public Currency findById(String id) {
        return dao.findById(id);
    }
}
