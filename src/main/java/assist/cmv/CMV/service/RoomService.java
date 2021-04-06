package assist.cmv.CMV.service;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.repository.ReservationRepository;
import assist.cmv.CMV.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RoomService {
    @Autowired
    private RoomRepository repository;

    @Autowired
    private ReservationRepository reservationRepository;

    private String isValidUpdate(Room room) {
        Optional<Room> optionalRoom = Optional.ofNullable(room);
        String response = "";
        if (optionalRoom.isPresent()) {

            //TODO la update nu se modifica id
            if (repository.existsById(room.getId()) && repository.countById(room.getId()) > 1)
                response += "Room with id <" + room.getId() + "> already exists.\n";
            if (repository.existsRoomByNfcTag(room.getNfcTag()) && repository.countByNfcTag(room.getNfcTag()) > 0)
                response += "Two rooms can't have the same NFCTag (" + room.getNfcTag() + ").\n";
            if (room.getRating() == 0)
                response += "";
            else if (room.getRating() < 1 || room.getRating() > 5)
                response += "Rating range is between 1 and 5.\n";
            if (room.getMaxCapacity() > 6)
                response += "Due to pandemic condition, room can hold maximum 6 persons.\n";
            if (room.getFacilities() == null || room.getFacilities().equals(""))
                response += "A room must have at least 1 facility.\n";
            if (room.getReview() == null)
                response += "";
            if (room.getBedsNumber() < 1 || room.getBedsNumber() > 4)
                response += "Due to pandemic condition, a room must hold 1 bed at least and 4 beds maximum.\n";
            if (room.getPrice() < 50 || room.getPrice() > 5000)
                response += "A valid price is between 50 and 5000 €.\n";
        }
        return response;
    }

    private String isValidAdd(Room room) {
        Optional<Room> optionalRoom = Optional.ofNullable(room);
        String response = "";
        if (optionalRoom.isPresent()) {
            if (repository.existsById(room.getId()))
                response += "Room with id <" + room.getId() + "> already exists.\n";
            if (repository.existsRoomByNfcTag(room.getNfcTag()))
                response += "Two rooms can't have the same NFCTag (" + room.getNfcTag() + ").\n";
            if (room.getMaxCapacity() > 6)
                response += "Due to pandemic condition, room can hold maximum 6 persons.\n";
            if (room.getFacilities() == null || room.getFacilities().equals(""))
                response += "A room must have at least 1 facility.\n";
            if (room.getBedsNumber() < 1 || room.getBedsNumber() > 4)
                response += "Due to pandemic condition, a room must hold 1 bed at least and 4 beds maximum.\n";
            if (room.getPrice() < 50 || room.getPrice() > 5000)
                response += "A valid price is between 50 and 5000 €.\n";
        }
        return response;
    }

    public ResponseEntity addRoom(Room room) {
        String response = isValidAdd(room);
        if (response == null || response.equals("")) {
            room.setReview("");
            room.setRating(5);
            repository.save(room);
            return new ResponseEntity<>("Room with id <" + room.getId() + "> has been added.", HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getRooms() {
        if (repository.count() == 0)
            return new ResponseEntity<>("Currently the hotel has no rooms.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity getRoom(int id) {
        if (repository.findById(id).orElse(null) != null)
            return new ResponseEntity<>(repository.findById(id), HttpStatus.OK);
        return new ResponseEntity<>("We don't have a room with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity updateRoom(Room room, int id) {
        Room existingRoom = repository.findById(id).orElse(null);
        String response = isValidUpdate(room);
        if (existingRoom != null) {
            if (response == null || response.equals("")) {
                existingRoom.setCleaned(room.getCleaned());
                existingRoom.setFacilities(room.getFacilities());
                existingRoom.setMaxCapacity(room.getMaxCapacity());
                existingRoom.setNfcTag(room.getNfcTag());
                existingRoom.setPetFriendly(room.getPetFriendly());
                existingRoom.setPrice(room.getPrice());
                existingRoom.setSmoking(room.getSmoking());
                repository.save(existingRoom);
                return new ResponseEntity<>("Room with id <" + id + "> has been updated.", HttpStatus.OK);
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Can't find room with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity deleteRoom(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return new ResponseEntity<>("Room with id <" + id + "> has been removed.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Can't find room with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getAvailableRoomsByStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<Room> allAvailableRooms = new ArrayList<>();
        for (Reservation reservation : allReservations) {
            LocalDate currentReservationStartDate = reservation.getStartDate();
            LocalDate currentReservationEndDate = reservation.getEndDate();

            if (currentReservationStartDate.compareTo(endDate) > 0 || currentReservationEndDate.compareTo(startDate) < 0)
                allAvailableRooms.add(repository.findById(reservation.getRoomNumber()).orElse(null));
            else if ((startDate.compareTo(currentReservationStartDate) * currentReservationStartDate.compareTo(endDate) > 0) || (startDate.compareTo(currentReservationEndDate) * currentReservationEndDate.compareTo(endDate) > 0))
                allAvailableRooms.add(repository.findById(reservation.getRoomNumber()).orElse(null));
        }
        return new ResponseEntity<>(allAvailableRooms, HttpStatus.OK);
    }


}
