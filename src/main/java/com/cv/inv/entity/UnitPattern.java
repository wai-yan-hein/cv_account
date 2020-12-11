/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "unit_pattern")
public class UnitPattern implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "pattern_id", unique = true, nullable = false)
    private Integer patternId;
    @Column(name = "pattern_name")
    private String patternName;

    public UnitPattern() {
    }
    
    public Integer getPatternId() {
        return patternId;
    }

    public void setPatternId(Integer patternId) {
        this.patternId = patternId;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    @Override
    public String toString() {
        return this.patternName;
    }

}