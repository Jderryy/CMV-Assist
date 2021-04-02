package assist.cmv.CMV.service;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.User;
import assist.cmv.CMV.repository.ReservationRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository repository;

    public ResponseEntity addReservation(Reservation reservation) {
        Optional<Reservation> optUser= Optional.ofNullable(reservation);
        if(optUser.isPresent()) {
            repository.save(reservation);
            return new ResponseEntity( HttpStatus.OK);
        }
        return new ResponseEntity("Invalid fields!", HttpStatus.BAD_REQUEST);
    }


    public Reservation getReservation(int id) {
        return repository.findById(id).orElse(null);
    }

    public List<Reservation> getReservations() {
        return repository.findAll();
    }

    public Reservation updateReservation(Reservation reservation) {
        Reservation existingReservation = reservation;
        existingReservation.setAnnulled(reservation.getCanceled());
        existingReservation.setEndDate(reservation.getEndDate());
        existingReservation.setId(reservation.getId());
        existingReservation.setPrice(reservation.getPrice());
        existingReservation.setStatus(reservation.getStatus());
        existingReservation.setStartDate(reservation.getStartDate());
        existingReservation.setUserId(reservation.getUserId());
        return repository.save(existingReservation);
    }

    public String deleteReservation(int id)
    {
        repository.deleteById(id);
        return "Reservation with id <" + id + "> has been removed.";
    }

}
