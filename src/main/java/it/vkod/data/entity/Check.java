package it.vkod.data.entity;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("checks")
public class Check implements Serializable, Cloneable {

    @Id
    Long id;

    Integer pincode;

    Date checkedOn;

    Time checkedInAt;

    Time checkedOutAt;

    String currentSession;

    Boolean isActive;

    String qrcode;

    Float lat;

    Float lon;

    Boolean validLocation;

    public Check setLat(Float lat) {
        this.lat = lat;
        return this;
    }

    public Check setLat(Double lat) {
        this.lat = lat.floatValue();
        return this;
    }

    public Check setLat(String lat) {
        this.lat = Float.parseFloat(lat);
        return this;
    }

    public Check setLon(Float lon) {
        this.lon = lon;
        return this;
    }

    public Check setLon(Double lon) {
        this.lon = lon.floatValue();
        return this;
    }

    public Check setLon(String lon) {
        this.lon = Float.parseFloat(lon);
        return this;
    }

    public boolean isNew() {
        return this.id == null;
    }


    @Override
    public Check clone() {

        try {
            return ((Check) super.clone())
                    .setPincode(this.getPincode())
                    .setCheckedOutAt(this.getCheckedOutAt())
                    .setCheckedInAt(this.getCheckedInAt())
                    .setCurrentSession(this.getCurrentSession())
                    .setQrcode(this.getQrcode())
                    .setId(this.getId())
                    .setCheckedOn(this.getCheckedOn())
                    .setIsActive(this.getIsActive())
                    .setLat(this.getLat())
                    .setLon(this.getLon());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return this.checkedOn + " - " + this.qrcode;
    }
}
