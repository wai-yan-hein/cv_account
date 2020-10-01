/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;
import static javax.persistence.GenerationType.IDENTITY;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
@Entity
@Table(name = "machine_info")
public class MachineInfo implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "machine_id", unique = true, nullable = false)
    private Integer machineId;
    @Column(name = "machine_name", unique = true, nullable = false)
    private String machineName;
    @Column(name = "machine_ip")
    private String ipAddress;
    @Column(name = "created_date", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    public MachineInfo() {
    }
}
