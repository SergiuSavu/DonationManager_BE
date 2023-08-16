package tests;

import de.msg.javatraining.donationmanager.controller.permission.PermissionController;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)

class PermissionControllerTest {

    @InjectMocks
    private PermissionController permissionController;

    @Mock
    private PermissionService permissionService;

    @Test
    void testAddPermissionToUser_Success() {
        when(permissionService.addPermissionToRole(anyLong(), any(Role.class), any(PermissionEnum.class)))
                .thenReturn(PermissionEnum.PERMISSION_MANAGEMENT);

        ResponseEntity<Void> response = permissionController.addPermissionToRole(1L, new Role(), PermissionEnum.PERMISSION_MANAGEMENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAddPermissionToUser_Failure() {
        when(permissionService.addPermissionToRole(anyLong(), any(Role.class), any(PermissionEnum.class)))
                .thenReturn(null); // Simulating permission not granted

        ResponseEntity<Void> response = permissionController.addPermissionToRole(1L, new Role(), PermissionEnum.CAMP_IMPORT);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeletePermissionFromUser_Success() {
        when(permissionService.deletePermissionFromRole(anyLong(), any(Role.class), any(PermissionEnum.class)))
                .thenReturn(PermissionEnum.BENEF_MANAGEMENT);

        ResponseEntity<Void> response = permissionController.deletePermissionFromRole(1L, new Role(), PermissionEnum.BENEF_MANAGEMENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeletePermissionFromUser_Failure() {
        when(permissionService.deletePermissionFromRole(anyLong(), any(Role.class), any(PermissionEnum.class)))
                .thenReturn(null); // Simulating permission not deleted

        ResponseEntity<Void> response = permissionController.deletePermissionFromRole(1L, new Role(), PermissionEnum.BENEF_MANAGEMENT);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Add more test cases as needed

    // Remember to adjust return values and parameters based on your application's logic
}
