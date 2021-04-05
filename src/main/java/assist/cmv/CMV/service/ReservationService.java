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


import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;
    private final LocalDate localDate =  LocalDate.now();

    private void setData(Reservation reservation) {
        reservation.setAnnulled(false);
        reservation.setStatus("new");
    }

    private String isValid(Reservation reservation) {
        List<Reservation> reservations= reservationRepository.findAll();
        Period period= Period.between(reservation.getStartDate(),reservation.getEndDate());
        System.out.println(period.getDays());
        StringBuilder messageResponse= new StringBuilder();
        if (reservation.getStartDate().isBefore(localDate))
                messageResponse.append("Start date is in the past!\n");
        if (reservation.getEndDate().isBefore(reservation.getStartDate()) || reservation.getStartDate().equals(reservation.getEndDate()))
            messageResponse.append("Start date is bigger than end date!\n");
        System.out.println(roomRepository.existsById(reservation.getRoomNumber()));
        if (!roomRepository.existsById(reservation.getRoomNumber())) {
            messageResponse.append("Room with id <").append(reservation.getRoomNumber()).append("> doesn't exist.\n");
        }
        else {
            for (Reservation r : reservations){
                if(r.getRoomNumber()==reservation.getRoomNumber()){
                    LocalDate start=r.getStartDate();
                    LocalDate end=r.getEndDate();
                    LocalDate s1=reservation.getStartDate();
                    LocalDate e1=reservation.getEndDate();
                    if(start.compareTo(e1) > 0 || end.compareTo(s1) < 0)
                        messageResponse.append("This room is already reserved between these dates!\n");
                }
            }
            int roomprice = roomRepository.getOne(reservation.getRoomNumber()).getPrice();
            if (period.getDays() > 30)
                messageResponse.append("Can't reserve a room for a period longer than 30 days! \n");
            reservation.setPrice(period.getDays() * roomprice);
        }
        if (!userRepository.existsById(reservation.getUserId()))
            messageResponse.append("There is no registred user with id <").append(reservation.getUserId()).append(">.\n");
        return messageResponse.toString();
    }

    public ResponseEntity addReservation(Reservation reservation) {
        String responseError = isValid(reservation);
        if (responseError.equals("")) {
            setData(reservation);
            reservationRepository.save(reservation);
            return new ResponseEntity<>("Reservation with id <" + reservation.getId() + "> has been added.", HttpStatus.OK);
        }
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity getReservation(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation == null)
            return new ResponseEntity<>("Reservation with id <" + id + "> doesn't exist.", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>("Reservation with id <" + id + "> doesn't exist.", HttpStatus.BAD_REQUEST);
        }
        String responseError = isValid(reservation);
        if (responseError == null) {

            Room existingRoom = roomRepository.findById(existingReservation.getRoomNumber()).orElse(null);
            roomRepository.save(existingRoom);
            existingReservation.setAnnulled(reservation.getAnnulled());
            existingReservation.setEndDate(reservation.getEndDate());
            existingReservation.setStatus(reservation.getStatus());
            existingReservation.setStartDate(reservation.getStartDate());
            existingReservation.setUserId(reservation.getUserId());
            existingReservation.setRoomNumber(reservation.getRoomNumber());
            existingReservation.setPrice(reservation.getPrice());
            existingRoom = roomRepository.findById(reservation.getRoomNumber()).orElse(null);
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
        if (existingReservation.getStartDate().isBefore(localDate)) {
            return new ResponseEntity<>("Reservation with id <" + id + "> has already started (can't be canceled).", HttpStatus.BAD_REQUEST);
        }
        existingReservation.setAnnulled(true);
        reservationRepository.save(existingReservation);
        return new ResponseEntity<>("Reservation with id <" + id + " succesfully canceled.", HttpStatus.OK);
    }


    public ResponseEntity performCheckIn(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if(existingReservation!=null){
            System.out.println(localDate);
            System.out.println(existingReservation.getStartDate());
            if(!localDate.equals(existingReservation.getStartDate())||localDate.isBefore(existingReservation.getStartDate()))
                return new ResponseEntity("Can not perform check-in until "+existingReservation.getStartDate(),HttpStatus.BAD_REQUEST);
            if(localDate.isAfter(existingReservation.getEndDate()))
                return new ResponseEntity("Check-in time expired!",HttpStatus.BAD_REQUEST);

        }
        existingReservation.setStatus("check-in");
        reservationRepository.save(existingReservation);
        return new ResponseEntity("Check-in performed!",HttpStatus.OK);

    }

    public ResponseEntity performCheckOut(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        return new ResponseEntity("Can not perform check-in until "+existingReservation.getStartDate(),HttpStatus.BAD_REQUEST);

    }
}
