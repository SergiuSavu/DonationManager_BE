package donatorTests;

import de.msg.javatraining.donationmanager.controller.donator.DonatorController;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.DonatorRepository;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import de.msg.javatraining.donationmanager.service.donatorService.DonatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonatorControllerTest {
    @InjectMocks
    private DonatorController donatorController;

    @Mock
    private DonatorService donatorService;

    @Mock
    private DonationService donationService;

    private User goodUser(Long userId) {
        PermissionEnum permission = PermissionEnum.BENEF_MANAGEMENT;
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

    private Donator createGoodDonator(Long donatorId) {
        Donator donator = new Donator("gfn1", "gln1", "gadn1", "gmdn1");
        donator.setId(donatorId);
        return donator;
    }

    private Donator createBadDonator() {
        Donator donator = new Donator("bfn1", "bln1", "badn1", "bmdn1");
        donator.setId(null);
        return donator;
    }


    @Test
    public void testGetAllDonators() {
        List<Donator> mockDonators = Arrays.asList(
                new Donator("fn1", "ln1", "adn1", "mdn1"),
                new Donator("fn2", "ln2", "adn2", "mdn2"),
                new Donator("fn3", "ln3", "adn3", "mdn3"),
                new Donator("fn4", "ln4", "adn4", "mdn4")
        );
        when(donatorService.getAllDonators()).thenReturn(mockDonators);

        List<Donator> result = donatorController.getAllDonators();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("fn1", result.get(0).getFirstName());
        assertEquals("ln2", result.get(1).getLastName());
        assertEquals("adn3", result.get(2).getAdditionalName());
        assertEquals("mdn4", result.get(3).getMaidenName());
    }

    @Test
    public void testGetDonator() {
        Donator donator = createGoodDonator(1L);

        when(donatorService.getDonatorById(donator.getId())).thenReturn(Optional.of(donator));

        Optional<Donator> resultDonator = donatorController.getDonator(donator.getId());

        assertNotNull(resultDonator);
        assertEquals(resultDonator.get().getId(), donator.getId());
        assertEquals(resultDonator.get().getFirstName(), donator.getFirstName());
        assertEquals(resultDonator.get().getLastName(), donator.getLastName());
        assertEquals(resultDonator.get().getAdditionalName(), donator.getAdditionalName());
        assertEquals(resultDonator.get().getMaidenName(), donator.getMaidenName());
    }


    @Test
    public void testCreateDonator() {
        Donator donator = createGoodDonator(1L);
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);

        when(donatorService.createDonator(eq(goodUser.getId()), any(Donator.class))).thenReturn(donator);

        ResponseEntity<?> goodResponse = donatorController.createDonator(goodUser.getId(), donator);
        assertNotNull(goodResponse);
        assertEquals(HttpStatus.OK, goodResponse.getStatusCode());

        // user with no permission
        ResponseEntity<Donator> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        when(donatorService.createDonator(eq(badUser.getId()), any(Donator.class))).thenReturn(forbiddenResponse.getBody());

        ResponseEntity<?> badResponse = donatorController.createDonator(badUser.getId(), donator);
        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
    }

    @Test
    public void testUpdateDonator() {
        Donator donator = createGoodDonator(1L);
        Donator updatedDonator = new Donator("ugfn1", "ugln1", "ugadn1", "ugmdn1");
        User goodUser = goodUser(2L);
        User badUser = badUser(3L);

        when(donatorService.updateDonator(eq(goodUser.getId()), eq(donator.getId()), any(Donator.class))).thenReturn(donator);

        ResponseEntity<?> goodResponse = donatorController.updateDonator(goodUser.getId(), donator.getId(), updatedDonator);
        assertNotNull(goodResponse);
        assertEquals(HttpStatus.OK, goodResponse.getStatusCode());

        // user with no permission
        ResponseEntity<Donator> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        when(donatorService.updateDonator(eq(badUser.getId()), eq(donator.getId()), any(Donator.class))).thenReturn(forbiddenResponse.getBody());

        ResponseEntity<?> badResponse = donatorController.updateDonator(badUser.getId(), donator.getId(), updatedDonator);
        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
    }

    @Test
    public void deleteDonatorById() {
        User goodUser = goodUser(1L);
        Donator donationsDonator = createGoodDonator(3L);
        Donator noDonationsdonator = createGoodDonator(4L);

        when(donationService.findDonationsByDonatorId(3L)).thenReturn(true);
        when(donatorService.getDonatorById(3L)).thenReturn(Optional.of(donationsDonator));

        when(donationService.findDonationsByDonatorId(4L)).thenReturn(false);
        when(donatorService.getDonatorById(4L)).thenReturn(Optional.of(noDonationsdonator));

        ResponseEntity<?> donationsResponse = donatorController.deleteDonatorById(goodUser.getId(), donationsDonator.getId());

        // donator has donations
        assertNotNull(donationsResponse);
        assertEquals(HttpStatus.OK, donationsResponse.getStatusCode());
        assertEquals(donationsResponse.getBody(), "Donator values set to UNKNOWN");

        // donator does not have donations
        ResponseEntity<?> noDonationsResponse = donatorController.deleteDonatorById(goodUser.getId(), noDonationsdonator.getId());
        assertNotNull(donationsResponse);
        assertEquals(HttpStatus.OK, donationsResponse.getStatusCode());
        assertEquals(noDonationsResponse.getBody(),"Donator has been deleted");

        // donator with id not found
        ResponseEntity<Donator> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        ResponseEntity<?> badResponse = donatorController.deleteDonatorById(goodUser.getId(), 25L);
        assertEquals(HttpStatus.FORBIDDEN, badResponse.getStatusCode());
        assertEquals(badResponse.getBody(),"Donator with given id does not exist");
    }

}
