package de.msg.javatraining.donationmanager.service.donationService;

import de.msg.javatraining.donationmanager.exceptions.donation.DonationApprovedException;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationIdException;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.donation.*;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.persistence.donationModel.*;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonationRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonorRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    private final PermissionEnum permission = PermissionEnum.DONATION_MANAGEMENT;

    private boolean checkDonationRequirements(Donation donation) {
        return donation.getAmount() >= 0
                && donation.getCurrency() != null
                && donation.getCampaign() != null
                && donation.getCreatedBy() != null
                && donation.getDonor() != null;
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

    private boolean checkExistance(Long donatorId, Long campaignId) {
        Optional<Donor> donator = donorRepository.findById(donatorId);
        Optional<Campaign> campaign = campaignRepository.findById(campaignId);

        return donator.isPresent() && campaign.isPresent();
    }

//    public boolean checkDonationStatus(Long id) {
//        Donation donation = donationRepository.findById(id)
//                .orElseThrow(() -> new IllegalStateException(
//                        "Donation with id: " + id + " does not exist!"
//                ));
//        //System.out.println(donation.isApproved()); // de-bugging purposes
//        return donation.isApproved();
//    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    // FUNCTIA BUNA
//    public Optional<Donation> getDonationById(Long id) throws DonationNotFoundException {
//        return Optional.ofNullable(donationRepository.findById(id)
//                .orElseThrow(() -> new DonationNotFoundException("Donation not found")));
//    }

    public Donation getDonationById(Long id) throws DonationNotFoundException {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(DonationNotFoundException::new);
        return donation;
    }

//    public ResponseEntity<?> getDonationById(Long id) throws DonationNotFoundException {
//        Donation donation;
//        try {
//            if (donationRepository.findById(id).isEmpty()) {
//                throw new DonationNotFoundException();
//            }
//            donation = donationRepository.findById(id).get();
//        } catch (DonationNotFoundException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//        return ResponseEntity.ok(donation);
//    }

//    public Donation createDonation(Long userId, Long donatorId, Long campaignId, Donation donation) {
//        if (checkDonationRequirements(donation)) {
//            if (checkUserPermission(userId, permission)) {
//                Optional<User> user = userRepository.findById(userId);
//                Optional<Donator> donator = donatorRepository.findById(donatorId);
//                Optional<Campaign> campaign = campaignRepository.findById(campaignId);
//                user.ifPresent(donation::setCreatedBy);
//                donator.ifPresent(donation::setDonator);
//                campaign.ifPresent(donation::setCampaign);
//                donation.setCreatedDate(LocalDate.now());
//                donation.setApproved(false);
//                donationRepository.save(donation);
//                return donation;
//            } else {
//                throw new IllegalStateException("User does not have permission to create a donation!");
//            }
//        } else {
//            throw new IllegalStateException("Donation requirements not met!");
//        }
//    }

//    public ResponseEntity<?> createDonation(Long userId, Long donatorId, Long campaignId, Donation donation) {
//        try {
//            if (checkDonationRequirements(donation)) {
//                if (checkUserPermission(userId, permission)) {
//                    if (checkExistance(donatorId, campaignId)) {
//                        Optional<User> user = userRepository.findById(userId);
//                        Optional<Donator> donator = donatorRepository.findById(donatorId);
//                        Optional<Campaign> campaign = campaignRepository.findById(campaignId);
//                        user.ifPresent(donation::setCreatedBy);
//                        donator.ifPresent(donation::setDonator);
//                        campaign.ifPresent(donation::setCampaign);
//                        donation.setCreatedDate(LocalDate.now());
//                        donation.setApproved(false);
//                        donationRepository.save(donation);
//                        return ResponseEntity.ok(donation);
//                    }
//
//                } else {
//                    throw new UserPermissionException();
//                }
//            } else {
//                throw new DonationRequirementsException();
//            }
//        } catch (UserPermissionException | DonationRequirementsException exception) {
//            return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
//        }
//    }

//    public ResponseEntity<?> createDonation(Long userId, Long donatorId, Long campaignId, Donation donation) {
//        try {
//            if (!checkDonationRequirements(donation)) {
//                throw new DonationRequirementsException();
//            }
//
//            if (!checkUserPermission(userId, permission)) {
//                throw new UserPermissionException();
//            }
//
//            if (!checkExistance(donatorId, campaignId)) {
//                throw new IllegalArgumentException();
//            }
//
//            Optional<User> user = userRepository.findById(userId);
//            Optional<Donator> donator = donatorRepository.findById(donatorId);
//            Optional<Campaign> campaign = campaignRepository.findById(campaignId);
//
//            if (user.isPresent() && donator.isPresent() && campaign.isPresent()) {
//                donation.setCreatedBy(user.get());
//                donation.setDonator(donator.get());
//                donation.setCampaign(campaign.get());
//                donation.setCreatedDate(LocalDate.now());
//                donation.setApproved(false);
//                donationRepository.save(donation);
//                return ResponseEntity.ok(donation);
//            } else {
//                throw new IllegalArgumentException();
//            }
//        } catch (DonationRequirementsException | UserPermissionException | IllegalArgumentException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//    }

    public Donation approveDonation(Long donationId, Long userId) throws DonationNotFoundException, UserNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException();
        }
        if (donationRepository.findById(donationId).isEmpty()) {
            throw new DonationNotFoundException();
        }

        Donation donation = donationRepository.findById(donationId).get();
        User user = userRepository.findById(userId).get();
        donation.setApprovedBy(user);
        donation.setApproved(true);
        donation.setApproveDate(LocalDate.now());
        donationRepository.save(donation);
        return donation;
    }

    public Donation createDonation(Long userId, Long donatorId, Long campaignId, Donation donation) throws
            DonationRequirementsException,
            UserPermissionException,
            DonationException {
        if (!checkDonationRequirements(donation)) {
            throw new DonationRequirementsException();
        }

        if (!checkUserPermission(userId, permission)) {
            throw new UserPermissionException();
        }

        if (!checkExistance(donatorId, campaignId)) {
            throw new DonationException("Problem with DonatorId or CampaignId");
        }

        Optional<User> user = userRepository.findById(userId);
        Optional<Donor> donator = donorRepository.findById(donatorId);
        Optional<Campaign> campaign = campaignRepository.findById(campaignId);

        if (user.isPresent() && donator.isPresent() && campaign.isPresent()) {
            donation.setCreatedBy(user.get());
            donation.setDonor(donator.get());
            donation.setCampaign(campaign.get());
            donation.setCreatedDate(LocalDate.now());
            donation.setApproved(false);
            donationRepository.save(donation);
            List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                    new NotificationParameter(String.valueOf(donation.getAmount()))
            ));
            notificationService.saveNotification(user.get(), parameters, NotificationType.DONATION_APPROVED);

            return donation;
        } else {
            throw new DonationException("User and/or donator and/or campaign not present!");
        }
    }



