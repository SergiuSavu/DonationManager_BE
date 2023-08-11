package de.msg.javatraining.donationmanager.controller.role;
import de.msg.javatraining.donationmanager.service.roleService.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRoleWithPermissions(@RequestBody Map<String, Object> request) {
        String roleName = (String) request.get("roleName");
        Set<Integer> permissionIds = (Set<Integer>) request.get("permissionIds");

        if (roleName == null || permissionIds == null || permissionIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Role newRole = roleService.createRoleWithPermissions(roleName, permissionIds);

        if (newRole == null) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(newRole);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Object> updateRoleWithPermissions(@PathVariable Integer roleId, @RequestBody Map<String, Set<Integer>> request) {
        Set<Integer> permissionIds = request.get("permissionIds");

        if (permissionIds == null || permissionIds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Role updatedRole = roleService.updateRoleWithPermissions(roleId, permissionIds);

        if (updatedRole == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRoleWithPermissions(@PathVariable Integer roleId) {
        boolean deleted = roleService.deleteRoleWithPermissions(roleId);

        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
