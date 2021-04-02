package assist.cmv.CMV.repository;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r where r.id=?1")
    Reservation findReservationBy(int id);

    Reservation findReservationByRoomNumber(int id);
}
