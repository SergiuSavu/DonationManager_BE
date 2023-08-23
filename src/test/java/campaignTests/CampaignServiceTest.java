package campaignTests;

import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    @InjectMocks
    private CampaignService campaignService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CampaignRepository campaignRepository;

    private User createUserWithRoleAndPermission1(Long userId, PermissionEnum perm) {
        Set<PermissionEnum> permissionEnums = new HashSet<>();

        permissionEnums.add(perm);
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId, "testuser", "test", "1234567890", "something", "test@example.com", "psswd", true, false, 1, roles, new HashSet<>());
        return user;
    }

    @Test
    public void testCreateCampaign() {
        Long userId = 1L;
        String campaignName = "Test Campaign";
        String campaignPurpose = "Testing purposes";

        // Create a user with the required permission
        User userWithPermission = createUserWithRoleAndPermission1(userId, PermissionEnum.CAMP_MANAGEMENT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithPermission));

        // Mock the campaignRepository behavior
        when(campaignRepository.findCampaignByName(campaignName)).thenReturn(null);

        ResponseEntity<?> response = campaignService.createCampaign(userId, campaignName, campaignPurpose);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Campaign);
    }

    @Test
    public void testCreateCampaignWithNoPermission() {
        Long userId = 1L;
        String campaignName = "Test Campaign";
        String campaignPurpose = "Testing purposes";

        // Create a user without the required permission
        User userWithoutPermission = createUserWithRoleAndPermission1(userId, PermissionEnum.USER_MANAGEMENT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutPermission));

        ResponseEntity<?> response = campaignService.createCampaign(userId, campaignName, campaignPurpose);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof String); // Change to the expected response type

        String errorMessage = (String) response.getBody();
        assertEquals("User does not have the required permission/s!", errorMessage);
    }

    @Test
    public void testUpdateCampaign() {
        Long userId = 1L;
        Long campaignId = 123L;
        String newName = "Updated Campaign";
        String newPurpose = "Updated purposes";

        // Create a user with the required permission
        User userWithPermission = createUserWithRoleAndPermission1(userId, PermissionEnum.CAMP_MANAGEMENT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithPermission));

        // Mock the campaignRepository behavior
        Campaign existingCampaign = new Campaign("Test Campaign", "Testing purposes");
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(existingCampaign));
        when(campaignRepository.findCampaignByName(newName)).thenReturn(null);

        ResponseEntity<?> response = campaignService.updateCampaign(userId, campaignId, newName, newPurpose);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Campaign);

        Campaign updatedCampaign = (Campaign) response.getBody();
        assertEquals(newName, updatedCampaign.getName());
        assertEquals(newPurpose, updatedCampaign.getPurpose());
    }

    @Test
    public void testUpdateCampaignWthNoPermission() {
        Long userId = 1L;
        Long campaignId = 123L;
        String newName = "Updated Campaign";
        String newPurpose = "Updated purposes";

        // Create a user without the required permission
        User userWithoutPermission = createUserWithRoleAndPermission1(userId, PermissionEnum.USER_MANAGEMENT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutPermission));

        ResponseEntity<?> response = campaignService.updateCampaign(userId, campaignId, newName, newPurpose);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);

        String errorMessage = (String) response.getBody();
        assertEquals("User does not have the required permission/s!", errorMessage);
    }

    @Test
    public void testDeleteCampaignById() {
        Long userId = 1L;
        Long campaignId = 123L;
        String newName = "Test Campaign";
        String newPurpose = "Testing purposes";

        // Create a user with the required permission
        User userWithPermission = createUserWithRoleAndPermission1(userId, PermissionEnum.CAMP_MANAGEMENT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithPermission));

        // Mock the campaignRepository behavior
        Campaign existingCampaign = new Campaign("Test Campaign", "Testing purposes");
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(existingCampaign));

        ResponseEntity<?> response = campaignService.deleteCampaignById(userId, campaignId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Optional[Campaign(id=null, name="+newName+", purpose="+newPurpose+")]", response.getBody().toString());
    }

    @Test
    public void testDeleteCampaignByIdWithNoPermission(){
        Long userId = 1L;
        Long campaignId = 123L;

        // Create a user without the required permission
        User userWithoutPermission = createUserWithRoleAndPermission1(userId, PermissionEnum.USER_MANAGEMENT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutPermission));

        ResponseEntity<?> response = campaignService.deleteCampaignById(userId, campaignId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);

        String errorMessage = (String) response.getBody();
        assertEquals("User does not have the required permission/s!", errorMessage);
    }


}