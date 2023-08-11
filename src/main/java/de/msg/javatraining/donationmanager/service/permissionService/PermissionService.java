package de.msg.javatraining.donationmanager.service.permissionService;

import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public PermissionEnum addPermissionToUser(Long userId, Role role, PermissionEnum permissionToAdd) {
        if (permissionToAdd == null) {
            throw new IllegalArgumentException("Permission to add cannot be null.");
        }

        Optional<User> userADMIN = userRepository.findById(userId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    if (role.getPermissions().contains(permissionToAdd)) {
                        throw new IllegalArgumentException("Permission already exists.");}
                    else {
                        role.getPermissions().add(permissionToAdd);
                        roleRepository.save(role);
                        return permissionToAdd;
                    }
                }
            }
        }
        throw new IllegalArgumentException("User not found or permission not available.");
    }



    public PermissionEnum deletePermissionFromUser(Long userId, Role role, PermissionEnum permissionToDelete) {
        if (userId == null || permissionToDelete == null) {
            throw new IllegalArgumentException("User ID and permission to delete cannot be null.");
        }

        Optional<User> userADMIN = userRepository.findById(userId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    if (role.getPermissions().contains(permissionToDelete)) {
                        role.getPermissions().remove(permissionToDelete);
                        roleRepository.save(role);
                        return permissionToDelete;
                    } else {
                        throw new IllegalArgumentException("Permission to delete does not exist.");
                    }
                }
            }
        }
        throw new IllegalArgumentException("User not found or permission not available.");
    }



    public boolean hasPermission(User user, PermissionEnum permission) {
        for (Role role : user.getRoles()) {
            if (role.getPermissions().contains(permission)) {
                return true;
            }
        }
        return false;
    }

    public Set<Role> getRoles(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::getRoles).orElse(null);
    }
}
