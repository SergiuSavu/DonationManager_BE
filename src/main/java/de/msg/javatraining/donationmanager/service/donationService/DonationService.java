package de.msg.javatraining.donationmanager.service.donationService;

import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonationRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonatorRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DonatorRepository donatorRepository;

    @Autowired
    private UserRepository userRepository;

    private final PermissionEnum permission = PermissionEnum.DONATION_MANAGEMENT;

    private boolean checkDonationRequirements(Donation donation) {
        return donation.getAmount() >= 0
                && donation.getCurrency() != null
                && donation.getCampaign() != null
                && donation.getCreatedBy() != null
                && donation.getCreatedDate() != null
                && donation.getDonator() != null;
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

    public Optional<Donation> getDonationById(Long id) {
        return Optional.ofNullable(donationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Donation with id: " + id + " does not exist!")));
    }

    public Donation createDonation(Long userId, Long donatorId, Long campaignId, Donation donation) {
        if (checkDonationRequirements(donation)) {
            if (checkUserPermission(userId, permission)) {
                Optional<User> user = userRepository.findById(userId);
                Optional<Donator> donator = donatorRepository.findById(donatorId);
                Optional<Campaign> campaign = campaignRepository.findById(campaignId);
                user.ifPresent(donation::setCreatedBy);
                donator.ifPresent(donation::setDonator);
                campaign.ifPresent(donation::setCampaign);
                donation.setCreatedDate(LocalDate.now());
                donation.setApproved(false);
                donationRepository.save(donation);
                return donation;
            } else {
                throw new IllegalStateException("User does not have permission to create a donation!");
            }
        } else {
            throw new IllegalStateException("Donation requirements not met!");
        }
    }

    public Donation deleteDonationById(Long userId, Long donationId) {
        if (donationId == null) {
            throw new IllegalArgumentException("DonationId cannot be null!");
        }
        Optional<Donation> donation = donationRepository.findById(donationId);
        if (donation.isPresent()) {
            if (!donation.get().isApproved()) {
                if (checkUserPermission(userId, permission)) {
                    donationRepository.deleteById(donationId);
                    return donation.get();
                } else {
                    throw new IllegalArgumentException("User does not have permission to delete a donation!");
                }
            } else {
                throw new IllegalArgumentException("Donation has already been approved! Can't delete an approved donation!");
            }
        } else {
            throw new EntityNotFoundException("Donation does not exist!");
        }
//        if (!checkDonationStatus(donationId)) {
//            if (checkUserPermission(userId, permission)) {
//                Donation donation = donationRepository.findById(donationId)
//                        .orElseThrow(() -> new EntityNotFoundException("Donation with id: " + donationId + " not found!"));
//                donationRepository.deleteById(donationId);
//                return donation;
//            } else {
//                throw new IllegalArgumentException("User does not have permission to delete a donation!");
//            }
//        } else {
//            throw new IllegalArgumentException("Donation has already been approved! Can't delete an approved donation!");
//        }
    }

    public Donation updateDonation(Long userId, Long donationId, Donation updatedDonation) {
        if (donationId == null) {
            throw new IllegalArgumentException("DonationId cannot be null!");
        }
        if (checkDonationRequirements(updatedDonation)) {
            if (checkUserPermission(userId, permission)) {
                //if (!checkDonationStatus(donationId)) {
//                    Donation donation = donationRepository.findById(donationId)
//                            .orElseThrow(() -> new IllegalStateException(
//                                    "Donation with id: " + donationId + " does not exist!"
//                            ));
                    Optional<Donation> donation = donationRepository.findById(donationId);
                    if (donation.isPresent()) {
                        if (!donation.get().isApproved()) {
                            if (updatedDonation.getAmount() != 0) {
                                donation.get().setAmount(updatedDonation.getAmount());
                            }
                            if (updatedDonation.getCurrency() != null) {
                                donation.get().setCurrency(updatedDonation.getCurrency());
                            }
                            if (updatedDonation.getCampaign() != null) {
                                donation.get().setCampaign(updatedDonation.getCampaign());
                            }
                            if (updatedDonation.getDonator() != null) {
                                donation.get().setDonator(updatedDonation.getDonator());
                            }
                            if (updatedDonation.getNotes() != null) {
                                donation.get().setNotes(updatedDonation.getNotes());
                            }
                            if (updatedDonation.getCreatedDate() != null) {
                                donation.get().setCreatedDate(updatedDonation.getCreatedDate());
                            }
                            if (updatedDonation.getCreatedBy() != null) {
                                donation.get().setCreatedBy(updatedDonation.getCreatedBy());
                            }
                            donationRepository.save(donation.get());
                            return donation.get();
                        } else {
                            throw new IllegalArgumentException("Can't modify an approved donation!");
                        }
                    } else {
                        throw new IllegalArgumentException("Donation does not exist!");
                    }
            } else {
                throw new IllegalArgumentException("No permission to modify a donation!");
            }
        } else {
            throw new IllegalArgumentException("Updated donation requirements not met!");
        }
    }

    public boolean findDonationsByDonatorId(Long donatorId) {
        try {
            List<Donation> donations = donationRepository.findByDonatorId(donatorId);
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
}
