package assist.cmv.CMV.repository;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsRoomByNfcTag(int nfcTag);
    boolean existsRoomById(int id);
    int countById(int id);

    @Query("SELECT r FROM Reservation r where r.id=?1")
    Reservation findByReservationId(int id);

    List<Room> findAllByReservationId(int id);

    int countByReservationId(int id);
}
