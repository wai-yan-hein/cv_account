/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cv.accountswing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="region_type")
public class RegionType implements java.io.Serializable {
    private String typeId;
    private String regTypeNameEng;
    private String regTypeNameMya;

    @Id
    @Column(name="reg_type_id", unique=true, nullable=false, length=15)
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    
    @Column(name="reg_type_name_eng", length=255)
    public String getRegTypeNameEng() {
        return regTypeNameEng;
    }

    public void setRegTypeNameEng(String regTypeNameEng) {
        this.regTypeNameEng = regTypeNameEng;
    }

    @Column(name="reg_type_name_mya", length=255)
    public String getRegTypeNameMya() {
        return regTypeNameMya;
    }

    public void setRegTypeNameMya(String regTypeNameMya) {
        this.regTypeNameMya = regTypeNameMya;
    }
}
