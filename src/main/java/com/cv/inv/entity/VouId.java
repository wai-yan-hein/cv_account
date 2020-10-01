/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Lenevo
 */
@Data
@Entity
@Table(name = "vou_id")
public class VouId implements java.io.Serializable {

    @Column(name = "vou_no")
    private Integer vouNo;
    @EmbeddedId
    private CompoundKey key;

    public VouId() {
    }

    public VouId(CompoundKey key, Integer vouNo) {
        this.key = key;
        this.vouNo = vouNo;
    }
}
