package de.msg.javatraining.donationmanager.service.permissionService;

import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Role addPermissionToRole(Long userId, Integer roleId, PermissionEnum permissionToAdd) {
  
        if (permissionToAdd == null) {
            throw new IllegalArgumentException("Permission to add cannot be null.");
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
                        throw new NullPointerException("Permission already exists.");}
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
        throw new IllegalArgumentException("User not found or permission not available.");
    }


    public Role deletePermissionFromRole(Long userId, Integer roleId, PermissionEnum permissionToDelete) {
        if (userId == null || permissionToDelete == null) {
            throw new IllegalArgumentException("User ID and permission to delete cannot be null.");
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
