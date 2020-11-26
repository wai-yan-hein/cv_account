/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.dao.CustomerDao;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
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
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerDao dao;
    @Autowired
    SeqTableService seqService;
    @Autowired
    SystemPropertyService spService;

    @Override
    public Customer save(Customer cus) {
        if (cus.getTraderId() == null || cus.getTraderId().isEmpty()) {
            String tmpTraderId = getTraderId("CUS", "-", Global.compId.toString());
            cus.setTraderId(tmpTraderId);
            cus.setCreatedBy(Global.loginUser.getUserId().toString());
        } else {
            cus.setUpdatedBy(Global.loginUser.getUserId().toString());

        }

        return dao.save(cus);
    }

    @Override
    public Customer findById(Integer id) {
        Customer cus = dao.findById(id);
        return cus;
    }

    @Override
    public List<Customer> search(String code, String name, String address,
            String phone, String compCode) {
        List<Customer> listCus = dao.search(code, name, address, phone, compCode);
        return listCus;
    }

    @Override
    public int delete(Integer id) {
        int cnt = dao.delete(id);
        return cnt;
    }

    private String getTraderId(String option, String period, String compCode) {
        int ttlLength = 5;
        String get = Global.sysProperties.get("system.trader.id.length");
        if (get != null) {
            ttlLength = Integer.parseInt(get);
        }
        int seqNo = seqService.getSequence(option, period, compCode);
        String tmpTraderId = option.toUpperCase() + String.format("%0" + ttlLength + "d", seqNo);
        return tmpTraderId;
    }

    private boolean isAutoGenerate(int compCode) {
        boolean status = false;
        SystemPropertyKey spk = new SystemPropertyKey("system.trader.id.auto.generate",
                compCode);
        SystemProperty sp = spService.findById(spk);

        if (sp != null) {
            if (sp.getPropValue() != null) {
                if (sp.getPropValue().equals("Y")) {
                    status = true;
                }
            }
        }

        return status;
    }
}
