/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_coa_op_details")
public class VAccOpeningD implements java.io.Serializable{
    private Long tranId;
    private Long tranIdH;
    private String coaCode;
    private String currCode;
    private Double exRate;
    private Double drAmt;
    private Double crAmt;
    private String coaName;
    private String coaNameM;
    private String coaParent;
    private String coaOption;
    private String compCode;
    private String curName;
    private String curSymbol;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tran_id", unique = true, nullable = false)
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Column(name="tran_id_h")
    public Long getTranIdH() {
        return tranIdH;
    }

    public void setTranIdH(Long tranIdH) {
        this.tranIdH = tranIdH;
    }

    @Column(name="coa_code", length=15)
    public String getCoaCode() {
        return coaCode;
    }

    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    @Column(name="curr_code", length=15)
    public String getCurrCode() {
        return currCode;
    }

    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }

    @Column(name="ex_rate")
    public Double getExRate() {
        return exRate;
    }

    public void setExRate(Double exRate) {
        this.exRate = exRate;
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

    @Column(name="coa_name_eng")
    public String getCoaName() {
        return coaName;
    }

    public void setCoaName(String coaName) {
        this.coaName = coaName;
    }

    @Column(name="coa_name_mya")
    public String getCoaNameM() {
        return coaNameM;
    }

    public void setCoaNameM(String coaNameM) {
        this.coaNameM = coaNameM;
    }

    @Column(name="coa_parent")
    public String getCoaParent() {
        return coaParent;
    }

    public void setCoaParent(String coaParent) {
        this.coaParent = coaParent;
    }

    @Column(name="coa_option")
    public String getCoaOption() {
        return coaOption;
    }

    public void setCoaOption(String coaOption) {
        this.coaOption = coaOption;
    }

    @Column(name="comp_code")
    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    @Column(name="cur_name")
    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    @Column(name="cur_symbol")
    public String getCurSymbol() {
        return curSymbol;
    }

    public void setCurSymbol(String curSymbol) {
        this.curSymbol = curSymbol;
    }
}
