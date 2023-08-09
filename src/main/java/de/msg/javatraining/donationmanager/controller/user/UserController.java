package de.msg.javatraining.donationmanager.controller.user;

import de.msg.javatraining.donationmanager.controller.dto.UserDTO;
import de.msg.javatraining.donationmanager.persistence.model.User;
import de.msg.javatraining.donationmanager.service.RefreshTokenService;
import de.msg.javatraining.donationmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @GetMapping("/all")
    public List<User> getAllUsers(){
       return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }

    @PostMapping("/new")
    public void createUser(@RequestBody User user){
        userService.createUser(user);
    }

    @DeleteMapping("/delete/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(@PathVariable("id") Long id) {
        refreshTokenService.deleteRefreshTokenForUser(id);
        userService.deleteUserById(id);
    }

    @PutMapping("/update/{id}")
    public void updateUser(@PathVariable("id") Long id, @RequestBody User newUser){
        userService.updateUser(id, newUser);
    }
}
