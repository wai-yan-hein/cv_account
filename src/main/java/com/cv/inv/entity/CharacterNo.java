/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "char_seq")
public class CharacterNo implements java.io.Serializable {

    @Id
    @Column(name = "ch", unique = true, nullable = false, length = 2)
    private String ch;
    @Column(name = "char_no", nullable = false, length = 3, unique = true)
    private String strNumber;

    public CharacterNo() {

    }

    public CharacterNo(String ch, String strNumber) {
        this.ch = ch;
        this.strNumber = strNumber;
    }

}
