package de.msg.javatraining.donationmanager.service.permissionService;

import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public boolean addPermissionToUser(Long userIdADMIN, Long userIdTARGET, PermissionEnum permissionToAdd) {
        if (permissionToAdd == null) {
            return false; // Invalid input
        }

        Optional<User> userADMIN = userRepository.findById(userIdADMIN);
        Optional<User> userTARGET = userRepository.findById(userIdTARGET);

        if (!userADMIN.isPresent() || !userTARGET.isPresent()) {
            return false; // User not found
        }

        if (userADMIN != null && userTARGET != null) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {

                    for (Role targetRole : userTARGET.get().getRoles()) {
                        if (targetRole.getName() == ERole.ROLE_ADM
                        && !targetRole.getPermissions().contains(permissionToAdd) &&
                                (permissionToAdd == PermissionEnum.PERMISSION_MANAGEMENT || permissionToAdd == PermissionEnum.USER_MANAGEMENT))
                        {targetRole.getPermissions().add(permissionToAdd);
                            roleRepository.save(targetRole);
                            return true; // Permission added successfully
                        }
                        else if (targetRole.getName() == ERole.ROLE_MGN
                                && !targetRole.getPermissions().contains(permissionToAdd) &&
                                (permissionToAdd == PermissionEnum.CAMP_MANAGEMENT || permissionToAdd == PermissionEnum.BENEF_MANAGEMENT
                                || permissionToAdd == PermissionEnum.DONATION_MANAGEMENT || permissionToAdd == PermissionEnum.DONATION_APPROVE
                                || permissionToAdd == PermissionEnum.DONATION_REPORTING || permissionToAdd == PermissionEnum.CAMP_REPORTING
                                || permissionToAdd == PermissionEnum.CAMP_IMPORT))
                        {
                            targetRole.getPermissions().add(permissionToAdd);
                            roleRepository.save(targetRole);
                            return true; // Permission added successfully
                        }
                         else if (targetRole.getName() == ERole.ROLE_CEN
                                && !targetRole.getPermissions().contains(permissionToAdd) &&
                                (permissionToAdd == PermissionEnum.BENEF_MANAGEMENT || permissionToAdd == PermissionEnum.DONATION_MANAGEMENT ||
                                        permissionToAdd == PermissionEnum.DONATION_REPORTING || permissionToAdd == PermissionEnum.CAMP_REPORTING))
                         {
                             targetRole.getPermissions().add(permissionToAdd);
                             roleRepository.save(targetRole);
                             return true; // Permission added successfully
                         }
                         else if (targetRole.getName() == ERole.ROLE_REP && !targetRole.getPermissions().contains(permissionToAdd) &&
                                permissionToAdd == PermissionEnum.CAMP_REPORT_RESTRICTED)
                         {
                             targetRole.getPermissions().add(permissionToAdd);
                             roleRepository.save(targetRole);
                             return true; // Permission added successfully
                         }
                    }

                    }
                }
            }
        return false; // User not found or permission not available
    }


    public boolean deletePermissionFromUser(Long userIdADMIN, Long userIdTARGET, PermissionEnum permissionToDelete) {
        if (userIdADMIN == null || userIdTARGET == null || permissionToDelete == null) {
            return false; // Invalid input
        }

        Optional<User> userADMIN = userRepository.findById(userIdADMIN);
        Optional<User> userTARGET = userRepository.findById(userIdTARGET);

        if (!userADMIN.isPresent() || !userTARGET.isPresent()) {
            return false; // User not found
        }

        boolean i = false;
        if (userADMIN != null && userTARGET != null) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    for (Role targetRole : userTARGET.get().getRoles()) {
                        if (targetRole.getPermissions().contains(permissionToDelete)) {
                            targetRole.getPermissions().remove(permissionToDelete);
                            roleRepository.save(targetRole);
                            i = true; // Permission removed successfully
                        }
                    }
                }
            }
        }

        return i; // User not found or permission not available
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
}
