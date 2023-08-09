package de.msg.javatraining.donationmanager.service;

import de.msg.javatraining.donationmanager.controller.dto.UserDTO;
import de.msg.javatraining.donationmanager.persistence.model.User;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    public void updateUser(Long id, User newUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "User with id: " + id + " does not exist"
                ));
        if(newUser.getUsername() != null){
            user.setUsername(newUser.getUsername());
        }
        if(newUser.getPassword() != null){
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }
        if(newUser.getRoles() != null){
            user.setRoles(newUser.getRoles());
        }
        if(newUser.getEmail() != null){
            user.setEmail(newUser.getEmail());
        }
        userRepository.save(user);
    }

    public UserDTO getUserById(Long id) {
        User userFromDB = userRepository.findById(id).orElse(null);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userFromDB.getId());
        userDTO.setEmail(userFromDB.getEmail());
        userDTO.setUsername(userFromDB.getUsername());
        userDTO.setPassword(userFromDB.getPassword());
        userDTO.setRoles(userFromDB.getRoles());

        return userDTO;
    }
}
