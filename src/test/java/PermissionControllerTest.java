
import de.msg.javatraining.donationmanager.controller.permission.PermissionController;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PermissionControllerTest {

    @InjectMocks
    private PermissionController permissionController;

    @Mock
    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPermissionToUser_Success() {
        when(permissionService.addPermissionToUser(anyLong(), anyLong(), any(PermissionEnum.class))).thenReturn(true);

        ResponseEntity<Void> response = permissionController.addPermissionToUser(1L, 2L, PermissionEnum.CAMP_IMPORT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAddPermissionToUser_Failure() {
        when(permissionService.addPermissionToUser(anyLong(), anyLong(), any(PermissionEnum.class))).thenReturn(false);

        ResponseEntity<Void> response = permissionController.addPermissionToUser(1L, 2L, PermissionEnum.CAMP_IMPORT);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeletePermissionFromUser_Success() {
        when(permissionService.deletePermissionFromUser(anyLong(), anyLong(), any(PermissionEnum.class))).thenReturn(true);

        ResponseEntity<Void> response = permissionController.deletePermissionFromUser(1L, 2L, PermissionEnum.BENEF_MANAGEMENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeletePermissionFromUser_Failure() {
        when(permissionService.deletePermissionFromUser(anyLong(), anyLong(), any(PermissionEnum.class))).thenReturn(false);

        ResponseEntity<Void> response = permissionController.deletePermissionFromUser(1L, 2L, PermissionEnum.BENEF_MANAGEMENT);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
