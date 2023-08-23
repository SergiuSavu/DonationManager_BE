package de.msg.javatraining.donationmanager.service.permissionService;

import de.msg.javatraining.donationmanager.exceptions.permission.PermissionException;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<PermissionEnum> getAllPermissions(){
        return Arrays.stream(PermissionEnum.values()).toList();
    }

    public List<PermissionEnum> getAllPermissions(Long roleId){
        Optional<Role> role = roleRepository.findById(roleId);
        return role.get().getPermissions().stream().toList();
    }

    public Role addPermissionToRole(Long userId, Integer roleId, PermissionEnum permissionToAdd) throws PermissionException{
  
        if (permissionToAdd == null) {
            throw new PermissionException("Permission to add cannot be null.","Permission_to_add_cannot_be_null.");
        }

        Set<PermissionEnum> s = new HashSet<>(); s.add(permissionToAdd);
        // Check if the permission exists in the PermissionRepository
        //if (!permissionRepository.exists(new PermissionEnumWrapper(s))) {
        //    throw new IllegalArgumentException("Permission does not exist.");
        //}

        Optional<User> userADMIN = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    if (role.get().getPermissions().contains(permissionToAdd)) {
                        throw new PermissionException("Permission already exists.","Permission_already_exists.");}
                    else {
                        Set<PermissionEnum> permissions = role.get().getPermissions();
                        permissions.add(permissionToAdd);
                        role.get().setPermissions(permissions);
                        return roleRepository.save(role.get());
                        //return permissionToAdd;
                    }
                }
            }
        }
        throw new PermissionException("User not found or permission not available to edit roles.", "User_not_found_or_permission_not_available_to_edit_roles.");
    }


    public Role deletePermissionFromRole(Long userId, Integer roleId, PermissionEnum permissionToDelete) throws PermissionException {
        if (permissionToDelete == null) {
            throw new PermissionException("Permission to delete cannot be null.", "Permission_to_delete_cannot_be_null.");
        }

        Set<PermissionEnum> pp = new HashSet<>(); pp.add(permissionToDelete);
        // Check if the permission exists in the PermissionRepository
        //if (!permissionRepository.exists(new PermissionEnumWrapper(pp))) {
        //    throw new IllegalArgumentException("Permission does not exist.");
        //}

        Optional<User> userADMIN = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    if (role.get().getPermissions().contains(permissionToDelete)) {
                        Set<PermissionEnum> permissions = role.get().getPermissions();
                        permissions.remove(permissionToDelete);
                        role.get().setPermissions(permissions);
                        return roleRepository.save(role.get());
                        //return permissionToDelete;
                    } else {
                        throw new PermissionException("Permission to delete does not exist.", "Permission_to_delete_does_not_exist.");
                    }
                }
            }
        }
        throw new PermissionException("User not found or permission not available to edit roles.", "User_not_found_or_permission_not_available_to_edit_roles.");
    }



    public boolean hasPermission(User user, PermissionEnum permission) {
        for (Role role : user.getRoles()) {
            if (role.getPermissions().contains(permission)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Role> findRoleWithPermission(PermissionEnum permission) {
        return roleRepository.findAll().stream()
                .filter(role -> role.getPermissions().contains(permission))
                .findFirst();
    }

    public Set<Role> getRoles(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::getRoles).orElse(null);
    }


    public List<User> getUsersWithPermission(PermissionEnum permission) {
        // Fetch all users
        List<User> allUsers = userRepository.findAll();

        // Filter users based on the permission and collect them into an ArrayList
        return allUsers.stream()
                .filter(user -> hasPermission(user, permission))
                .collect(Collectors.toList());
    }

}
