package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@CrossOrigin(origins = "http://localhost:3000/")
@RestController
public class RoomController {

    @Autowired
    private RoomService service;


    @PostMapping("/addRoom")
    public ResponseEntity addRoom(@RequestBody Room room) {
        return service.addRoom(room);
    }

    @GetMapping("/rooms")
    public ResponseEntity getRooms() {
        return service.getRooms();
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity getRoomById(@PathVariable int id) {
        return service.getRoom(id);
    }

    @GetMapping("/rooms/check/{id}")
    public ResponseEntity getAvailabilityById(@PathVariable int id) { return new ResponseEntity<>(service.isAvailable(id), !service.isAvailable(id) ? HttpStatus.OK : HttpStatus.OK);
    }

//    @GetMapping("/rooms/availablebyTwoDates/{startDate}/{endDate}")
//    public ResponseEntity getAllAvailableRoomsByTwoDates(@PathVariable String startDate, @PathVariable String endDate) {
//        return service.getAvailableRoomsByStartDateAndEndDate(LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//    }

    @GetMapping("/rooms/available")
    public ResponseEntity getAllAvailableRooms() { return  service.getAvailableRooms();}

    @PutMapping("/rooms/update/{id}")
    public ResponseEntity updateRoom(@RequestBody Room room, @PathVariable int id) {
        return service.updateRoom(room, id);
    }

    @DeleteMapping("/rooms/delete/{id}")
    public ResponseEntity deleteRoom(@PathVariable int id) {
        return service.deleteRoom(id);
    }




}
