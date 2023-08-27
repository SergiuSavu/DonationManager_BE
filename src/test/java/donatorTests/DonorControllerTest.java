package donatorTests;

import de.msg.javatraining.donationmanager.controller.donor.DonorController;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import de.msg.javatraining.donationmanager.service.donorService.DonorService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonorControllerTest {
    @InjectMocks
    private DonorController donorController;

    @Mock
    private DonorService donorService;

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

    private Donor createGoodDonator(Long donatorId) {
        Donor donor = new Donor("gfn1", "gln1", "gadn1", "gmdn1");
        donor.setId(donatorId);
        return donor;
    }

    private Donor createBadDonator() {
        Donor donor = new Donor("bfn1", "bln1", "badn1", "bmdn1");
        donor.setId(null);
        return donor;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllDonators() {
        List<Donor> mockDonors = Arrays.asList(
                new Donor("fn1", "ln1", "adn1", "mdn1"),
                new Donor("fn2", "ln2", "adn2", "mdn2"),
                new Donor("fn3", "ln3", "adn3", "mdn3"),
                new Donor("fn4", "ln4", "adn4", "mdn4")
        );
        when(donorService.getAllDonators()).thenReturn(mockDonors);

        List<Donor> result = donorController.getAllDonators();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("fn1", result.get(0).getFirstName());
        assertEquals("ln2", result.get(1).getLastName());
        assertEquals("adn3", result.get(2).getAdditionalName());
        assertEquals("mdn4", result.get(3).getMaidenName());
    }

    @Test
    public void testGetDonator() throws DonatorNotFoundException {
        Donor donor = createGoodDonator(1L);

        when(donorService.getDonatorById(donor.getId())).thenReturn(donor);

        ResponseEntity<?> resultDonator = donorController.getDonator(donor.getId());
        Donor result = (Donor) resultDonator.getBody();

        assertNotNull(resultDonator);
        assertEquals(result.getId(), donor.getId());
        assertEquals(result.getFirstName(), donor.getFirstName());
        assertEquals(result.getLastName(), donor.getLastName());
        assertEquals(result.getAdditionalName(), donor.getAdditionalName());
        assertEquals(result.getMaidenName(), donor.getMaidenName());
    }

//    @Test
//    public void testGetDonator_Success() throws DonatorNotFoundException {
//        // Create a sample Donator object
//        Donator donator = createGoodDonator(1L);
//
//        // Mock the behavior of donatorService.getDonatorById to return null (or any value since we're creating the ResponseEntity ourselves)
//        // when(donatorService.getDonatorById(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK)); -- nu da eroare, da nu cred ca este bine
//
//        when(donatorService.getDonatorById(1L)).thenReturn(donator);
//
//        // Create a ResponseEntity with your Donator
//        ResponseEntity<?> responseEntity = donatorController.getDonator(1L);
//
//        // Assert that the response is as expected
////        assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
////        assertEquals(responseEntity.getBody(), result.getBody());
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(donator, responseEntity.getBody());
//    }


//    @Test
//    public void testCreateDonator() throws UserPermissionException, DonatorRequirementsException {
//        Donator donator = createGoodDonator(1L);
//        Donator badDonator = new Donator();
//        User goodUser = goodUser(1L);
//        User badUser = badUser(2L);
//
//        when(donatorService.createDonator(eq(goodUser.getId()), any(Donator.class))).thenReturn(donator);
//        when(donatorService.createDonator(eq(badUser.getId()), any(Donator.class))).thenReturn(null);
//        when(donatorService.createDonator(goodUser.getId(), badDonator)).thenReturn(null);
//
//        ResponseEntity<?> goodResponse = donatorController.createDonator(goodUser.getId(), donator);
//        assertEquals("Donator created successfully!", goodResponse.getBody());
//
//        // user with no permission
//        ResponseEntity<?> permissionResponse = donatorController.createDonator(badUser.getId(), donator);
//        assertEquals("Donator has not been created!", permissionResponse.getBody());
//
//        // user with no permission
//        ResponseEntity<?> requirementsResponse = donatorController.createDonator(goodUser.getId(), badDonator);
//        assertEquals("Donator has not been created!", requirementsResponse.getBody());
//    }
@Test
public void testCreateDonator_Success() throws UserPermissionException, DonatorRequirementsException {
    Donor donor = createGoodDonator(1L);
    User goodUser = goodUser(1L);

    when(donorService.createDonator(eq(goodUser.getId()), any(Donor.class))).thenReturn(donor);

    ResponseEntity<?> response = donorController.createDonator(goodUser.getId(), donor);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Donator created successfully!", response.getBody());
}

    @Test
    public void testCreateDonator_NoPermission() throws UserPermissionException, DonatorRequirementsException {
        Donor donor = createGoodDonator(1L);
        User badUser = badUser(2L);

        when(donorService.createDonator(badUser.getId(), donor)).thenThrow(new UserPermissionException());

        ResponseEntity<?> response = donorController.createDonator(badUser.getId(), donor);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Use a proper HTTP status code for errors
        assertTrue(response.getBody() instanceof Map); // Check that the response body is a JSON object

        // You can then extract the error message from the JSON and check it
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertTrue(responseBody.containsKey("error"));
        assertEquals("User does not have permission", responseBody.get("error"));
    }


//    @Test
//    public void testCreateDonator_NoPermission() throws UserPermissionException, DonatorRequirementsException {
//        Donator donator = createGoodDonator(1L);
//        User badUser = badUser(2L);
//
//        //when(donatorService.createDonator(eq(badUser.getId()), any(Donator.class))).thenReturn(null);
//        when(donatorService.createDonator(badUser.getId(), donator)).thenThrow(new UserPermissionException());
//
//        assertThrows(UserPermissionException.class, () -> {
//            donatorService.createDonator(badUser.getId(), donator);
//        });
//
//        ResponseEntity<?> response = donatorController.createDonator(badUser.getId(), donator);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        //assertEquals("Donator has not been created!", response.getBody());
//        assertEquals(new UserPermissionException(), response.getBody());
//    }

    @Test
    public void testCreateDonator_RequirementsNotMet() throws UserPermissionException, DonatorRequirementsException {
        Donor badDonor = new Donor();
        User goodUser = goodUser(1L);

        when(donorService.createDonator(goodUser.getId(), badDonor)).thenReturn(null);

        ResponseEntity<?> response = donorController.createDonator(goodUser.getId(), badDonor);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Donator has not been created!", response.getBody());
    }


    @Test
    public void testUpdateDonator() throws DonatorIdException, UserPermissionException, DonatorRequirementsException, DonatorNotFoundException {
        Donor donor = createGoodDonator(1L);
        Donor badDonor = createBadDonator();
        Donor updatedDonor = new Donor("ugfn1", "ugln1", "ugadn1", "ugmdn1");
        Donor badUpdatedDonor = new Donor(null, null, null, null);
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);

        when(donorService.updateDonator(eq(goodUser.getId()), eq(donor.getId()), any(Donor.class))).thenReturn(donor);

        ResponseEntity<?> goodResponse = donorController.updateDonator(goodUser.getId(), donor.getId(), updatedDonor);
        assertNotNull(goodResponse);
        assertEquals("Donator updated successfully!", goodResponse.getBody());

        // user with no permission
        ResponseEntity<?> permissionResponse = donorController.updateDonator(badUser.getId(), donor.getId(), updatedDonor);
        assertEquals("Donator has not been updated!", permissionResponse.getBody());

        // donator id
        ResponseEntity<?> idResponse = donorController.updateDonator(goodUser.getId(), badDonor.getId(), updatedDonor);
        assertEquals("Donator has not been updated!", idResponse.getBody());

        // donator requirements
        ResponseEntity<?> requirementsResponse = donorController.updateDonator(goodUser.getId(), badUpdatedDonor.getId(), badUpdatedDonor);
        assertEquals("Donator has not been updated!", requirementsResponse.getBody());

        // donator not found
        ResponseEntity<?> foundResponse = donorController.updateDonator(goodUser.getId(), 250L, updatedDonor);
        assertEquals("Donator has not been updated!", foundResponse.getBody());
    }

    @Test
    public void testDeleteDonatorById() throws DonatorIdException, UserPermissionException, DonatorNotFoundException {
        Long userId = 1L;
        Long donatorId = 1L;
        Long badId = 25L;
        Donor testDonor = createGoodDonator(donatorId);
        Donor noDonationsDonor = createGoodDonator(2L);

        // Mock the behavior of the service methods
        when(donorService.deleteDonatorById(eq(userId), eq(donatorId))).thenReturn(testDonor);
        when(donorService.deleteDonatorById(eq(userId), eq(noDonationsDonor.getId()))).thenReturn(noDonationsDonor);
        when(donorService.deleteDonatorById(eq(userId), eq(badId))).thenReturn(null);
        when(donationService.findDonationsByDonatorId(eq(donatorId))).thenReturn(true);
        when(donationService.findDonationsByDonatorId(eq(noDonationsDonor.getId()))).thenReturn(false);

        // Call the controller method
        ResponseEntity<?> response = donorController.deleteDonatorById(userId, donatorId);

        // Assertions
        assertNotNull(response);
        assertEquals("Donator values have been set to UNKNOWN!", response.getBody());

        response = donorController.deleteDonatorById(userId, noDonationsDonor.getId());
        assertNotNull(response);
        assertEquals("Donator values have been deleted!", response.getBody());

        response = donorController.deleteDonatorById(userId, 25L);
        assertEquals("Donator with given id does not exist!", response.getBody());
    }

}
