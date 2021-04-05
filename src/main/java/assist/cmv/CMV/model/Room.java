package assist.cmv.CMV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name ="room")
public class Room {

    //todo de scos reservainId

    @Id
    private int id;
    private int reservationId;
    private int maxCapacity;
    private String facilities;
    private boolean smoking;
    private boolean petFriendly;
    private double rating;
    private String review;
    private int nfcTag;
    private int bedsNumber;
    private boolean cleaned;
    private int price;
    private boolean availability;




    public boolean getAvailability() {
        return availability;
    }

    public boolean getCleaned() {
        return cleaned;
    }

    public boolean getPetFriendly() {
        return petFriendly;
    }

    public boolean getSmoking() {
        return smoking;
    }

}
