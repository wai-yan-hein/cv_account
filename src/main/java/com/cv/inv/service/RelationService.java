/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.UnitRelation;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RelationService {

    public UnitRelation save(UnitRelation relation);

    public UnitRelation findByKey(RelationKey key);

    public List<UnitRelation> search(String patternId);

    public List<UnitRelation> findAll();
}
