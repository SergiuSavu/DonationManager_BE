package de.msg.javatraining.donationmanager.controller.permission;

import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permissions")
public class PermissionController {


    @Autowired
    private PermissionService permissionService;

    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> addPermissionToUser(@PathVariable Long userId, @RequestBody PermissionEnum permission) {
        boolean added = permissionService.addPermissionToUser(userId, permission);

        if (added) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if permission was not granted
        }
    }

    @PostMapping("/{userId}/delete")
    public ResponseEntity<Void> deletePermissionFromUser(@PathVariable Long userId, @RequestBody PermissionEnum permission) {
        boolean deleted = permissionService.deletePermissionFromUser(userId, permission);

        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if permission was not granted
        }
    }
}
