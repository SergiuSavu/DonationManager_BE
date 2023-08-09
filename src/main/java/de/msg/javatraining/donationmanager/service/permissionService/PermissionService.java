package de.msg.javatraining.donationmanager.service.permissionService;

import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public boolean addPermissionToUser(Long userId, PermissionEnum permission) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if the user has PERMISSION_MANAGEMENT permission
            if (!hasPermission(user, PermissionEnum.PERMISSION_MANAGEMENT)) {
                return false; // User doesn't have the required permission
            }

            // Find the role containing the permission
            Optional<Role> permissionRole = findRoleWithPermission(permission);

            if (permissionRole.isPresent()) {
                user.getRoles().add(permissionRole.get());
                userRepository.save(user);
                return true; // Permission added successfully
            }
        }

        return false; // User not found or permission not available
    }

    public boolean deletePermissionFromUser(Long userId, PermissionEnum permission) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if the user has PERMISSION_MANAGEMENT permission
            if (!hasPermission(user, PermissionEnum.PERMISSION_MANAGEMENT)) {
                return false; // User doesn't have the required permission
            }

            // Find the role containing the permission
            Optional<Role> permissionRole = findRoleWithPermission(permission);

            if (permissionRole.isPresent()) {
                user.getRoles().remove(permissionRole.get());
                userRepository.save(user);
                return true; // Permission removed successfully
            }
        }

        return false; // User not found or permission not available
    }

    private boolean hasPermission(User user, PermissionEnum permission) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getPermissions().contains(permission));
    }

    private Optional<Role> findRoleWithPermission(PermissionEnum permission) {
        return roleRepository.findAll().stream()
                .filter(role -> role.getPermissions().contains(permission))
                .findFirst();
    }
}
