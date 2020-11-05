/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

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
 * @author Lenovo
 */
@Entity
@Table(name = "coa_opening")
public class COAOpening implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "coa_op_id", unique = true, nullable = false)
    private Integer opId;
    @Temporal(TemporalType.DATE)
    @Column(name = "op_date")
    private Date opDate;
    @Column(name = "source_acc_id")
    private String sourceAccId;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "cr_amt")
    private Double crAmt;
    @Column(name = "dr_amt")
    private Double drAmt;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "comp_id")
    private Integer compId;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "dept_code")
    private String depCode;
    @Column(name = "cv_id")
    private String cvId;

    public Integer getOpId() {
        return opId;
    }

    public void setOpId(Integer opId) {
        this.opId = opId;
    }

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
    }

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public Double getCrAmt() {
        return crAmt;
    }

    public void setCrAmt(Double crAmt) {
        this.crAmt = crAmt;
    }

    public Double getDrAmt() {
        return drAmt;
    }

    public void setDrAmt(Double drAmt) {
        this.drAmt = drAmt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getCvId() {
        return cvId;
    }

    public void setCvId(String cvId) {
        this.cvId = cvId;
    }

}