//    public Donation deleteDonationById(Long userId, Long donationId) {
//        if (donationId == null) {
//            throw new IllegalArgumentException("DonationId cannot be null!");
//        }
//        Optional<Donation> donation = donationRepository.findById(donationId);
//        if (donation.isPresent()) {
//            if (!donation.get().isApproved()) {
//                if (checkUserPermission(userId, permission)) {
//                    donationRepository.deleteById(donationId);
//                    return donation.get();
//                } else {
//                    throw new IllegalArgumentException("User does not have permission to delete a donation!");
//                }
//            } else {
//                throw new IllegalArgumentException("Donation has already been approved! Can't delete an approved donation!");
//            }
//        } else {
//            throw new EntityNotFoundException("Donation does not exist!");
//        }
////        if (!checkDonationStatus(donationId)) {
////            if (checkUserPermission(userId, permission)) {
////                Donation donation = donationRepository.findById(donationId)
////                        .orElseThrow(() -> new EntityNotFoundException("Donation with id: " + donationId + " not found!"));
////                donationRepository.deleteById(donationId);
////                return donation;
////            } else {
////                throw new IllegalArgumentException("User does not have permission to delete a donation!");
////            }
////        } else {
////            throw new IllegalArgumentException("Donation has already been approved! Can't delete an approved donation!");
////        }
//    }


    public Donation deleteDonationById(Long userId, Long donationId) throws
            DonationIdException,
            DonationNotFoundException,
            DonationApprovedException,
            UserPermissionException {
        if (donationId == null) {
            throw new DonationIdException();
        }

        Optional<Donation> donation = donationRepository.findById(donationId);
                //.orElseThrow(DonationNotFoundException::new);

        if (donation.isEmpty()) {
            throw new DonationNotFoundException();
        }

        if (donation.get().isApproved()) {
            throw new DonationApprovedException();
        }

        if (!checkUserPermission(userId, permission)) {
            throw new UserPermissionException();
        }

        donationRepository.deleteById(donationId);
        return donation.get();
    }

