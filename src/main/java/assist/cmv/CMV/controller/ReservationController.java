package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.service.ReservationService;
import assist.cmv.CMV.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @PostMapping("/addReservation")
    public ResponseEntity addReservation(@RequestBody Reservation reservation) {
        Date localDate = new Date();
        if(reservation.getStartDate().before(localDate))
            return service.addReservation(null);
        if(reservation.getEndDate().before(reservation.getStartDate()))
            return service.addReservation(null);

        return service.addReservation(reservation);
    }

    @GetMapping("/reservations")
    public List<Reservation> getReservations() {
        return service.getReservations();
    }

    @GetMapping("/reservation/{id}")
    public Reservation getReservationById(@PathVariable int id) {
        return service.getReservation(id);
    }

    @PutMapping("/reservation/update")
    public Reservation updateReservation(@RequestBody Reservation reservation) {
        return service.updateReservation(reservation);
    }

    @DeleteMapping("/reservation/delete/{id}")
    public String deleteReservation(@PathVariable int id) {
        service.deleteReservation(id);
        return "Reservation with id <" + id + "> has been removed.";
    }
}
