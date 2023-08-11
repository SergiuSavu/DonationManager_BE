import de.msg.javatraining.donationmanager.controller.role.RoleController;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.service.roleService.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @InjectMocks
    private RoleController roleController;

    @Mock
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateRoleWithPermissions_ValidData_Success() {
        String roleName = "ROLE_CEN";
        Set<Integer> permissionIds = Collections.singleton(1);
        Map<String, Object> request = new HashMap<>();
        request.put("roleName", roleName);
        request.put("permissionIds", permissionIds);

        Role createdRole = new Role();
        createdRole.setName(ERole.valueOf(roleName));

        when(roleService.createRoleWithPermissions(roleName, permissionIds)).thenReturn(createdRole);

        ResponseEntity<Role> response = roleController.createRoleWithPermissions(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdRole, response.getBody());
    }

    @Test
    public void testCreateRoleWithPermissions_InvalidData_BadRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("roleName", "ROLE_CEN");
        // permissionIds not provided

        ResponseEntity<Role> response = roleController.createRoleWithPermissions(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testUpdateRoleWithPermissions_ValidData_Success() {
        Integer roleId = 1;
        Set<Integer> permissionIds = Collections.singleton(1);
        Map<String, Set<Integer>> request = new HashMap<>();
        request.put("permissionIds", permissionIds);

        Role updatedRole = new Role();
        updatedRole.setId(roleId);

        when(roleService.updateRoleWithPermissions(roleId, permissionIds)).thenReturn(updatedRole);
        ResponseEntity<Object> response = roleController.updateRoleWithPermissions(roleId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRole, response.getBody());
    }

    @Test
    public void testUpdateRoleWithPermissions_InvalidData_NotFound() {
        Integer roleId = 1;

        // permissionIds not provided
        Map<String, Set<Integer>> request = new HashMap<>();
        ResponseEntity<Object> response = roleController.updateRoleWithPermissions(roleId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testDeleteRoleWithPermissions_ValidData_Success() {
        Integer roleId = 1;
        when(roleService.deleteRoleWithPermissions(roleId)).thenReturn(true);
        ResponseEntity<Void> response = roleController.deleteRoleWithPermissions(roleId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteRoleWithPermissions_InvalidData_NotFound() {
        Integer roleId = 1;
        when(roleService.deleteRoleWithPermissions(roleId)).thenReturn(false);
        ResponseEntity<Void> response = roleController.deleteRoleWithPermissions(roleId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
