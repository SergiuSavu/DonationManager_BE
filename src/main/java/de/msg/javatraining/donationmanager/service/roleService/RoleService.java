package de.msg.javatraining.donationmanager.service.roleService;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role createRoleWithPermissions(String roleName, Set<Integer> permissionIds) {
        Role role = new Role();
        role.setName(ERole.valueOf(roleName)); // ERole is an enum you should define
        Set<PermissionEnum> permissions = new HashSet<>();

        for (Integer permissionId : permissionIds) {
            PermissionEnum permission = PermissionEnum.getById(permissionId);
            if (permission != null) {
                permissions.add(permission);
            }
        }

        role.setPermissions(permissions);
        // Save the role in the database

        return role;
    }

    public Role updateRoleWithPermissions(Integer roleId, Set<Integer> permissionIds) {
        Optional<Role> optionalRole = roleRepository.findById(Long.valueOf(roleId));

        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();

            Set<PermissionEnum> permissions = new HashSet<>();
            for (Integer permissionId : permissionIds) {
                PermissionEnum permission = PermissionEnum.getById(permissionId);
                if (permission != null) {
                    permissions.add(permission);
                }
            }

            role.setPermissions(permissions);
            return roleRepository.save(role); // Update the role and save changes
        }

        return null; // Role not found
    }

    public boolean deleteRoleWithPermissions(Integer roleId) {
        Optional<Role> optionalRole = roleRepository.findById(Long.valueOf(roleId));

        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            roleRepository.delete(role);
            return true;
        }

        return false;
    }
}
