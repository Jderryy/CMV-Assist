package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.service.ReservationService;
import assist.cmv.CMV.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @PostMapping("/addReservation")
    public ResponseEntity addReservation(@RequestBody Reservation reservation) {
        return service.addReservation(reservation);
    }

    //for guest
    @GetMapping("/reservations/{id}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable int id){
        return service.getReservationsByUserId(id);
    }

    //for admin
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        return service.getReservations();
    }

    //for guest
    @PutMapping("/reservation/update/{id}")
    public ResponseEntity updateReservation(@RequestBody Reservation reservation, @PathVariable int id) {
        return service.updateReservation(reservation, id);
    }

    @PutMapping("/reservation/cancel/{id}")
    public ResponseEntity cancelReservation(@PathVariable int id){
        return service.cancelReservation(id);

    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity getReservationById(@PathVariable int id) {
        return service.getReservation(id);
    }


}
