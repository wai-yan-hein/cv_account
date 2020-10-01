/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_tri_balance")
public class VTriBalance implements java.io.Serializable{
    private VTriBalanceKey key;
    private Double opening;
    private Double drAmt;
    private Double crAmt;
    private Double closing;
    private String coaName;
    private String usrCoaCode;

    @EmbeddedId
    public VTriBalanceKey getKey() {
        return key;
    }

    public void setKey(VTriBalanceKey key) {
        this.key = key;
    }

    @Column(name="opening")
    public Double getOpening() {
        return opening;
    }

    public void setOpening(Double opening) {
        this.opening = opening;
    }

    @Column(name="dr_amt")
    public Double getDrAmt() {
        return drAmt;
    }

    public void setDrAmt(Double drAmt) {
        this.drAmt = drAmt;
    }

    @Column(name="cr_amt")
    public Double getCrAmt() {
        return crAmt;
    }

    public void setCrAmt(Double crAmt) {
        this.crAmt = crAmt;
    }

    @Column(name="closing")
    public Double getClosing() {
        return closing;
    }

    public void setClosing(Double closing) {
        this.closing = closing;
    }

    @Column(name="coa_name_eng")
    public String getCoaName() {
        return coaName;
    }

    public void setCoaName(String coaName) {
        this.coaName = coaName;
    }

    @Column(name="coa_code_usr")
    public String getUsrCoaCode() {
        return usrCoaCode;
    }

    public void setUsrCoaCode(String usrCoaCode) {
        this.usrCoaCode = usrCoaCode;
    }
}
