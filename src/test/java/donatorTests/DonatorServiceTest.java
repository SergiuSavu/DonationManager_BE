//package donatorTests;
//
//import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
//import de.msg.javatraining.donationmanager.persistence.model.ERole;
//import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
//import de.msg.javatraining.donationmanager.persistence.model.Role;
//import de.msg.javatraining.donationmanager.persistence.model.user.User;
//import de.msg.javatraining.donationmanager.persistence.repository.DonatorRepository;
//import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
//import de.msg.javatraining.donationmanager.service.donatorService.DonatorService;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//public class DonatorServiceTest {
//
//    @InjectMocks
//    private DonatorService donatorService;
//
//    @Mock
//    private DonatorRepository donatorRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    private User goodUser(Long userId) {
//        PermissionEnum permission = PermissionEnum.BENEF_MANAGEMENT;
//        Set<PermissionEnum> permissionEnums = new HashSet<>();
//        permissionEnums.add(permission);
//
//        Set<Role> roles = new HashSet<>();
//        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
//        roles.add(role);
//
//        User user = new User(userId,"testUser1", "testUser1", "1234567899", "goodUser", "test1@example.com", "psswd1", true, false, 1, roles, new HashSet<>());
//        return user;
//    }
//
//    private User badUser(Long userId) {
//        PermissionEnum permission = PermissionEnum.CAMP_REPORTING;
//        Set<PermissionEnum> permissionEnums = new HashSet<>();
//        permissionEnums.add(permission);
//
//        Set<Role> roles = new HashSet<>();
//        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
//        roles.add(role);
//
//        User user = new User(userId,"testuser2", "testUser2", "1234567800", "badUser", "test2@example.com", "psswd2", true, false, 1, roles, new HashSet<>());
//        return user;
//    }
//
//    private Donator makeDonator(Long donatorId) {
//        Donator donator = new Donator("fn1", "ln1", "adn1", "mdn1");
//        donator.setId(donatorId);
//        return donator;
//    }
//
//    private Donator makeBadDonator() {
//        return new Donator();
//    }
//
//    @Test
//    public void testGetAllDonators() {
//        List<Donator> donators = new ArrayList<>(
//                Arrays.asList(
//                        new Donator("fn1", "ln1", "adn1", "mdn1"),
//                        new Donator("fn2", "ln2", "adn2", "mdn2")
//                )
//        );
//
//        when(donatorRepository.findAll()).thenReturn(donators);
//
//        List<Donator> donatorList = donatorRepository.findAll();
//        assertNotNull(donatorList);
//        assertFalse(donatorList.isEmpty());
//        assertEquals(2, donatorList.size());
//        assertEquals("fn1", donatorList.get(0).getFirstName());
//        assertEquals("ln2", donatorList.get(1).getLastName());
//    }
//
//    @Test
//    public void testGetDonatorById() {
//        Donator donator = new Donator();
//        donator.setId(1L);
//        donator.setFirstName("fn1");
//        donator.setLastName("ln1");
//        donator.setAdditionalName("adn1");
//        donator.setMaidenName("mdn1");
//
//        when(donatorRepository.findById(1L)).thenReturn(Optional.of(donator));
//
//        Optional<Donator> don = donatorService.getDonatorById(1L);
//        assertNotNull(don);
//        don.ifPresent(value -> assertEquals("fn1", value.getFirstName()));
//        don.ifPresent(value -> assertEquals("ln1", value.getLastName()));
//        don.ifPresent(value -> assertEquals("adn1", value.getAdditionalName()));
//        don.ifPresent(value -> assertEquals("mdn1", value.getMaidenName()));
//    }
//
//    @Test
//    public void testCreateDonator() {
//        Donator donator = makeDonator(1L);
//        User goodUser = goodUser(1L);
//        User badUser = badUser(2L);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(goodUser));
//        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
//        when(donatorRepository.save(donator)).thenReturn(donator);
//
//        Donator createdDonator = donatorService.createDonator(goodUser.getId(), donator);
//
//        assertNotNull(createdDonator);
//        assertThrows(IllegalStateException.class, () -> {
//            donatorService.createDonator(goodUser.getId(), new Donator(null, "ln", "s", "d"));
//        });
//        assertThrows(IllegalStateException.class, () -> {
//           donatorService.createDonator(badUser.getId(), new Donator("fn1", "ln1", "adn1", "mdn1"));
//        });
//    }
//
//    @Test
//    public void testDeleteDonatorById() {
//        Donator donator = makeDonator(1L);
//        Donator don = makeDonator(null);
//        User goodUser = goodUser(1L);
//        User badUser = badUser(2L);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(goodUser));
//        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
//        when(donatorRepository.deleteDonatorById(1L)).thenReturn(Optional.of(donator));
//
//        assertEquals(donatorRepository.deleteDonatorById(1L), Optional.of(donator));
//
//        assertThrows(EntityNotFoundException.class, () -> {
//            donatorService.deleteDonatorById(goodUser.getId(), 25L);
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            donatorService.deleteDonatorById(goodUser.getId(), don.getId());
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            donatorService.deleteDonatorById(badUser.getId(), donator.getId());
//        });
//    }
//
//    @Test
//    public void testUpdateDonator() {
//        Donator donator = makeDonator(1L);
//        Donator updon = new Donator("upfn", "upln", "upadnm", "upmdn");
//        updon.setId(1L);
//        Donator don = makeDonator(null);
//        User goodUser = goodUser(1L);
//        User badUser = badUser(2L);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(goodUser));
//        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
//        when(donatorRepository.findById(1L)).thenReturn(Optional.of(donator));
//        when(donatorRepository.save(donator)).thenReturn(donator);
//
//        // everything works
//        assertEquals(donatorService.updateDonator(1L, 1L, updon), donator);
//
//        // donor id is null
//        assertThrows(IllegalArgumentException.class, () -> {
//            donatorService.updateDonator(goodUser.getId(), don.getId(), updon);
//        });
//
//        // donor id not found
//        assertThrows(IllegalStateException.class, () -> {
//            donatorService.updateDonator(goodUser.getId(), 25L, updon);
//        });
//
//        // user without permission is trying to udpate donor
//        assertThrows(IllegalArgumentException.class, () -> {
//            donatorService.updateDonator(badUser.getId(), donator.getId(), updon);
//        });
//    }
//}
