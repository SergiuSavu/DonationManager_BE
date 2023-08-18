package de.msg.javatraining.donationmanager.controller.permission;

import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {


    @Autowired
    private PermissionService permissionService;


    @GetMapping("/all")
    public List<PermissionEnum> getAllPermissions(){
        return permissionService.getAllPermissions();
    }

    @GetMapping("/{roleId}/all")
    public List<PermissionEnum> getAllPermissions(@PathVariable Long roleId){
        return permissionService.getAllPermissions(roleId);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> addPermissionToRole(@PathVariable Long userId, @RequestBody Role role, @RequestBody PermissionEnum permission) {
        PermissionEnum p = permissionService.addPermissionToRole(userId, role, permission);

        if (p!=null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if permission was not granted
        }
    }

    @PostMapping("/{userId}/delete")
    public ResponseEntity<Void> deletePermissionFromRole(@PathVariable Long userId, @RequestBody Role role, @RequestBody PermissionEnum permission) {
        PermissionEnum p = permissionService.deletePermissionFromRole(userId, role, permission);

        if (p!=null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if permission was not granted
        }
    }
}
