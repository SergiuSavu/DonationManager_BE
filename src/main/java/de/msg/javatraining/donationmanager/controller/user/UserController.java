package de.msg.javatraining.donationmanager.controller.user;

import de.msg.javatraining.donationmanager.controller.dto.UserDTO;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.service.userService.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

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
        return userService.createUser(user);
    }

    @PutMapping("/toggle/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public void toggleUserActive(@PathVariable("id") Long id) {
        userService.toggleUserActive(id);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User newUser, HttpServletRequest request){
        // Extract the token from the header
        String token = request.getHeader("Authorization"); // Assuming the header is "Bearer <token>"
        System.out.println("token: " + token);

        // Decode the JWT
        DecodedJWT jwt = JWT.decode(token);

        // Extract the username
        String usernameFromToken = jwt.getClaim("sub").asString();
        System.out.println("userId: " + usernameFromToken);

        // Fetch the user by username
        User userFromToken = userService.findByUsername(usernameFromToken);

        if(userFromToken == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        return userService.updateUser(userFromToken, id, newUser);
    }

    //   getRoles, addRoles, deleteRoles with @PutMapping("/roles")... "/roles/{id}",
    //   "roles/add/{id}", "roles/delete/{id}")
}
