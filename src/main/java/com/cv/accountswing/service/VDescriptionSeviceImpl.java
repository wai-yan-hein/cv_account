/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.VDescriptionDao;
import com.cv.accountswing.entity.view.VDescription;
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
public class VDescriptionSeviceImpl implements VDescriptionService {

    @Autowired
    private VDescriptionDao dao;

    @Override
    public List<VDescription> getDescriptions() {
        return dao.getDescriptions();
    }

}
