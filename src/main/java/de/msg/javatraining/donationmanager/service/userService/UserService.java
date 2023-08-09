package de.msg.javatraining.donationmanager.service.userService;

import de.msg.javatraining.donationmanager.controller.dto.UserDTO;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        List<User> usersFromDB = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        UserDTO user = new UserDTO();

        usersFromDB.forEach(
                u -> {
                    user.setId(u.getId());
                    user.setFirstName(u.getFirstName());
                    user.setLastName(u.getLastName());
                    user.setMobileNumber(u.getMobileNumber());
                    user.setUsername(u.getUsername());
                    user.setEmail(u.getEmail());
                    user.setPassword(u.getPassword());
                    user.setRoles(u.getRoles());
                    user.setCampaigns(u.getCampaigns());
                    user.setActive(u.isActive());
                    user.setFirstLogin(u.isFirstLogin());
                    user.setRetryCount(u.getRetryCount());

                    userDTOs.add(user);
                }
        );

        return userDTOs;
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void createUser(User user) {
        //TODO: Implement mailSender for the automatically generated password
        //TODO: Implement password auto generation
        //TODO: New update method for the first time a user logs in
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String tempUsername;
        String lastName = user.getLastName().toLowerCase();
        String firstName = user.getFirstName().toLowerCase();
        if(lastName.length() < 5){
            tempUsername = lastName + firstName.substring(0,2);
        }
        else {
            tempUsername = lastName.substring(0,5).toLowerCase() + firstName.charAt(0);
        }
        int originalLength = tempUsername.length();

        int i = 1;
        String counterString = "";
        while(userRepository.existsByUsername(tempUsername)){
            tempUsername = tempUsername.substring(0 , originalLength);
            tempUsername = tempUsername.concat(String.format("%d", i++));
        }
        user.setUsername(tempUsername);
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
        userDTO.setFirstName(userFromDB.getFirstName());
        userDTO.setLastName(userFromDB.getLastName());
        userDTO.setMobileNumber(userFromDB.getMobileNumber());
        userDTO.setUsername(userFromDB.getUsername());
        userDTO.setEmail(userFromDB.getEmail());
        userDTO.setPassword(userFromDB.getPassword());
        userDTO.setRoles(userFromDB.getRoles());
        userDTO.setCampaigns(userFromDB.getCampaigns());
        userDTO.setActive(userFromDB.isActive());
        userDTO.setFirstLogin(userFromDB.isFirstLogin());
        userDTO.setRetryCount(userFromDB.getRetryCount());

        return userDTO;
    }
}
