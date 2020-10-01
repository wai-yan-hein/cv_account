/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author lenovo
 */
@Data
@Embeddable
public class RetOutCompoundKey implements Serializable {

    @Column(name = "ret_out_detail_id", unique = true, nullable = false)
    private String retOutDetailId;
    @Column(name = "gl_id", unique = true, nullable = false)
    private Long glId;
    @Column(name = "vou_no")
    private String vouNo;

    public RetOutCompoundKey() {
    }

    public RetOutCompoundKey(String retOutDetailId, Long glId, String vouNo) {
        this.retOutDetailId = retOutDetailId;
        this.glId = glId;
        this.vouNo = vouNo;
    }

}
