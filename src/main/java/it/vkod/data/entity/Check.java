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

}
