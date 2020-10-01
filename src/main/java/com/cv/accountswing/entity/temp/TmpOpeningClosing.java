/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.temp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="tmp_op_cl")
public class TmpOpeningClosing implements java.io.Serializable{
    private TmpOpeningClosingKey key;
    private Double opening;
    private Double drAmt;
    private Double crAmt;
    private Double closing;

    @EmbeddedId
    public TmpOpeningClosingKey getKey() {
        return key;
    }

    public void setKey(TmpOpeningClosingKey key) {
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
}
