package de.msg.javatraining.donationmanager.service.donorService;

import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.repository.DonorRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private LogService logService;

    private final PermissionEnum permission = PermissionEnum.BENEF_MANAGEMENT;

    private boolean checkDonatorRequirements(Donor donor) {
        return donor.getFirstName() != null && donor.getLastName() != null;
    }

    private boolean checkUserPermission(Long userId, PermissionEnum requiredPermission) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            for (Role role : user.getRoles()) {
                if (role.getPermissions().contains(requiredPermission)) {
                    return true;
                }
            }
        }

        return false;
    }


    public List<Donor> getAllDonators() {
        return donorRepository.findAll();
    }

//    public Optional<Donator> getDonatorById(Long id) {
//        return Optional.ofNullable(donatorRepository.findById(id)
//                .orElseThrow(() -> new IllegalStateException("Donator with id: " + id + " does not exist.")));
//    }

    public List<Donor> getDonatorsByCampaignId(Long id){
        return donorRepository.findDonatorsByCampaignId(id);
    }


    // Asta este buna
//    public ResponseEntity<?> getDonatorById(Long id) {
//        Donator donator;
//        try {
//            if (donatorRepository.findById(id).isEmpty()) {
//                throw new DonatorNotFoundException();
//            }
//            donator = donatorRepository.findById(id).get();
//        } catch (DonatorNotFoundException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//        return ResponseEntity.ok(donator);
//    }

    public Donor getDonatorById(Long id) throws DonatorNotFoundException {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(DonatorNotFoundException::new);
        return donor;
    }


//    public Donator createDonator(Long userId, Donator donator) {
//        if (checkDonatorRequirements(donator)) {
//            if (checkUserPermission(userId, permission)) {
//                donatorRepository.save(donator);
//                return donator;
//            } else {
//                throw new IllegalStateException("User does not have the required permission!");
//            }
//        } else {
//            throw new IllegalStateException("Donator requirements not met!");
//        }
//    }

//    public ResponseEntity<?> createDonator(Long userId, Donator donator) {
//        try {
//            if (checkDonatorRequirements(donator)) {
//                if (checkUserPermission(userId, permission)) {
//                    donatorRepository.save(donator);
//                    return ResponseEntity.ok(donator);
//                } else {
//                    throw new UserPermissionException();
//                }
//            } else {
//                throw new DonatorRequirementsException();
//            }
//        } catch (UserPermissionException | DonatorRequirementsException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//    }

    public Donor createDonator(Long userId, Donor donor) throws
            UserPermissionException,
            DonatorRequirementsException {
        Optional<User> user = userRepository.findById(userId);
        if (checkDonatorRequirements(donor)) {
            if (checkUserPermission(userId, permission)) {
                donorRepository.save(donor);
                logService.logOperation("INSERT", "Created donator", user.get().getUsername());
                return donor;
            } else {
                logService.logOperation("ERROR", "Donator id can't be null!", user.get().getUsername());
                throw new UserPermissionException("User does not have the required permission/s!");
            }
        } else {
            logService.logOperation("ERROR", "Donator does not meet the requirements!", user.get().getUsername());
            throw new DonatorRequirementsException();
        }
    }


//    public Donator deleteDonatorById(Long userId, Long donatorId) throws EntityNotFoundException {
//        if (donatorId == null) {
//            throw new IllegalArgumentException("DonatorId cannot be null.");
//        }
//        if (checkUserPermission(userId, permission)) {
//            Donator donator = donatorRepository.findById(donatorId)
//                    .orElseThrow(() -> new EntityNotFoundException("Donator with ID: " + donatorId + " not found."));
//            donatorRepository.deleteDonatorById(donatorId);
//            return donator;
//        }
//        else {
//            throw new IllegalArgumentException("User does not have permission to delete a donator!");
//        }
//    }

//    public ResponseEntity<?> deleteDonatorById(Long userId, Long donatorId) {
//        try {
//            if (donatorId == null) {
//                throw new DonatorIdException();
//            }
//            Optional<Donator> donator = donatorRepository.findById(donatorId);
//            if (donator.isPresent()) {
//                if (checkUserPermission(userId, permission)) {
//                    donatorRepository.deleteById(donatorId);
//                    return ResponseEntity.ok(donator);
//                } else {
//                    throw new UserPermissionException();
//                }
//            } else {
//                throw new DonatorNotFoundException();
//            }
//        } catch (DonatorIdException
//                 | UserPermissionException
//                 | DonatorNotFoundException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//    }

    public Donor deleteDonatorById(Long userId, Long donatorId) throws
            DonatorIdException,
            DonatorNotFoundException,
            UserPermissionException {

        Optional<User> user = userRepository.findById(userId);

        if (donatorId == null) {
            logService.logOperation("ERROR", "Donator id can't be null!", user.get().getUsername());
            throw new DonatorIdException();
        }


        Donor donor = donorRepository.findById(donatorId)
                .orElseThrow(DonatorNotFoundException::new);

        if (checkUserPermission(userId, permission)) {
            donorRepository.deleteById(donatorId);
            logService.logOperation("DELETE", "Deleted donor", user.get().getUsername());
            return donor;
        } else {
            logService.logOperation("ERROR", "User does not have the required permission/s!", user.get().getUsername());
            throw new UserPermissionException();
        }
    }


