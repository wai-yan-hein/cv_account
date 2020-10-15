/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Lenevo
 */
@Embeddable
public class CompoundKey implements Serializable {

    @Column(name = "machine_name", nullable = false, length = 50)
    private String machineName;
    @Column(name = "vou_type", nullable = false, length = 15)
    private String vouType;
    @Column(name = "vou_period", nullable = false, length = 15)
    private String period;

    public CompoundKey() {
    }

    public CompoundKey(String machineName, String vouType, String period) {
        this.machineName = machineName;
        this.vouType = vouType;
        this.period = period;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
    

}
