/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.COAOpening;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class OpeningDaoImpl extends AbstractDao<Integer, COAOpening> implements OpeningDao {

    @Override
    public COAOpening save(COAOpening coa) {
        persist(coa);
        return coa;
    }

}
