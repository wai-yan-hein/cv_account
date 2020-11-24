/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.VRefDao;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.entity.view.VRef;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class VRefServiceImpl implements VRefService {

    @Autowired
    private VRefDao dao;

    @Override
    public List<VRef> getRefrences() {
        return dao.getRefrences();
    }

}
