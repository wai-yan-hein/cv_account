/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Entity
@Table(name = "vou_status")
public class VouStatus implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "vou_status_id", unique = true, nullable = false)
    private Integer vouStatusId;
    @Column(name = "status_desp", nullable = false, length = 15, unique = true)
    private String statusDesp;

    public Integer getVouStatusId() {
        return vouStatusId;
    }

    public void setVouStatusId(Integer vouStatusId) {
        this.vouStatusId = vouStatusId;
    }

    public String getStatusDesp() {
        return statusDesp;
    }

    public void setStatusDesp(String statusDesp) {
        this.statusDesp = statusDesp;
    }

}
