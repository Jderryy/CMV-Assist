package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.User;
import assist.cmv.CMV.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private BCryptPasswordEncoder encoder;


    @PostMapping("/addUser")
    public ResponseEntity addUser(@RequestBody User user) {
        boolean correct = true;
        String pwd = user.getPassword();
        String encryptPassword = encoder.encode(pwd);
        user.setPassword(encryptPassword);
        if (correct) {
            return service.saveUser(user);
        }
        return service.saveUser(null);

    }

    @GetMapping("/users")
    public ResponseEntity getUsers() {
        return service.getUsers();
    }

    @GetMapping("/userById/{id}")
    public ResponseEntity findUserById(@PathVariable int id) {
        return service.getUserById(id);
    }

    @GetMapping("/user/{name}")
    public ResponseEntity findUserByName(@PathVariable String name) {
        return service.getUserByName(name);
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity updateUser(@RequestBody User user, @PathVariable int id) {
        return service.updateUser(user, id);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable int id) {
        return service.deleteUser(id);
    }

    @GetMapping("/users/emails")
    public ResponseEntity findAllEmails(String body, String message) {
        return service.sendEmailToAllUsers(body, message);
    }
}