/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VRef;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class VRefDaoImpl extends AbstractDao<String, VRef> implements VRefDao {

    @Override
    public List<VRef> getRefrences() {
        String hsql = "select o from VRef o";
        return findHSQL(hsql);
    }

}
