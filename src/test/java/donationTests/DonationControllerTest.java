package donationTests;

import de.msg.javatraining.donationmanager.controller.donation.DonationController;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import de.msg.javatraining.donationmanager.service.donatorService.DonatorService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DonationControllerTest {
    @InjectMocks
    private DonationController donationController;

    @Mock
    private DonatorService donatorService;
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

    private Donator createDonator(Long donatorId) {
        Donator donator = new Donator("fn1", "ln1", "adn1", "mdn1");
        donator.setId(donatorId);
        return donator;
    }

    private Campaign createCampaign(Long campaignId) {
        Campaign campaign = new Campaign("c1", "p1");
        campaign.setId(campaignId);
        return campaign;
    }

    @Test
    public void testGetAllDonations() {
        User user = goodUser(1L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);

        List<Donation> mockDonations = Arrays.asList(
                new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null),
                new Donation(2L,250, "USD", campaign, donator, user, null, "", LocalDate.now(), false, null),
                new Donation(3L,2000, "YEN", campaign, donator, user, null, "", LocalDate.now(), false, null)
        );
        when(donationService.getAllDonations()).thenReturn(mockDonations);

        List<Donation> result = donationController.getAllDonations();
        assertEquals(3, result.size());
        assertEquals("EUR", result.get(0).getCurrency());
        assertEquals(250, result.get(1).getAmount());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    public void testGetDonation() {
        User user = goodUser(1L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);

        when(donationService.getDonationById(donation.getId())).thenReturn(Optional.of(donation));

        Optional<Donation> result = donationController.getDonation(donation.getId());

        assertNotNull(result);
        assertEquals(result.get().getId(), donation.getId());
        assertEquals(result.get().getAmount(), donation.getAmount());
        assertEquals(result.get().getCurrency(), donation.getCurrency());
    }

    @Test
    public void testCreateDonation() {
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);

        when(donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), donation)).thenReturn(donation);

        ResponseEntity<?> goodResponse = donationController.createDonation(user.getId(), donator.getId(), campaign.getId(), donation);
        assertNotNull(goodResponse);
        assertEquals(HttpStatus.OK, goodResponse.getStatusCode());

        // user with no permission
        ResponseEntity<Donation> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        when(donationService.createDonation(eq(badUser.getId()), eq(donator.getId()), eq(campaign.getId()), any(Donation.class))).thenReturn(forbiddenResponse.getBody());

        ResponseEntity<?> badResponse = donationController.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation);
        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
    }

    @Test
    public void testUpdateDonation() {
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation updatedDonation = new Donation(1L,200, "USD", campaign, donator, user, null, "", LocalDate.now(), false, null);

        when(donationService.updateDonation(eq(user.getId()), eq(donation.getId()), any(Donation.class))).thenReturn(donation);

        ResponseEntity<?> goodResponse = donationController.updateDonation(user.getId(), donation.getId(), updatedDonation);
        assertNotNull(goodResponse);
        assertEquals(HttpStatus.OK, goodResponse.getStatusCode());

        // user with no permission
        ResponseEntity<Donation> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        when(donationService.updateDonation(eq(badUser.getId()), eq(donation.getId()), any(Donation.class))).thenReturn(forbiddenResponse.getBody());

        ResponseEntity<?> badResponse = donationController.updateDonation(badUser.getId(), donation.getId(), updatedDonation);
        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
    }

    @Test
    public void testDeleteDonationById() {
        User user = goodUser(1L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation approvedDonation = new Donation(2L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), true, null);

        when(donationService.getDonationById(1L)).thenReturn(Optional.of(donation));
        when(donationService.getDonationById(2L)).thenReturn(Optional.of(approvedDonation));

        ResponseEntity<?> response = donationController.deleteDonationById(user.getId(), donation.getId());

        // everything works just fine
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // donation has been approved, can't be deleted
        ResponseEntity<?> approvedResponse = donationController.deleteDonationById(user.getId(), approvedDonation.getId());
        assertEquals(HttpStatus.FORBIDDEN, approvedResponse.getStatusCode());

        // donation does not exist
        ResponseEntity<?> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        ResponseEntity<?> badResponse = donationController.deleteDonationById(user.getId(), 25L);
        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
    }
}
