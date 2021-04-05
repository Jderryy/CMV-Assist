package assist.cmv.CMV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name ="room")
public class Room {

    //todo de modificat campul reservationIn in reservationsId
    // care va fi o lista ce va memora toate rezervarile facute
    // pentru o camera (o camera poate avea rezervari multiple
    // la perioade de timp diferite)

    @Id
    @GeneratedValue
    private int id;
    @ElementCollection
    private List<Integer> reservationsId;
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

    public void addReservationId(int id) {
        this.reservationsId.add(id);
    }

    public boolean getAvailability() {
        return !(reservationsId.size() > 0);
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
