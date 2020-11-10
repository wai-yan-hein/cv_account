/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import java.util.Date;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * Trader class is parent class of Customer, Patient and Supplier class. Sharing
 * "trader" table with Patient, Customer and Supplier class. Database table name
 * is trader.
 */
@Entity
@Table(name = "trader")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "T")
public class Trader implements java.io.Serializable {

    private Integer id;
    private String traderId;
    private Integer compCode;
    private String traderName;
    private String address;
    private Region region;
    private TraderType traderType;
    private String phone;
    private String email;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private ChartOfAccount account;
    private boolean active;
    private String remark;
    private String parent;
    private String appShortName; //use integration with other application
    private String appTraderCode; //Original trader id from integration app

    public Trader(Integer id, String traderName) {
        this.id = id;
        this.traderName = traderName;
    }

    public Trader() {
    }
    
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "trader_id", unique = true, nullable = false, length = 25)
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Column(name = "comp_code", length = 15, nullable = false)
    public Integer getCompCode() {
        return compCode;
    }

    public void setCompCode(Integer compCode) {
        this.compCode = compCode;
    }

    @Column(name = "address", nullable = true, length = 500)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "created_id", nullable = true, length = 15)
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

    @Column(name = "email", nullable = true, length = 25)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone", nullable = true, length = 500)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "trader_name", nullable = false, length = 255)
    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    @Column(name = "updated_id", nullable = true, length = 15)
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "updated_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @ManyToOne
    @JoinColumn(name = "account_code")
    public ChartOfAccount getAccount() {
        return account;
    }

    public void setAccount(ChartOfAccount account) {
        this.account = account;
    }

    @Column(name = "active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "remark", length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "parent", nullable = true, length = 15)
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Column(name = "app_short_name", length = 10)
    public String getAppShortName() {
        return appShortName;
    }

    public void setAppShortName(String appShortName) {
        this.appShortName = appShortName;
    }

    @Column(name = "app_trader_code", length = 25)
    public String getAppTraderCode() {
        return appTraderCode;
    }

    public void setAppTraderCode(String appTraderCode) {
        this.appTraderCode = appTraderCode;
    }

    @Transient
    public String getDecriminatorValue() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }

    @ManyToOne
    @JoinColumn(name = "reg_id")
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @ManyToOne
    @JoinColumn(name = "trader_type_id")
    public TraderType getTraderType() {
        return traderType;
    }

    public void setTraderType(TraderType traderType) {
        this.traderType = traderType;
    }

    @Override
    public String toString() {
        return traderName;
    }

}
