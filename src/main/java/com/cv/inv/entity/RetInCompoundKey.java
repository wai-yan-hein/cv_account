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
public class RetInCompoundKey implements Serializable {

    @Column(name = "ret_in_detail_id", unique = true, nullable = false)
    private String retInDetailId;
    @Column(name = "gl_id", unique = true, nullable = false)
    private Long glId;
    @Column(name = "voucher_no")
    private String vouNo;

    public RetInCompoundKey() {
    }

    public RetInCompoundKey(String retInDetailId, Long glId, String vouNo) {
        this.retInDetailId = retInDetailId;
        this.glId = glId;
        this.vouNo = vouNo;
    }
    

}