//    public ResponseEntity<?> deleteDonationById(Long userId, Long donationId) {
//        try {
//            if (donationId == null) {
//                throw new DonationIdException();
//            }
//            Optional<Donation> donation = donationRepository.findById(donationId);
//            if (donation.isPresent()) {
//                if (!donation.get().isApproved()) {
//                    if (checkUserPermission(userId, permission)) {
//                        donationRepository.deleteById(donationId);
//                        return ResponseEntity.ok(donation);
//                    } else {
//                        throw new UserPermissionException();
//                    }
//                } else {
//                    throw new DonationApprovedException();
//                }
//            } else {
//                throw new DonationNotFoundException();
//            }
//        } catch (DonationIdException
//                 | UserPermissionException
//                 | DonationApprovedException
//                 | DonationNotFoundException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//    }

//    public Donation updateDonation(Long userId, Long donationId, Donation updatedDonation) {
//        if (donationId == null) {
//            throw new IllegalArgumentException("DonationId cannot be null!");
//        }
//        if (checkDonationRequirements(updatedDonation)) {
//            if (checkUserPermission(userId, permission)) {
//                //if (!checkDonationStatus(donationId)) {
////                    Donation donation = donationRepository.findById(donationId)
////                            .orElseThrow(() -> new IllegalStateException(
////                                    "Donation with id: " + donationId + " does not exist!"
////                            ));
//                    Optional<Donation> donation = donationRepository.findById(donationId);
//                    if (donation.isPresent()) {
//                        if (!donation.get().isApproved()) {
//                            if (updatedDonation.getAmount() != 0) {
//                                donation.get().setAmount(updatedDonation.getAmount());
//                            }
//                            if (updatedDonation.getCurrency() != null) {
//                                donation.get().setCurrency(updatedDonation.getCurrency());
//                            }
//                            if (updatedDonation.getCampaign() != null) {
//                                donation.get().setCampaign(updatedDonation.getCampaign());
//                            }
//                            if (updatedDonation.getDonator() != null) {
//                                donation.get().setDonator(updatedDonation.getDonator());
//                            }
//                            if (updatedDonation.getNotes() != null) {
//                                donation.get().setNotes(updatedDonation.getNotes());
//                            }
//                            if (updatedDonation.getCreatedDate() != null) {
//                                donation.get().setCreatedDate(updatedDonation.getCreatedDate());
//                            }
//                            if (updatedDonation.getCreatedBy() != null) {
//                                donation.get().setCreatedBy(updatedDonation.getCreatedBy());
//                            }
//                            donationRepository.save(donation.get());
//                            return donation.get();
//                        } else {
//                            throw new IllegalArgumentException("Can't modify an approved donation!");
//                        }
//                    } else {
//                        throw new IllegalArgumentException("Donation does not exist!");
//                    }
//            } else {
//                throw new IllegalArgumentException("No permission to modify a donation!");
//            }
//        } else {
//            throw new IllegalArgumentException("Updated donation requirements not met!");
//        }
//    }

