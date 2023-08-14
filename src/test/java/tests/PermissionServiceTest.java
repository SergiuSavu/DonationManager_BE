package tests;

import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class) // This annotation initializes the Mockito extensions for JUnit 5
class PermissionServiceTest {
    @InjectMocks
    private PermissionService permissionService;
    @Mock
    private  RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;

    //@Mock
    //private PermissionRepository permissionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUserWithRoleAndPermission1(Long userId) {
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.PERMISSION_MANAGEMENT);
        permissionEnums.add(PermissionEnum.USER_MANAGEMENT);
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId,"testuser", "test", "1234567890", "something", "test@example.com", "psswd", true, false, 1, roles, new HashSet<>());
        return user;
    }

    @Test
    void testAddPermissionToRole() {
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.PERMISSION_MANAGEMENT);
        permissionEnums.add(PermissionEnum.USER_MANAGEMENT);

        // Test case: Permission already exists
        PermissionEnum existingPermission = PermissionEnum.USER_MANAGEMENT;
        assertThrows(IllegalArgumentException.class, () -> {
            permissionService.addPermissionToRole(1L, new Role(1, ERole.ROLE_ADM, permissionEnums), existingPermission);
        });

        // Test case: Permission to add is null
        assertThrows(IllegalArgumentException.class, () -> {
            permissionService.addPermissionToRole(1L, new Role(1, ERole.ROLE_ADM, permissionEnums), null);
        });

        // Create a user with the necessary role and permissions
        PermissionEnum expected = PermissionEnum.BENEF_MANAGEMENT;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUserWithRoleAndPermission1(1L)));
        PermissionEnum p = permissionService.addPermissionToRole(1L, new Role(1, ERole.ROLE_ADM, permissionEnums), PermissionEnum.BENEF_MANAGEMENT);

        assertEquals(expected, p);

    }
    @Test
    void testDeletePermissionFromRole() {
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.USER_MANAGEMENT);

        // Mock the behavior of userRepository.findById to return the user you want to use
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUserWithRoleAndPermission1(1L)));

        // Test case: Permission to delete is null
        assertThrows(IllegalArgumentException.class, () -> {
            permissionService.deletePermissionFromRole(1L, new Role(1, ERole.ROLE_ADM, permissionEnums), null);
        });

        // Test case: User not found or permission not available
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            permissionService.deletePermissionFromRole(1L, new Role(1, ERole.ROLE_ADM, permissionEnums), PermissionEnum.CAMP_IMPORT);
        });

        // Test case: Permission to delete does not exist
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUserWithRoleAndPermission1(1L)));
        assertThrows(IllegalArgumentException.class, () -> {
            permissionService.deletePermissionFromRole(1L, new Role(1, ERole.ROLE_ADM, permissionEnums), PermissionEnum.CAMP_IMPORT);
        });

        // Test case: Successful deletion
        User user = createUserWithRoleAndPermission1(1L);
        PermissionEnum expected = PermissionEnum.BENEF_MANAGEMENT;

        // Mock the behavior of userRepository.findById for the user
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        permissionEnums.add(PermissionEnum.BENEF_MANAGEMENT);
        PermissionEnum p = permissionService.deletePermissionFromRole(user.getId(), new Role(1, ERole.ROLE_ADM, permissionEnums), PermissionEnum.BENEF_MANAGEMENT);
        assertEquals(expected, p);
    }

    @Test
    void testHasPermission() {
        // Create a user with a role containing the permission
        User user = createUserWithRoleAndPermission1(1L);

        boolean hasPermission = permissionService.hasPermission(user, PermissionEnum.PERMISSION_MANAGEMENT);
        assertTrue(hasPermission);

        boolean hasNoPermission = permissionService.hasPermission(user, PermissionEnum.CAMP_REPORT_RESTRICTED);
        assertFalse(hasNoPermission);
    }

    @Test
    void testGetRoles() {
        // Create roles with the necessary permissions
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.PERMISSION_MANAGEMENT);
        permissionEnums.add(PermissionEnum.USER_MANAGEMENT);

        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);

        // Create a user and add the role
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setMobileNumber("1234567890");
        user.setPassword("testpassword");
        user.setRoles(new HashSet<>(Collections.singletonList(role))); // Add the role

        // Mock the behavior of userRepository.findById
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Role> roles = permissionService.getRoles(1L);
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals(user.getRoles(), roles);
    }
}
