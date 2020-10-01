/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "location")
public class Location implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "location_id", unique = true, nullable = false)
    private Integer locationId;
    @Column(name = "location_name", nullable = false, length = 50, unique = true)
    private String locationName;
    @Column(name = "parent")
    private Integer parent;
    @Column(name = "calc_stock")
    private boolean calcStock;

    public Location() {
    }

    public Location(int locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return locationName;
    }

}
