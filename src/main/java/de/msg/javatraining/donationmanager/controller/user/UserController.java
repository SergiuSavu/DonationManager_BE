package de.msg.javatraining.donationmanager.controller.user;

import de.msg.javatraining.donationmanager.controller.dto.UserDTO;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.service.userService.UserException;
import de.msg.javatraining.donationmanager.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<UserDTO> getAllUsers(){
       return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createUser(@RequestBody User user){
        ResponseEntity<?> response;
        try{
            userService.createUser(user);
            response = new ResponseEntity<>(user, HttpStatusCode.valueOf(200));
        }
        catch (UserException exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/toggle/{id}")
    public ResponseEntity<?> toggleUserActive(@PathVariable("id") Long id) {
        ResponseEntity<?> response;
        try{
            userService.toggleUserActive(id);
            response = new ResponseEntity<>(id, HttpStatusCode.valueOf(200));
        }
        catch (UserException exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User newUser){
        ResponseEntity<?> response;
        try{
            userService.updateUser(id, newUser);
            response = new ResponseEntity<>(newUser, HttpStatusCode.valueOf(200));
        }
        catch (UserException exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

}
