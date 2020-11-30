/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.TraderDao;
import com.cv.accountswing.entity.Trader;
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
public class TraderServiceImpl implements TraderService {

    @Autowired
    TraderDao dao;
    @Autowired
    SeqTableService seqService;
    @Autowired
    SystemPropertyService spService;

    @Override
    public Trader findById(Integer id) {
        return dao.findById(id);
    }

    @Override
    public List<Trader> searchTrader(String code, String name, String address,
            String phone, String parentCode, String compCode, String appTraderCode) {
        List<Trader> listTR = dao.searchTrader(code, name, address, phone,
                parentCode, compCode, appTraderCode);
        return listTR;
    }

    @Override
    public Trader saveTrader(Trader trader) {

        return dao.saveTrader(trader);
    }
}
