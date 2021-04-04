package assist.cmv.CMV.service;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.repository.ReservationRepository;
import assist.cmv.CMV.repository.RoomRepository;
import assist.cmv.CMV.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;
    Date localDate = new Date();

    public void setData(Reservation reservation) {
        reservation.setAnnulled(false);
        reservation.setStatus("new");
    }

    public String isValid(Reservation reservation) {
        long diff = reservation.getEndDate().getTime() - reservation.getStartDate().getTime();
        long isToday =  TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if(isToday>1){
            if (reservation.getStartDate().before(localDate))
                return "Start date is in the past!";
        }
        if (reservation.getEndDate().before(reservation.getStartDate()) || reservation.getStartDate().equals(reservation.getEndDate()))
            return "Start date is bigger than end date!";
        if (!roomRepository.existsById(reservation.getRoomNumber())) {
            return "Room with id <" + reservation.getRoomNumber() + "> doesn't exists.";
        } else if (roomRepository.getOne(reservation.getRoomNumber()).getReservationId() >= 1)
            return "Room with id <" + reservation.getRoomNumber() + "> is already reserved.";
        if (!userRepository.existsById(reservation.getUserId()))
            return "There is no registred user with id <" + reservation.getUserId() + ">.";
        int roomprice = roomRepository.getOne(reservation.getRoomNumber()).getPrice();
        if (isToday > 30)
            return "Can't reserve a room for a period longer than 30 days!";
        reservation.setPrice(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) * roomprice);
        return null;
    }

    public ResponseEntity addReservation(Reservation reservation) {
        String responseError = isValid(reservation);
        if (responseError == null) {
            setData(reservation);
            reservationRepository.save(reservation);
            Room existingRoom = roomRepository.findById(reservation.getRoomNumber()).orElse(null);
            existingRoom.setReservationId(reservation.getId());
            roomRepository.save(existingRoom);

            return new ResponseEntity<>("Reservation with id <" + reservation.getId() + "> has been added.", HttpStatus.OK);
        }
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity getReservation(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation == null)
            return new ResponseEntity<>("Reservation with id <" + id + "> doesn't exists.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(existingReservation, HttpStatus.OK);

    }

    public ResponseEntity getReservations() {
        List<Reservation> lst = reservationRepository.findAll();
        if (lst.size() != 0)
            return new ResponseEntity<>(reservationRepository.findAll(), HttpStatus.OK);
        else
            return new ResponseEntity<>(reservationRepository.findAll(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getReservationsByUserId(int id) {
        if (!userRepository.existsById(id))
            return new ResponseEntity<>("User with id <" + id + "> was not found.", HttpStatus.BAD_REQUEST);
        List<Reservation> lst = reservationRepository.findByUserId(id);
        if (lst.size() != 0)
            return new ResponseEntity<>(reservationRepository.findByUserId(id), HttpStatus.OK);
        else
            return new ResponseEntity<>("There are no reservations available for user with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity updateReservation(Reservation reservation, int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation == null) {
            return new ResponseEntity<>("Reservation with id <" + id + "> doesn't exists.", HttpStatus.BAD_REQUEST);
        }
        String responseError = isValid(reservation);
        if (responseError == null) {

            Room existingRoom = roomRepository.findById(existingReservation.getRoomNumber()).orElse(null);
            existingRoom.setReservationId(0);
            roomRepository.save(existingRoom);
            existingReservation.setAnnulled(reservation.getAnnulled());
            existingReservation.setEndDate(reservation.getEndDate());
            existingReservation.setStatus(reservation.getStatus());
            existingReservation.setStartDate(reservation.getStartDate());
            existingReservation.setUserId(reservation.getUserId());
            existingReservation.setRoomNumber(reservation.getRoomNumber());
            existingReservation.setPrice(reservation.getPrice());
            existingRoom = roomRepository.findById(reservation.getRoomNumber()).orElse(null);
            existingRoom.setReservationId(id);
            roomRepository.save(existingRoom);

            reservationRepository.save(existingReservation);
            return new ResponseEntity<>(existingReservation, HttpStatus.OK);

        }
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity cancelReservation(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation == null) {
            return new ResponseEntity<>("Reservation with id <" + id + "> does not exist.", HttpStatus.BAD_REQUEST);
        }
        if (existingReservation.getStartDate().before(localDate)) {
            return new ResponseEntity<>("Reservation with id <" + id + "> has already started (can't be canceled).", HttpStatus.BAD_REQUEST);
        }
        existingReservation.setAnnulled(true);
        Room existingRoom = roomRepository.findById(existingReservation.getRoomNumber()).orElse(null);
        existingRoom.setReservationId(0);
        roomRepository.save(existingRoom);
        reservationRepository.save(existingReservation);
        return new ResponseEntity<>("Reservation with id <" + id + " succesfully canceled.", HttpStatus.OK);
    }


    public ResponseEntity performCheckIn(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if(existingReservation!=null){
            if(existingReservation.getStartDate().before(localDate))
                return new ResponseEntity("Can not perform check-in until "+existingReservation.getStartDate(),HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity("Can not perform check-in until "+existingReservation.getStartDate(),HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity performCheckOut(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        return new ResponseEntity("Can not perform check-in until "+existingReservation.getStartDate(),HttpStatus.BAD_REQUEST);

    }
}
