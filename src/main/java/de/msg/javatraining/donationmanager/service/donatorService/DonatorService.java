package de.msg.javatraining.donationmanager.service.donatorService;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.DonationRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonatorRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonatorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonatorRepository donatorRepository;

    private final PermissionEnum permission = PermissionEnum.BENEF_MANAGEMENT;

    private boolean checkDonatorRequirements(Donator donator) {
        return donator.getFirstName() != null && donator.getLastName() != null;
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


    public List<Donator> getAllDonators() {
        return donatorRepository.findAll();
    }

    public Optional<Donator> getDonatorById(Long id) {
        return Optional.ofNullable(donatorRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Donator with id: " + id + " does not exist.")));
    }


    public Donator createDonator(Long userId, Donator donator) {
        if (checkDonatorRequirements(donator)) {
            if (checkUserPermission(userId, permission)) {
                donatorRepository.save(donator);
                return donator;
            } else {
                throw new IllegalStateException("User does not have the required permission!");
            }
        } else {
            throw new IllegalStateException("Donator requirements not met!");
        }
    }

    public Donator deleteDonatorById(Long userId, Long donatorId) throws EntityNotFoundException {
        if (donatorId == null) {
            throw new IllegalArgumentException("DonatorId cannot be null.");
        }
        if (checkUserPermission(userId, permission)) {
            Donator donator = donatorRepository.findById(donatorId)
                    .orElseThrow(() -> new EntityNotFoundException("Donator with ID: " + donatorId + " not found."));
            donatorRepository.deleteDonatorById(donatorId);
            return donator;
        }
        else {
            throw new IllegalArgumentException("User does not have permission to delete a donator!");
        }
    }


    public Donator updateDonator(Long userId, Long donatorId, Donator updatedDonator) {
        if (donatorId == null) {
            throw new IllegalArgumentException("DonatorId cannot be null.");
        }
        if (checkUserPermission(userId, permission)) {
            Donator donator = donatorRepository.findById(donatorId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Donator with id: " + donatorId + " does not exist!"
                    ));

            if (updatedDonator.getAdditionalName() != null) {
                donator.setAdditionalName(updatedDonator.getAdditionalName());
            }
            if (updatedDonator.getFirstName() != null) {
                donator.setFirstName(updatedDonator.getFirstName());
            }
            if (updatedDonator.getLastName() != null) {
                donator.setLastName(updatedDonator.getLastName());
            }
            if (updatedDonator.getMaidenName() != null) {
                donator.setMaidenName(updatedDonator.getMaidenName());
            }
            donatorRepository.save(donator);
            return donator;
        } else {
            throw new IllegalArgumentException("No permission to modify a donator");
        }
    }
}
