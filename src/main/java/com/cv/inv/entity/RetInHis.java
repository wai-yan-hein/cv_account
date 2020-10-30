/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.PaymentType;
import com.cv.accountswing.entity.Trader;
import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "ret_in_his")
public class RetInHis implements java.io.Serializable {

    private String retInId;
    private Date retInDate;
    private Trader customer;
    private PaymentType paymentType;
    private Location location;
    private String remark;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private Boolean deleted;
    private Integer session;
    private Double vouTotal;
    private Double paid;
    private Double balance;
    private Currency currency;
    //For parent currency
    private Double exRateP;
    //=========================
    //For school
    private String regNo;
    private String stuNo;
    private String stuName;

    private String migId;
    private String admissionNo;
    private String ptType; //Patient Type

    @Column(name = "balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Column(name = "created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
    @JoinColumn(name = "cus_id")
    public Trader getCustomer() {
        return customer;
    }

    public void setCustomer(Trader customer) {
        this.customer = customer;
    }

    @Column(name = "deleted")
    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @ManyToOne
    @JoinColumn(name = "location")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @ManyToOne
    @JoinColumn(name = "payment_type")
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Column(name = "remark", length = 25)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ret_in_date")
    public Date getRetInDate() {
        return retInDate;
    }

    public void setRetInDate(Date retInDate) {
        this.retInDate = retInDate;
    }

    @Id
    @Column(name = "ret_in_id", unique = true, nullable = false, length = 15)
    public String getRetInId() {
        return retInId;
    }

    public void setRetInId(String retInId) {
        this.retInId = retInId;
    }

    @Column(name = "session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @Column(name = "updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name = "vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "cur_code"),
        @JoinColumn(name = "comp_code")
    })
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name = "exchange_rate_p")
    public Double getExRateP() {
        return exRateP;
    }

    public void setExRateP(Double exRateP) {
        this.exRateP = exRateP;
    }

    @Column(name = "stu_reg_no", length = 15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name = "stu_no", length = 25)
    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    @Column(name = "stu_name", length = 255)
    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    @Column(name = "mig_id", length = 25)
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    @Column(name = "admission_no", length = 15)
    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    @Column(name = "patient_type", length = 15)
    public String getPtType() {
        return ptType;
    }

    public void setPtType(String ptType) {
        this.ptType = ptType;
    }
}