//    public Donator updateDonator(Long userId, Long donatorId, Donator updatedDonator) {
//        if (donatorId == null) {
//            throw new IllegalArgumentException("DonatorId cannot be null.");
//        }
//        if (checkUserPermission(userId, permission)) {
//            Donator donator = donatorRepository.findById(donatorId)
//                    .orElseThrow(() -> new IllegalStateException(
//                            "Donator with id: " + donatorId + " does not exist!"
//                    ));
//
//            if (updatedDonator.getAdditionalName() != null) {
//                donator.setAdditionalName(updatedDonator.getAdditionalName());
//            }
//            if (updatedDonator.getFirstName() != null) {
//                donator.setFirstName(updatedDonator.getFirstName());
//            }
//            if (updatedDonator.getLastName() != null) {
//                donator.setLastName(updatedDonator.getLastName());
//            }
//            if (updatedDonator.getMaidenName() != null) {
//                donator.setMaidenName(updatedDonator.getMaidenName());
//            }
//            donatorRepository.save(donator);
//            return donator;
//        } else {
//            throw new IllegalArgumentException("No permission to modify a donator");
//        }
//    }


    public Donor updateDonator(Long userId, Long donatorId, Donor updatedDonor) throws
            DonatorIdException,
            UserPermissionException,
            DonatorRequirementsException,
            DonatorNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (donatorId == null) {
            logService.logOperation("ERROR", "Donator id can't be null!", user.get().getUsername());
            throw new DonatorIdException();
        }

        if (!checkUserPermission(userId, permission)) {
            logService.logOperation("ERROR", "User does not have the required permission/s!", user.get().getUsername());
            throw new UserPermissionException();
        }

        if (!checkDonatorRequirements(updatedDonor)) {
            logService.logOperation("ERROR", "Donator requirements not met!", user.get().getUsername());
            throw new DonatorRequirementsException();
        }

        Donor donor = donorRepository.findById(donatorId)
                .orElseThrow(DonatorNotFoundException::new);

        if (updatedDonor.getAdditionalName() != null) {
            donor.setAdditionalName(updatedDonor.getAdditionalName());
        }
        if (updatedDonor.getFirstName() != null) {
            donor.setFirstName(updatedDonor.getFirstName());
        }
        if (updatedDonor.getLastName() != null) {
            donor.setLastName(updatedDonor.getLastName());
        }
        if (updatedDonor.getMaidenName() != null) {
            donor.setMaidenName(updatedDonor.getMaidenName());
        }
        donorRepository.save(donor);
        logService.logOperation("UPDATE", "Updated donation", user.get().getUsername());
        return donor;
    }

//    public ResponseEntity<?> updateDonator(Long userId, Long donatorId, Donator updatedDonator) {
//        try {
//            if (donatorId == null) {
//                throw new DonatorIdException();
//            }
//            if (checkDonatorRequirements(updatedDonator)) {
//                if (checkUserPermission(userId, permission)) {
//                    Optional<Donator> donator = donatorRepository.findById(donatorId);
//                    if (donator.isPresent()) {
//                        if (updatedDonator.getAdditionalName() != null) {
//                            donator.get().setAdditionalName(updatedDonator.getAdditionalName());
//                        }
//                        if (updatedDonator.getFirstName() != null) {
//                            donator.get().setFirstName(updatedDonator.getFirstName());
//                        }
//                        if (updatedDonator.getLastName() != null) {
//                            donator.get().setLastName(updatedDonator.getLastName());
//                        }
//                        if (updatedDonator.getMaidenName() != null) {
//                            donator.get().setMaidenName(updatedDonator.getMaidenName());
//                        }
//                        donatorRepository.save(donator.get());
//                        return ResponseEntity.ok(donator);
//                    } else {
//                        throw new DonatorNotFoundException();
//                    }
//                } else {
//                    throw new UserPermissionException();
//                }
//            } else {
//                throw new DonatorRequirementsException();
//            }
//        } catch (DonatorIdException
//                 | DonatorNotFoundException
//                 | UserPermissionException
//                 | DonatorRequirementsException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//    }
}
