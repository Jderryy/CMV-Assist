package assist.cmv.CMV.service;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.model.User;
import assist.cmv.CMV.repository.ReservationRepository;
import assist.cmv.CMV.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    
    //todo de rezolvat conflictul cu NFCTag

    @Autowired
    private RoomRepository repository;

    @Autowired
    private ReservationRepository reservationRepository;

    public ResponseEntity addRoom(Room room) {
        Optional<Room> optionalRoom = Optional.ofNullable(room);
        if (optionalRoom.isPresent()) {
            if (repository.existsById(room.getId()))
                return new ResponseEntity<>("Room with id <" + room.getId() + "> already exists.", HttpStatus.BAD_REQUEST);
            if (repository.existsRoomByNfcTag(room.getNfcTag()))
                return new ResponseEntity<>("Two rooms can't have the same NFCTag (" + room.getNfcTag() + ").", HttpStatus.BAD_REQUEST);
            if (room.getRating() < 1 || room.getRating() > 5)
                return new ResponseEntity<>("Rating range is between 1 and 5.", HttpStatus.BAD_REQUEST);
            if (room.getMaxCapacity() > 6)
                return new ResponseEntity<>("Due to pandemic condition, room can hold maximum 6 persons.", HttpStatus.BAD_REQUEST);
            if (room.getFacilities() == null || room.getFacilities().equals(""))
                return new ResponseEntity<>("A room must have at least 1 facility. :)", HttpStatus.BAD_REQUEST);
            if (room.getReview() == null || room.getReview().length() < 10 || room.getReview().length() > 100)
                return new ResponseEntity<>("A review is valid only if is between 10 and 100 characters.", HttpStatus.BAD_REQUEST);
            if (room.getBedsNumber() < 1 || room.getBedsNumber() > 4)
                return new ResponseEntity<>("Due to pandemic condition, a room must hold 1 bed at least and 4 beds maximum.", HttpStatus.BAD_REQUEST);
            if (room.getPrice() < 50 || room.getPrice() > 5000)
                return new ResponseEntity<>("A valid price is between 50 and 5000 €.", HttpStatus.BAD_REQUEST);

            repository.save(room);
            return new ResponseEntity<>("Room with id <" + room.getId() + "> has been added.", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getRooms() {
        if (repository.count() == 0)
            return new ResponseEntity<>("Currently the hotel has no room.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity getRoom(int id) {
        if (repository.findById(id).orElse(null) != null)
            return new ResponseEntity<>(repository.findById(id), HttpStatus.OK);
        return new ResponseEntity<>("We don't have a room with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity updateRoom(Room room, int id) {
        Room existingRoom = repository.findById(id).orElse(null);
        if (repository.existsById(id)) {
            if (repository.existsById(room.getId()) && repository.countById(room.getId()) > 1)
                return new ResponseEntity<>("The id <" + room.getId() + "> is already assigned to a room.", HttpStatus.BAD_REQUEST);
            if (repository.existsRoomByNfcTag(room.getNfcTag()) && room.getNfcTag() != existingRoom.getNfcTag())
                return new ResponseEntity<>("There is already a room with NFCTag <" + room.getNfcTag() + ">.", HttpStatus.BAD_REQUEST);
            if (repository.findByReservationId(room.getReservationId()) == null)
                return new ResponseEntity<>("There is no reservation with id <" + room.getReservationId() + "> that can be assigned to this room.", HttpStatus.BAD_REQUEST);
            existingRoom.setAvailability(room.getAvailability());
            existingRoom.setCleaned(room.getCleaned());
            existingRoom.setFacilities(room.getFacilities());
            //existingRoom.setId(room.getId());
            existingRoom.setMaxCapacity(room.getMaxCapacity());
            existingRoom.setNfcTag(room.getNfcTag());
            existingRoom.setPetFriendly(room.getPetFriendly());
            existingRoom.setPrice(room.getPrice());
            existingRoom.setReservationId(room.getReservationId());
            existingRoom.setSmoking(room.getSmoking());

            Reservation reservation = reservationRepository.findById(room.getReservationId()).orElse(null);
            reservation.setRoomNumber(room.getId());
            reservationRepository.save(reservation);

            repository.save(existingRoom);
            return new ResponseEntity<>("Room with id <" + id + "> has been updated.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Can't find room with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity deleteRoom(int id) {
        if (repository.existsById(id))
        {
            repository.deleteById(id);
            return new ResponseEntity<>("Room with id <" + id + "> has been removed.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Can't find room with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public void test2 () {
        System.out.println("mergegegegegeegeg");
    }
}