//    public ResponseEntity<?> updateDonation(Long userId, Long donationId, Donation updatedDonation) {
//        try {
//            if (donationId == null) {
//                throw new DonationIdException();
//            }
//            if (checkDonationRequirements(updatedDonation)) {
//                if (checkUserPermission(userId, permission)) {
//                    Optional<Donation> donation = donationRepository.findById(donationId);
//                    if (donation.isPresent()) {
//                        if (!donation.get().isApproved()) {
//                            if (updatedDonation.getAmount() != 0) {
//                                donation.get().setAmount(updatedDonation.getAmount());
//                            }
//                            if (updatedDonation.getCurrency() != null) {
//                                donation.get().setCurrency(updatedDonation.getCurrency());
//                            }
//                            if (updatedDonation.getCampaign() != null) {
//                                donation.get().setCampaign(updatedDonation.getCampaign());
//                            }
//                            if (updatedDonation.getDonator() != null) {
//                                donation.get().setDonator(updatedDonation.getDonator());
//                            }
//                            if (updatedDonation.getNotes() != null) {
//                                donation.get().setNotes(updatedDonation.getNotes());
//                            }
//                            if (updatedDonation.getCreatedDate() != null) {
//                                donation.get().setCreatedDate(updatedDonation.getCreatedDate());
//                            }
//                            if (updatedDonation.getCreatedBy() != null) {
//                                donation.get().setCreatedBy(updatedDonation.getCreatedBy());
//                            }
//                            donationRepository.save(donation.get());
//                            return ResponseEntity.ok(donation);
//                        } else {
//                            throw new DonationApprovedException();
//                        }
//                    } else {
//                        throw new DonationNotFoundException();
//                    }
//                } else {
//                    throw new UserPermissionException();
//                }
//            } else {
//                throw new DonationRequirementsException();
//            }
//        } catch (DonationIdException
//                 | DonationApprovedException
//                 | DonationNotFoundException
//                 | UserPermissionException
//                 | DonationRequirementsException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//    }

    public Donation updateDonation(Long userId, Long donationId, Donation updatedDonation) throws
            DonationIdException,
            DonationRequirementsException,
            UserPermissionException,
            DonationNotFoundException,
            DonationApprovedException {

        // Check if donationId is null
        if (donationId == null) {
            throw new DonationIdException();
        }

        // Check if the updated donation meets the requirements
        if (!checkDonationRequirements(updatedDonation)) {
            throw new DonationRequirementsException();
        }

        // Check user permission
        if (!checkUserPermission(userId, permission)) {
            throw new UserPermissionException();
        }

        // Find the donation by ID
        Optional<Donation> donation = donationRepository.findById(donationId);

        // Check if the donation exists
        if (donation.isEmpty()) {
            throw new DonationNotFoundException();
        }

        // Check if the donation is not approved
        if (donation.get().isApproved()) {
            throw new DonationApprovedException();
        }

        // Update the donation fields
        Donation existingDonation = donation.get();
        if (updatedDonation.getAmount() != 0) {
            existingDonation.setAmount(updatedDonation.getAmount());
        }
        if (updatedDonation.getCurrency() != null) {
            existingDonation.setCurrency(updatedDonation.getCurrency());
        }
        if (updatedDonation.getCampaign() != null) {
            existingDonation.setCampaign(updatedDonation.getCampaign());
        }
        if (updatedDonation.getDonor() != null) {
            existingDonation.setDonor(updatedDonation.getDonor());
        }
        if (updatedDonation.getNotes() != null) {
            existingDonation.setNotes(updatedDonation.getNotes());
        }
        if (updatedDonation.getCreatedDate() != null) {
            existingDonation.setCreatedDate(updatedDonation.getCreatedDate());
        }
        if (updatedDonation.getCreatedBy() != null) {
            existingDonation.setCreatedBy(updatedDonation.getCreatedBy());
        }

        // Save the updated donation
        donationRepository.save(existingDonation);
        return existingDonation;
    }


    public boolean findDonationsByDonatorId(Long donatorId) {
        try {
            List<Donation> donations = donationRepository.findByDonorId(donatorId);
            return donations.size() > 0;
        } catch (IllegalStateException exception) {
            System.out.println("Donator doesn't have donations or doesn't exist");
        }
        return false;
    }

    public boolean findDonationsByCampaignId(Long id){
        int counter=0;
        try {
            List<Donation> donations = donationRepository.findDonationsByCampaignId(id);
            for(Donation d : donations){
                if(d.getAmount()!=0)
                    counter++;

            }

            return counter != 0;

        }catch (Exception e){
            System.out.println("problema la donatie");
            return false;
        }

    }
    public List<Donation> getDonationsByCampaignId(Long id){
        return donationRepository.findDonationsByCampaignId(id);
    }
}
