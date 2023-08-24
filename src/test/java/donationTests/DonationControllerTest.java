package donationTests;

import de.msg.javatraining.donationmanager.controller.donation.DonationController;
import de.msg.javatraining.donationmanager.exceptions.donation.*;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import de.msg.javatraining.donationmanager.service.donorService.DonorService;
import de.msg.javatraining.donationmanager.service.userService.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class DonationControllerTest {
    @InjectMocks
    private DonationController donationController;

    @Mock
    private DonorService donorService;
    @Mock
    private UserService userService;
    @Mock
    private CampaignService campaignService;
    @Mock
    private DonationService donationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User goodUser(Long userId) {
        PermissionEnum permission = PermissionEnum.DONATION_MANAGEMENT;
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(permission);

        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId,"testUser1", "testUser1", "1234567899", "goodUser", "test1@example.com", "psswd1", true, false, 1, roles, new HashSet<>());
        return user;
    }

    private User badUser(Long userId) {
        PermissionEnum permission = PermissionEnum.CAMP_REPORTING;
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(permission);

        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId,"testuser2", "testUser2", "1234567800", "badUser", "test2@example.com", "psswd2", true, false, 1, roles, new HashSet<>());
        return user;
    }

    private Donor createDonator(Long donatorId) {
        Donor donor = new Donor("fn1", "ln1", "adn1", "mdn1");
        donor.setId(donatorId);
        return donor;
    }

    private Campaign createCampaign(Long campaignId) {
        Campaign campaign = new Campaign("c1", "p1");
        campaign.setId(campaignId);
        return campaign;
    }

    @Test
    public void testGetAllDonations() {
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);

        List<Donation> mockDonations = Arrays.asList(
                new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null),
                new Donation(2L,250, "USD", campaign, donor, user, null, "", LocalDate.now(), false, null),
                new Donation(3L,2000, "YEN", campaign, donor, user, null, "", LocalDate.now(), false, null)
        );
        when(donationService.getAllDonations()).thenReturn(mockDonations);

        List<Donation> result = donationController.getAllDonations();
        assertEquals(3, result.size());
        assertEquals("EUR", result.get(0).getCurrency());
        assertEquals(250, result.get(1).getAmount());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    public void testGetDonation() throws DonationNotFoundException {
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);

        when(donationService.getDonationById(donation.getId())).thenReturn(donation);

        ResponseEntity<?> responseEntity = donationController.getDonation(donation.getId());
        Donation result = (Donation) responseEntity.getBody();

        assertNotNull(result);
        assertEquals(result.getId(), donation.getId());
        assertEquals(result.getAmount(), donation.getAmount());
        assertEquals(result.getCurrency(), donation.getCurrency());
    }

