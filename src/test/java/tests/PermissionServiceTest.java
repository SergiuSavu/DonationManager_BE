package tests;

import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

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

    private User createUserWithRoleAndPermission2(Long userId) {
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.BENEF_MANAGEMENT);
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_MGN, permissionEnums);
        roles.add(role);

        User user = new User(userId,"testuser", "test", "1234567890", "something", "test@example.com", "psswd", true, false, 1, roles, new HashSet<>());
        return user;
    }

    @Test
    void testAddPermissionToUser_InvalidInput() {
        // Invalid user IDs and permission
        boolean result = permissionService.addPermissionToUser(null, 2L, PermissionEnum.CAMP_IMPORT);
        assertFalse(result);

        result = permissionService.addPermissionToUser(1L, null, PermissionEnum.CAMP_IMPORT);
        assertFalse(result);

        result = permissionService.addPermissionToUser(1L, 2L, null);
        assertFalse(result);
    }

    @Test
    void testAddPermissionToUser_UserNotFound() {
        // Mock userRepository.findById to return empty Optional for user
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean result = permissionService.addPermissionToUser(1L, 2L, PermissionEnum.CAMP_IMPORT);
        assertFalse(result);
    }


    @Test
    void testDeletePermissionFromUser_InvalidInput() {
        // Invalid user IDs and permission
        boolean result = permissionService.deletePermissionFromUser(null, 2L, PermissionEnum.BENEF_MANAGEMENT);
        assertFalse(result);

        result = permissionService.deletePermissionFromUser(1L, null, PermissionEnum.BENEF_MANAGEMENT);
        assertFalse(result);

        result = permissionService.deletePermissionFromUser(1L, 2L, null);
        assertFalse(result);
    }

    @Test
    void testDeletePermissionFromUser_UserNotFound() {
        // Mock userRepository.findById to return empty Optional for user
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean result = permissionService.deletePermissionFromUser(1L, 2L, PermissionEnum.BENEF_MANAGEMENT);
        assertFalse(result);
    }

    @Test
    void testAddPermissionToUser() {
        // Create a user with the necessary role and permissions
        User user = createUserWithRoleAndPermission1(1L);
        User user2 = createUserWithRoleAndPermission2(2L); // The user whose permissions we want to change

        // Mock the behavior of userRepository.findById for both adminUser and targetUser
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        boolean result = permissionService.addPermissionToUser(user.getId(), user2.getId(),PermissionEnum.CAMP_IMPORT);
        assertTrue(result);

        boolean result2 = permissionService.addPermissionToUser(user.getId(), user2.getId(),PermissionEnum.CAMP_REPORT_RESTRICTED);
        assertFalse(result2);
    }

    @Test
    void testDeletePermissionFromUser() {
        // Create a user with the necessary role and permissions
        User user = createUserWithRoleAndPermission1(1L);
        User user2 = createUserWithRoleAndPermission2(2L); // The user whose permissions we want to change

        // Mock the behavior of userRepository.findById for both adminUser and targetUser
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        boolean result = permissionService.deletePermissionFromUser(1L, 2L, PermissionEnum.BENEF_MANAGEMENT);
        assertTrue(result);

        boolean result2 = permissionService.deletePermissionFromUser(1L, 2L, PermissionEnum.BENEF_MANAGEMENT);
        assertFalse(result2);
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
