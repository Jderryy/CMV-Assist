package assist.cmv.CMV.service;

import assist.cmv.CMV.model.User;
import assist.cmv.CMV.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public ResponseEntity saveUser(User user) {
        Optional<User> optUser = Optional.ofNullable(user);
        if (optUser.isPresent()) {
            if (repository.findByRoleId(user.getRoleId()) == null)
                return new ResponseEntity<>("There is no role <" + user.getRoleId() + "> available.", HttpStatus.BAD_REQUEST);
            if (user.getName() != null && (user.getName().length() < 5 || user.getName().length() > 20))
                return new ResponseEntity<>("The name length should be between 5 and 20 characters.", HttpStatus.BAD_REQUEST);
            if (user.getEmail() != null && (user.getEmail().length() < 5 || user.getEmail().length() > 30 || !user.getEmail().contains("@") || !user.getEmail().contains(".")))
                return new ResponseEntity<>("Invalid email. (must contain '@', '.' and has a length between 5 and 30 characters.", HttpStatus.BAD_REQUEST);
            if (user.getPhone() != null && (!user.getPhone().matches("[0-9]+") || user.getPhone().length() < 10 || user.getPhone().length() > 20))
                return new ResponseEntity<>("Invalid phone number. (0712345678)", HttpStatus.BAD_REQUEST);
            if (user.getCreditCard() != null && !user.getCreditCard().matches("[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}"))
                return new ResponseEntity<>("Invalid card number. (1234-5678-9123-4567)", HttpStatus.BAD_REQUEST);
            repository.save(user);
            return new ResponseEntity<>("User with id <" + user.getId() + "> has been added.", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getUsers() {
        System.out.println(repository.count());
        if (repository.count() == 0)
            return new ResponseEntity<>("There are no users yet.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity getUserById(int id) {
        if (repository.count() >= 2 && repository.findById(id).orElse(null) != null)
            return new ResponseEntity<>(repository.findById(id), HttpStatus.OK);
        return new ResponseEntity<>("There is no registred user with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getUserByName(String name) {
        if (repository.findUserByName(name) != null)
            return new ResponseEntity<>(repository.findUserByName(name), HttpStatus.OK);
        return new ResponseEntity<>("There is no registred user with name <" + name + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity deleteUser(int id) {
        if (repository.existsById(id))
        {
            repository.deleteById(id);
            return new ResponseEntity<>("User with id <" + id + "> has been removed.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Can't find user with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity updateUser(User user, int id) {
        User existingUser = repository.findById(id).orElse(null);
        if (existingUser != null) {
            if (repository.findByRoleId(user.getRoleId()) == null)
                return new ResponseEntity<>("There is no role <" + user.getRoleId() + "> available.", HttpStatus.BAD_REQUEST);
            if (user.getName() != null && (user.getName().length() < 5 || user.getName().length() > 20))
                return new ResponseEntity<>("The name length should be between 5 and 20 characters.", HttpStatus.BAD_REQUEST);
            if (user.getEmail() != null && (user.getEmail().length() < 5 || user.getEmail().length() > 30 || !user.getEmail().contains("@") || !user.getEmail().contains(".") || repository.findByEmail(user.getEmail()) != null))
            return new ResponseEntity<>("Invalid email. (must contain '@', '.' and has a length between 5 and 30 characters.", HttpStatus.BAD_REQUEST);
            if (user.getPhone() != null && (!user.getPhone().matches("[0-9]+") || user.getPhone().length() < 10 || user.getPhone().length() > 20 || repository.findByPhone(user.getPhone()) != null))
                return new ResponseEntity<>("Invalid phone number. (0712345678)", HttpStatus.BAD_REQUEST);
            if (user.getCreditCard() != null && (!user.getCreditCard().matches("[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}") || repository.findByCreditCard(user.getCreditCard()) != null))
                return new ResponseEntity<>("Invalid card number. (1234-5678-9123-4567)", HttpStatus.BAD_REQUEST);
            System.out.println("asdasd");
            existingUser.setName(user.getName());
            existingUser.setPassword(user.getPassword());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setRoleId(user.getRoleId());
            existingUser.setCreditCard(user.getCreditCard());
            repository.save(existingUser);
            return new ResponseEntity("User with id <" + id + "> has been updated.", HttpStatus.OK);
        }
        return new ResponseEntity("No such user with id <" + id + "> was found.", HttpStatus.BAD_REQUEST);
    }

}
