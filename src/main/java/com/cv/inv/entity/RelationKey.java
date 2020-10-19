/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Lenovo
 */
@Embeddable
public class RelationKey implements Serializable {

    @Column(name = "from_unit")
    private String fromUnit;
    @Column(name = "to_unit")
    private String toUnit;

    public RelationKey() {
    }

    public RelationKey(String fromUnit, String toUnit) {
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(String fromUnit) {
        this.fromUnit = fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }

    public void setToUnit(String toUnit) {
        this.toUnit = toUnit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.fromUnit);
        hash = 89 * hash + Objects.hashCode(this.toUnit);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RelationKey other = (RelationKey) obj;
        if (!Objects.equals(this.fromUnit, other.fromUnit)) {
            return false;
        }
        if (!Objects.equals(this.toUnit, other.toUnit)) {
            return false;
        }
        return true;
    }

}
