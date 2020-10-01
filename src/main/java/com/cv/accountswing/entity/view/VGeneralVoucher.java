/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_general_voucher")
public class VGeneralVoucher implements java.io.Serializable{
    private String gvVouNo;
    private Date glDate;
    private String vouNo;
    private String reference;
    private Integer compId;
    
    @Id
    @Column(name = "gl_vou_no", unique = true, nullable = false)
    public String getGvVouNo() {
        return gvVouNo;
    }

    public void setGvVouNo(String gvVouNo) {
        this.gvVouNo = gvVouNo;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "gl_date")
    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    @Column(name="voucher_no")
    public String getVouNo() {
        return vouNo;
    }
    
    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="reference")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Column(name="comp_id")
    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
    }
}
