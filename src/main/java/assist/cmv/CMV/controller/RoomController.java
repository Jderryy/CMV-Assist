package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.repository.RoomRepository;
import assist.cmv.CMV.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/rooms/update/{id}")
    public ResponseEntity updateRoom(@RequestBody Room room, @PathVariable int id) {
        return service.updateRoom(room, id);
    }

    @DeleteMapping("/rooms/delete/{id}")
    public ResponseEntity deleteRoom(@PathVariable int id) {
        return service.deleteRoom(id);
    }

}