//    @Test
//    public void testCreateDonation() throws UserPermissionException, DonationRequirementsException, DonationException {
//        User user = goodUser(1L);
//        User badUser = badUser(2L);
//        Donator donator = createDonator(1L);
//        Campaign campaign = createCampaign(1L);
//        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
//        Donation noRequirementsDonation = new Donation();
//        Donation donationException = new Donation(3L,200, "EUR", null, null, null, null, "", LocalDate.now(), false, null);
//
//        // Mock service response
//        when(donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), donation)).thenReturn(donation);
//
//        // Mock service response to throw UserPermissionException
//        when(donationService.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation))
//                .thenThrow(new UserPermissionException("User does not have permission!"));
//
//        // Mock service response to throw DonationRequirementsException
//        when(donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), noRequirementsDonation))
//                .thenThrow(new DonationRequirementsException("Requirements not met!"));
//
//        // Call the controller method
//        ResponseEntity<?> responseEntity = donationController.createDonation(user.getId(), donator.getId(), campaign.getId(), donation);
//
//        // Verify the response
//        assertNotNull(responseEntity);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Donation created successfully!", responseEntity.getBody());
//
//        // Call the controller method
//        ResponseEntity<?> permissionResponse = donationController.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation);
//
//        // Verify the response
//        assertNotNull(responseEntity);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("User does not have permission!", permissionResponse.getBody());
//
//        // Call the controller method
//        ResponseEntity<?> requirementsResponse = donationController.createDonation(user.getId(), donator.getId(), campaign.getId(), noRequirementsDonation);
//
//        // Verify the response
//        assertNotNull(responseEntity);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Requirements not met!", requirementsResponse.getBody());
//
//        // user with no permission
////        ResponseEntity<Donation> forbiddenResponse = ResponseEntity.status(HttpStatus.OK).build();
////        when(donationService.createDonation(eq(badUser.getId()), eq(donator.getId()), eq(campaign.getId()), any(Donation.class))).thenReturn(forbiddenResponse.getBody());
//
////        assertThrows(UserPermissionException.class, () -> {
////            donationController.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation);
////        });
////
////        ResponseEntity<?> badResponse = donationController.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation);
////        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
//    }


    @Test
    public void testCreateDonation() throws UserPermissionException, DonationRequirementsException, DonationException {
        Donor donor = createDonator(1L);
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);
        Donation badDonation = new Donation();

        when(donationService.createDonation(user.getId(), donor.getId(), campaign.getId(), donation)).thenReturn(donation);
        when(donationService.createDonation(eq(badUser.getId()), eq(donor.getId()), eq(campaign.getId()), eq(badDonation))).thenReturn(null);
        when(donationService.createDonation(eq(badUser.getId()), eq(donor.getId()), eq(campaign.getId()), eq(badDonation))).thenReturn(null);

        ResponseEntity<?> goodResponse = donationController.createDonation(user.getId(), donor.getId(), campaign.getId(), donation);
        assertEquals("Donation created successfully!", goodResponse.getBody());

        // user with no permission
        ResponseEntity<?> permissionResponse = donationController.createDonation(badUser.getId(), donor.getId(), campaign.getId(), donation);
        assertEquals("Donation has not been created!", permissionResponse.getBody());

        // bad donation requirements
        ResponseEntity<?> requirementsResponse = donationController.createDonation(user.getId(), donor.getId(), campaign.getId(), badDonation);
        assertEquals("Donation has not been created!", requirementsResponse.getBody());

    }

    @Test
    public void testCreateDonation_Success() throws UserPermissionException, DonationRequirementsException, DonationException {
        // Create test data
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);

        // Mock service response
        when(donationService.createDonation(user.getId(), donor.getId(), campaign.getId(), donation)).thenReturn(donation);

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.createDonation(user.getId(), donor.getId(), campaign.getId(), donation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Donation created successfully!", responseEntity.getBody());
    }

    @Test
    public void testCreateDonation_UserPermissionException() throws UserPermissionException, DonationRequirementsException, DonationException {
        // Create test data
        User user = badUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);


        // Mock service response to throw UserPermissionException
        when(donationService.createDonation(user.getId(), donor.getId(), campaign.getId(), donation))
                .thenThrow(new UserPermissionException("User does not have permission."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.createDonation(user.getId(), donor.getId(), campaign.getId(), donation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User does not have permission.", responseEntity.getBody());
    }

    @Test
    public void testCreateDonation_DonationRequirementsException() throws UserPermissionException, DonationRequirementsException, DonationException {
        // Create test data
        User user = badUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation();

        // Mock service response to throw DonationRequirementsException
        when(donationService.createDonation(user.getId(), donor.getId(), campaign.getId(), donation))
                .thenThrow(new DonationRequirementsException("Donation requirements not met."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.createDonation(user.getId(), donor.getId(), campaign.getId(), donation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Donation requirements not met.", responseEntity.getBody());
    }

    @Test
    public void testCreateDonation_DonationException() throws UserPermissionException, DonationRequirementsException, DonationException {
        // Create test data
        User user = goodUser(null);
        Donor donor = createDonator(null);
        Campaign campaign = createCampaign(null);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);

        // Mock service response to throw DonationException
        when(donationService.createDonation(user.getId(), donor.getId(), campaign.getId(), donation))
                .thenThrow(new DonationException("Donation cannot be processed."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.createDonation(user.getId(), donor.getId(), campaign.getId(), donation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Donation cannot be processed.", responseEntity.getBody());
    }

    @Test
    public void testUpdateDonation_Success() throws DonationRequirementsException,
            UserPermissionException, DonationNotFoundException, DonationApprovedException, DonationIdException {
        // Create test data
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);
        Donation newDonation = new Donation(1L,200, "USD", campaign, donor, user, null, "", LocalDate.now(), false, null);

        // Mock service response
        when(donationService.updateDonation(user.getId(), donation.getId(), newDonation)).thenReturn(donation);

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.updateDonation(user.getId(), donation.getId(), newDonation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Donation updated successfully!", responseEntity.getBody());
    }

    @Test
    public void testUpdateDonation_DonationRequirementsException() throws DonationRequirementsException,
            DonationIdException, UserPermissionException, DonationNotFoundException, DonationApprovedException {
        // Create test data
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);
        Donation newDonation = new Donation();


        // Mock service response to throw DonationRequirementsException
        when(donationService.updateDonation(user.getId(), donation.getId(), newDonation))
                .thenThrow(new DonationRequirementsException("Donation requirements not met."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.updateDonation(user.getId(), donation.getId(), newDonation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Donation requirements not met.", responseEntity.getBody());
    }

    @Test
    public void testUpdateDonation_DonationIdException() throws DonationRequirementsException,
            DonationIdException, UserPermissionException, DonationNotFoundException, DonationApprovedException {
        // Create test data
        Long userId = 1L;
        Long donationId = null; // Invalid donationId
        Donation newDonation = new Donation(/* Initialize your new Donation object here */);

        // Mock service response to throw DonationIdException
        when(donationService.updateDonation(userId, donationId, newDonation))
                .thenThrow(new DonationIdException("Invalid donation ID."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.updateDonation(userId, donationId, newDonation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Invalid donation ID.", responseEntity.getBody());
    }

    @Test
    public void testUpdateDonation_UserPermissionException() throws DonationRequirementsException,
            DonationIdException, UserPermissionException, DonationNotFoundException, DonationApprovedException {
        // Create test data
        Long userId = 1L;
        Long donationId = 2L;
        Donation newDonation = new Donation(/* Initialize your new Donation object here */);

        // Mock service response to throw UserPermissionException
        when(donationService.updateDonation(userId, donationId, newDonation))
                .thenThrow(new UserPermissionException("User does not have permission."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.updateDonation(userId, donationId, newDonation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User does not have permission.", responseEntity.getBody());
    }

    @Test
    public void testUpdateDonation_DonationNotFoundException() throws DonationRequirementsException,
            DonationIdException, UserPermissionException, DonationNotFoundException, DonationApprovedException {
        // Create test data
        Long userId = 1L;
        Long donationId = 2L;
        Donation newDonation = new Donation(/* Initialize your new Donation object here */);

        // Mock service response to throw DonationNotFoundException
        when(donationService.updateDonation(userId, donationId, newDonation))
                .thenThrow(new DonationNotFoundException("Donation not found."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.updateDonation(userId, donationId, newDonation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Donation not found.", responseEntity.getBody());
    }

    @Test
    public void testUpdateDonation_DonationApprovedException() throws DonationRequirementsException,
            DonationIdException, UserPermissionException, DonationNotFoundException, DonationApprovedException {
        // Create test data
        Long userId = 1L;
        Long donationId = 2L;
        Donation newDonation = new Donation(/* Initialize your new Donation object here */);

        // Mock service response to throw DonationApprovedException
        when(donationService.updateDonation(userId, donationId, newDonation))
                .thenThrow(new DonationApprovedException("Donation is already approved."));

        // Call the controller method
        ResponseEntity<?> responseEntity = donationController.updateDonation(userId, donationId, newDonation);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Donation is already approved.", responseEntity.getBody());
    }

//    @Test
//    public void testUpdateDonation() {
//        User user = goodUser(1L);
//        User badUser = badUser(2L);
//        Donator donator = createDonator(1L);
//        Campaign campaign = createCampaign(1L);
//        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
//        Donation updatedDonation = new Donation(1L,200, "USD", campaign, donator, user, null, "", LocalDate.now(), false, null);
//
//        when(donationService.updateDonation(eq(user.getId()), eq(donation.getId()), any(Donation.class))).thenReturn(donation);
//
//        ResponseEntity<?> goodResponse = donationController.updateDonation(user.getId(), donation.getId(), updatedDonation);
//        assertNotNull(goodResponse);
//        assertEquals(HttpStatus.OK, goodResponse.getStatusCode());
//
//        // user with no permission
//        ResponseEntity<Donation> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        when(donationService.updateDonation(eq(badUser.getId()), eq(donation.getId()), any(Donation.class))).thenReturn(forbiddenResponse.getBody());
//
//        ResponseEntity<?> badResponse = donationController.updateDonation(badUser.getId(), donation.getId(), updatedDonation);
//        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
//    }
//
//    @Test
//    public void testDeleteDonationById() throws DonationNotFoundException {
//        User user = goodUser(1L);
//        Donator donator = createDonator(1L);
//        Campaign campaign = createCampaign(1L);
//        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
//        Donation approvedDonation = new Donation(2L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), true, null);
//
//        when(donationService.getDonationById(1L)).thenReturn(Optional.of(donation));
//        when(donationService.getDonationById(2L)).thenReturn(Optional.of(approvedDonation));
//
//        ResponseEntity<?> response = donationController.deleteDonationById(user.getId(), donation.getId());
//
//        // everything works just fine
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        // donation has been approved, can't be deleted
//        ResponseEntity<?> approvedResponse = donationController.deleteDonationById(user.getId(), approvedDonation.getId());
//        assertEquals(HttpStatus.FORBIDDEN, approvedResponse.getStatusCode());
//
//        // donation does not exist
//        ResponseEntity<?> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        ResponseEntity<?> badResponse = donationController.deleteDonationById(user.getId(), 25L);
//        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
//    }
}
