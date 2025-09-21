package it.unibs.ingsw.destinazioni.domain.model;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class LocationAddress {

    private String street;
    private String streetNumber;
    private String town;
    private String province;

    public LocationAddress(String street, String streetNumber, String town, String province) {
        this.street = street;
        this.streetNumber = streetNumber;
        this.town = town;
        this.province = province;
    }
}
