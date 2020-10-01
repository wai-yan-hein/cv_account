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
@Table(name = "v_cr_dr_voucher")
public class VCrDrVoucher implements java.io.Serializable {

    private String voucherNo;
    private int compCode;
    private int splitId;
    private Date glDate;
    private String deptId;
    private String deptName;
    private String deptUsrCode;
    private String sourceAccId;
    private String sourceAccName;
    private String sourceAccCode;
    private String currency;
    private String remark;
    private String fromDesp;
    private String toDesp;
    private String naration;
    private Double ttlDr;
    private Double ttlCr;

    @Id @Column(name="voucher_no")
    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    @Column(name="comp_code")
    public int getCompCode() {
        return compCode;
    }

    public void setCompCode(int compCode) {
        this.compCode = compCode;
    }

    @Column(name="split_id")
    public int getSplitId() {
        return splitId;
    }

    public void setSplitId(int splitId) {
        this.splitId = splitId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "gl_date")
    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    @Column(name="dept_id")
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Column(name="dept_name")
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Column(name="dept_usr_code")
    public String getDeptUsrCode() {
        return deptUsrCode;
    }

    public void setDeptUsrCode(String deptUsrCode) {
        this.deptUsrCode = deptUsrCode;
    }

    @Column(name="source_ac_id")
    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
    }

    @Column(name="source_acc_name")
    public String getSourceAccName() {
        return sourceAccName;
    }

    public void setSourceAccName(String sourceAccName) {
        this.sourceAccName = sourceAccName;
    }

    @Column(name="source_acc_code")
    public String getSourceAccCode() {
        return sourceAccCode;
    }

    public void setSourceAccCode(String sourceAccCode) {
        this.sourceAccCode = sourceAccCode;
    }

    @Column(name="from_cur_id")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="from_desp")
    public String getFromDesp() {
        return fromDesp;
    }

    public void setFromDesp(String fromDesp) {
        this.fromDesp = fromDesp;
    }

    @Column(name="to_desp")
    public String getToDesp() {
        return toDesp;
    }

    public void setToDesp(String toDesp) {
        this.toDesp = toDesp;
    }

    @Column(name="naration")
    public String getNaration() {
        return naration;
    }

    public void setNaration(String naration) {
        this.naration = naration;
    }

    @Column(name="ttl_dr")
    public Double getTtlDr() {
        return ttlDr;
    }

    public void setTtlDr(Double ttlDr) {
        this.ttlDr = ttlDr;
    }

    @Column(name="ttl_cr")
    public Double getTtlCr() {
        return ttlCr;
    }

    public void setTtlCr(Double ttlCr) {
        this.ttlCr = ttlCr;
    }
}
