import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.service.roleService.RoleService;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRoleWithPermissions() {
        String roleName = "ROLE_CEN";
        Set<Integer> permissionIds = new HashSet<>();
        permissionIds.add(1);
        permissionIds.add(2);

        RoleService roleService = new RoleService();
        Role role = roleService.createRoleWithPermissions(roleName, permissionIds);

        assertNotNull(role);
        assertTrue(role.getPermissions().contains(PermissionEnum.getById(1)));
        assertTrue(role.getPermissions().contains(PermissionEnum.getById(2)));
    }

    @Test
    public void testUpdateRoleWithPermissions() {
        long roleId = 1L;
        Set<Integer> permissionIds = new HashSet<>();
        permissionIds.add(3);
        permissionIds.add(4);

        Role role = new Role();
        role.setId(Math.toIntExact(roleId));

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        Role updatedRole = roleService.updateRoleWithPermissions((int) roleId, permissionIds);

        assertNotNull(updatedRole);
        assertTrue(updatedRole.getPermissions().contains(PermissionEnum.getById(3)));
        assertTrue(updatedRole.getPermissions().contains(PermissionEnum.getById(4)));
    }

    @Test
    public void testUpdateRoleWithPermissionsRoleNotFound() {
        long roleId = 1L;
        Set<Integer> permissionIds = new HashSet<>();
        permissionIds.add(3);
        permissionIds.add(4);

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        Role updatedRole = roleService.updateRoleWithPermissions((int) roleId, permissionIds);
        assertNull(updatedRole);
    }

    @Test
    public void testDeleteRoleWithPermissionsRoleFound() {
        long roleId = 1L;
        Role role = new Role();
        role.setId(Math.toIntExact(roleId));

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        boolean deleted = roleService.deleteRoleWithPermissions((int) roleId);
        assertTrue(deleted);

        verify(roleRepository).delete(role);
    }

    @Test
    public void testDeleteRoleWithPermissionsRoleNotFound() {
        long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        boolean deleted = roleService.deleteRoleWithPermissions((int) roleId);
        assertFalse(deleted);
        verify(roleRepository, never()).delete(any(Role.class));
    }
}
