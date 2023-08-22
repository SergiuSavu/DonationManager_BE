package de.msg.javatraining.donationmanager.service.userService;

import de.msg.javatraining.donationmanager.controller.dto.UserDTO;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.NotificationService;
import de.msg.javatraining.donationmanager.service.emailService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    NotificationService notificationService;

    public List<UserDTO> getAllUsers() {
        List<User> usersFromDB = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        usersFromDB.forEach(
                u -> {
                    UserDTO user = new UserDTO();
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

    public void toggleUserActive(Long id) {

        if (userRepository.findById(id).isEmpty())
        {
            throw new IllegalStateException("User with user id: " + id + " does not exist");
        }
        else {
            User user = userRepository.findById(id).get();
            user.setActive(!user.isActive());
            if(user.isActive()) {
                resetRetryCount(user.getUsername());
            }
            userRepository.save(user);
        }
    }

    public ResponseEntity<?> createUser(User user) {
        try{
            userValidations(user);

            //Username generation
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
            while(userRepository.existsByUsername(tempUsername)){
                tempUsername = tempUsername.substring(0 , originalLength);
                tempUsername = tempUsername.concat(String.format("%d", i++));
            }
            user.setUsername(tempUsername);


//            EmailRequest emailRequest = new EmailRequest();
//            emailRequest.setDestination(user.getEmail());
//            emailRequest.setSubject("User account created");
//            emailRequest.setMessage(
//                    "User account created successfully.\n" +
//                            "Login information: \n" +
//                            "Username: " +  user.getUsername() + "\n" +
//                            "Password: " +  generatedPassword + "\n" +
//                            "This a randomly generated password that will need to be changed on your first login."
//            );
//        emailService.sendSimpleMessage(emailRequest);
//            user.setPassword(passwordEncoder.encode(generatedPassword));
////        emailService.sendSimpleMessage(emailRequest);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                    new NotificationParameter(user.getFirstName()),
                    new NotificationParameter(user.getLastName()),
                    new NotificationParameter(user.getMobileNumber()),
                    new NotificationParameter(user.getEmail()),
                    new NotificationParameter(user.getUsername())
            ));
            notificationService.saveNotification(user, parameters, NotificationType.WELCOME_NEW_USER);
        }
        catch (IllegalStateException | IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);

        }
        return new ResponseEntity<>("User created successfully.", HttpStatus.OK);
    }

    public void resetRetryCount(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "User with username: " + username + " does not exist."
                ));
        user.setRetryCount(0);

        userRepository.save(user);
    }

    public ResponseEntity<?> updateRetryCount(String username) {
        try {
            if(userRepository.findByUsername(username).isEmpty()){
                throw new IllegalStateException("User with username: " + username + " does not exist.");
            }
            User user = userRepository.findByUsername(username).get();

            user.setRetryCount(user.getRetryCount()+1);
            if(user.getRetryCount() == 5){
                user.setActive(false);
            }

            userRepository.save(user);
        }
        catch (IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("Retry count updated", HttpStatus.OK);
    }


    public ResponseEntity<?> updateUser(Long id, User newUser) {
        try {
            if(userRepository.findById(id).isEmpty()){
                throw new IllegalStateException("User with id: " + id + " does not exist.");
            }
            User user = userRepository.findById(id).get();

            userValidations(newUser);

            if(newUser.getFirstName() != null){
                user.setFirstName(newUser.getFirstName());
            }
            if(newUser.getLastName() != null){
                user.setLastName(newUser.getLastName());
            }
            if(newUser.getMobileNumber() != null){
                user.setMobileNumber(newUser.getMobileNumber());
            }
            if(newUser.getEmail() != null){
                user.setEmail(newUser.getEmail());
            }
            if(newUser.getPassword() != null){
                user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            }
            if(!newUser.getRoles().isEmpty()){
                user.setRoles(newUser.getRoles());
            }
            if(!newUser.getCampaigns().isEmpty() ){
                user.setCampaigns(newUser.getCampaigns());
            }
            if(newUser.isActive() != user.isActive()){
                user.setActive(newUser.isActive());
                if(user.isActive()) {
                    user.setRetryCount(0);
                }
            }
            if(!newUser.isFirstLogin())
                user.setFirstLogin(false);

            userRepository.save(user);
        }
        catch(IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("User successfully updated.", HttpStatus.OK);
    }

    private void userValidations(User user) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalStateException("Email already in use.");
            }

            if (userRepository.existsByMobileNumber(user.getMobileNumber())) {
                throw new IllegalStateException("Mobile number already in use.");
            }

//            if (!user.getMobileNumber().matches("^(?:\\+?40|0)?7\\d{8}$")) {
//                throw new IllegalArgumentException("Mobile number is not valid.");
//            }
//
//            if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//                throw new IllegalArgumentException("Email is not valid.");
//            }
    }

    public ResponseEntity<?> getUserById(Long id) {
        UserDTO userDTO = new UserDTO();
        try {
            if(userRepository.findById(id).isEmpty()){
                throw new IllegalStateException("User with id: " + id + " does not exist.");
            }
            User userFromDB = userRepository.findById(id).get();


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


        }
        catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
