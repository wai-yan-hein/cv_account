/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.VouIdDao;
import com.cv.inv.entity.CompoundKey;
import com.cv.inv.entity.VouId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lenovo
 */
@Service
@Transactional
public class VouIdServiceImpl implements VouIdService {

    @Autowired
    private VouIdDao voudIdDao;

    @Override
    public VouId save(VouId vouId) {
        return voudIdDao.save(vouId);
    }

    @Override
    public Object getMax(String machineName, String vouType, String vouPeriod) throws Exception {
        return voudIdDao.getMax(machineName, vouType, vouPeriod);
    }

    @Override
    public Object find(CompoundKey key) {
        return voudIdDao.find(key);
        }

}
