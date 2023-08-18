package donationTests;

import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonationRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonatorRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.DonatorRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.donatorService.DonatorService;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DonationServiceTest {
    @InjectMocks
    private DonationService donationService;

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private DonatorRepository donatorRepository;
    @Mock
    private UserRepository userRepository;

    private final PermissionEnum permission = PermissionEnum.DONATION_MANAGEMENT;

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

        List<Donation> donations = new ArrayList<>(
                Arrays.asList(
                        new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null),
                        new Donation(2L,300, "USD", campaign, donator, user, null, "", LocalDate.now(), false, null)
                )
        );

        when(donationRepository.findAll()).thenReturn(donations);

        List<Donation> donationList = donationRepository.findAll();
        assertNotNull(donationList);
        assertFalse(donationList.isEmpty());
        assertEquals(2, donationList.size());
        assertEquals("EUR", donationList.get(0).getCurrency());
        assertEquals(300, donationList.get(1).getAmount());
    }

    @Test
    public void testGetDonationById() {
        User user = goodUser(1L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);

        when(donationRepository.findById(eq(user.getId()))).thenReturn(Optional.of(donation));

        Optional<Donation> optDonation = donationService.getDonationById(1L);
        assertNotNull(optDonation);
        optDonation.ifPresent(value -> assertEquals(1L, value.getId()));
        optDonation.ifPresent(value -> assertEquals(200, value.getAmount()));
        optDonation.ifPresent(value -> assertEquals("EUR", value.getCurrency()));
    }

    @Test
    public void testCreateDonation() {
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);


        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(donatorRepository.findById(eq(donator.getId()))).thenReturn(Optional.of(donator));
        when(campaignRepository.findById(eq(campaign.getId()))).thenReturn(Optional.of(campaign));
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation createdDonation = donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), donation);

        // works
        assertNotNull(createdDonation);

        // donation requirements not met
        assertThrows(IllegalStateException.class, () -> {
            donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), new Donation());
        });

        // user without permission
        assertThrows(IllegalStateException.class, () -> {
            donationService.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation);
        });
    }

    @Test
    public void testUpdateDonation() {
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation updatedDonation = new Donation(donation.getId(),550, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation updatedDonation2 = new Donation(donation.getId(), 0, null, null, null, null, null, null, null, false, null);
        Donation nullDonation = new Donation(null,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(donationRepository.save(donation)).thenReturn(donation);
        //when(donationService.checkDonationStatus(1L)).thenReturn(donation.isApproved());

        // everything works
        assertEquals(donationService.updateDonation(1L, donation.getId(), updatedDonation), donation);

        // can't modify an approved donation
        donation.setApproved(true);
        assertThrows(IllegalArgumentException.class, () -> {
           donationService.updateDonation(1L, donation.getId(), updatedDonation);
        });

        // updatedDonation requirements not met
        assertThrows(IllegalArgumentException.class, () -> {
            donationService.updateDonation(1L, donation.getId(), updatedDonation2);
        });

        // no permission for user
        assertThrows(IllegalArgumentException.class, () -> {
            donationService.updateDonation(badUser.getId(), donation.getId(), updatedDonation);
        });

        // donation id null
        assertThrows(IllegalArgumentException.class, () -> {
            donationService.updateDonation(user.getId(), nullDonation.getId(), updatedDonation);
        });

        // donation does not exist
        assertThrows(IllegalArgumentException.class, () -> {
            donationService.updateDonation(user.getId(), 25L, updatedDonation);
        });
    }

    @Test
    public void testDeleteDonationById() {
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donator donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation nullDonation = new Donation(null,200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);



        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(donationRepository).deleteById(1L);

        Donation deletedDonation = donationService.deleteDonationById(user.getId(), donation.getId());
        verify(donationRepository).deleteById(1L);

        // user does not have permission
        assertThrows(IllegalArgumentException.class, () -> {
            donationService.deleteDonationById(badUser.getId(), donation.getId());
        });

        // donation does not exist
        assertThrows(EntityNotFoundException.class, () -> {
            donationService.deleteDonationById(user.getId(), 25L);
        });

        // donation id can't be null
        assertThrows(IllegalArgumentException.class, () -> {
            donationService.deleteDonationById(badUser.getId(), nullDonation.getId());
        });

        // can't delete an approved donation
        donation.setApproved(true);
        assertThrows(IllegalArgumentException.class, () -> {
            donationService.deleteDonationById(user.getId(), donation.getId());
        });
    }
}