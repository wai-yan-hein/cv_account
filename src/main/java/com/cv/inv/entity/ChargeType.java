/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Entity
@Table(name = "charge_type")
public class ChargeType implements Serializable {

    private Integer chargeTypeId;
    private String chargeTypeDesp;
    private Date updatedDate;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "charge_type_id", unique = true, nullable = false)
    public Integer getChargeTypeId() {
        return chargeTypeId;
    }

    public void setChargeTypeId(Integer chargeTypeId) {
        this.chargeTypeId = chargeTypeId;
    }

    @Column(name = "charge_type_desc", nullable = false, length = 15, unique = true)
    public String getChargeTypeDesp() {
        return chargeTypeDesp;
    }

    public void setChargeTypeDesp(String chargeTypeDesp) {
        this.chargeTypeDesp = chargeTypeDesp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
