/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "auto_text")
public class AutoText implements Serializable {

    @Id
    @Column(name = "desp", unique = true, nullable = false)
    private String desp;
    @Column(name = "option", nullable = false)
    private String option;

    public AutoText(String desp, String option) {
        this.desp = desp;
        this.option = option;
    }

    public AutoText() {
    }
    
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

}
