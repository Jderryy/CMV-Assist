package assist.cmv.CMV.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="reservation")
public class Reservation {

    @Id
    @GeneratedValue
    private int id;
    private Date startDate;
    private Date endDate;
    private int userId;
    private String status;
    private boolean annulled;
    private long price;
    private int roomNumber;

    public boolean getAnnulled() {
        return annulled;
    }

}
