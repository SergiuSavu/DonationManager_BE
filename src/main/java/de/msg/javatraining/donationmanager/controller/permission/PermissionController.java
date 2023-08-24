package de.msg.javatraining.donationmanager.controller.permission;
import de.msg.javatraining.donationmanager.exceptions.permission.PermissionException;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

    @PostMapping("/{roleId}/{userId}/add")
    public ResponseEntity<?> addPermissionToRole(@PathVariable("userId") Long userId, @PathVariable("roleId") Integer roleId, @RequestBody PermissionEnum permission) {
        ResponseEntity<?> response;
        try{
            Role p = permissionService.addPermissionToRole(userId, roleId, permission);
            response = new ResponseEntity<>(p, HttpStatusCode.valueOf(200));
        }
        catch (PermissionException exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @DeleteMapping("/{roleId}/{userId}/delete")
    public ResponseEntity<?> deletePermissionFromRole(@PathVariable("userId") Long userId, @PathVariable("roleId") Integer roleId, @RequestBody PermissionEnum permission) throws PermissionException {
        ResponseEntity<?> response;

        try{
            Role p = permissionService.deletePermissionFromRole(userId, roleId, permission);
            response = new ResponseEntity<>(p, HttpStatusCode.valueOf(200));
        }
        catch (PermissionException exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }
}
