/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity.view;

import com.cv.inv.entity.RetOutCompoundKey;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 *
 * @author lenovo
 */
@Data
@Entity
@Table(name = "v_ret_out")
public class VRetOut implements Serializable {
    @EmbeddedId
    private RetOutCompoundKey key;
    @Column(name = "stock_id")
    private String stockId;
    @Column(name = "cv_id")
    private Long traderId;
    @Column(name = "trader_name")
    private String traderName;
    @Temporal(TemporalType.DATE)
    @Column(name = "gl_date")
    private Date glDate;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "paid")
    private Double vouPaid;
    @Column(name = "balance")
    private Double vouBalance;
    @Column(name = "location_id")
    private Integer locationId;
    @Column(name = "comp_id")
    private Integer compId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "user_short_name")
    private String  userName;
    @Column(name = "from_cur_id")
    private String  fromCurId;
    
}
