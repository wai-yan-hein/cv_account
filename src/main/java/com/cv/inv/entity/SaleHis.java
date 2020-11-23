/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Trader;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Entity
@Table(name = "sale_his")
public class SaleHis implements java.io.Serializable {

    @Id
    @Column(name = "voucher_no", unique = true, nullable = false, length = 15)
    private String vouNo;
    @ManyToOne
    @JoinColumn(name = "cus_id")
    private Trader traderId;
    @ManyToOne
    @JoinColumn(name = "saleman_id")
    private SaleMan saleManId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sale_date")
    private Date saleDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "credit_term")
    private Date creditTerm;
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "from_cur_id"),
        @JoinColumn(name = "comp_code")
    })
    private Currency currency;
    @ManyToOne
    @JoinColumn(name = "vou_status_id")
    private VouStatus vouStatusId;
    @Column(name = "remark", length = 500)
    private String remark;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "disc_p")
    private Double discP;
    @Column(name = "tax_amt")
    private Double taxAmt;
    @Column(name = "tax_p")
    private Double taxP;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "grand_total")
    private Double grandTotal;
    @Column(name = "paid")
    private Double paid;
    @Column(name = "vou_balance")
    private Double vouBalance;
    @Column(name = "user_id")
    private String createdBy;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    public Trader getTraderId() {
        return traderId;
    }

    public void setTraderId(Trader traderId) {
        this.traderId = traderId;
    }

    public SaleMan getSaleManId() {
        return saleManId;
    }

    public void setSaleManId(SaleMan saleManId) {
        this.saleManId = saleManId;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public Date getCreditTerm() {
        return creditTerm;
    }

    public void setCreditTerm(Date creditTerm) {
        this.creditTerm = creditTerm;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public VouStatus getVouStatusId() {
        return vouStatusId;
    }

    public void setVouStatusId(VouStatus vouStatusId) {
        this.vouStatusId = vouStatusId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getDiscP() {
        return discP;
    }

    public void setDiscP(Double discP) {
        this.discP = discP;
    }

    public Double getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(Double taxAmt) {
        this.taxAmt = taxAmt;
    }

    public Double getTaxP() {
        return taxP;
    }

    public void setTaxP(Double taxP) {
        this.taxP = taxP;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
