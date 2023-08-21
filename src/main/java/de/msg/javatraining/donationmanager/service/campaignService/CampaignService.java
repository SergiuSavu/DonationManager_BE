package de.msg.javatraining.donationmanager.service.campaignService;


import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignIdException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNameException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignRequirementsException;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.exceptions.user.UserIdException;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampaignRepository campaignRepository;


    public List<Campaign> getAllCampaigns(){
        return campaignRepository.findAll() ;
    }

    public ResponseEntity<?> createCampaign(Long userId, String name, String purpose) {
        try {
            if (name == null || purpose == null) {
                throw new CampaignRequirementsException();
            }

            Optional<User> userADMIN = userRepository.findById(userId);

            if (userADMIN.isPresent()) {
                PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
                boolean hasAdminPermission = false;

                for (Role adminRole : userADMIN.get().getRoles()) {
                    if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                        hasAdminPermission = true;
                        break;
                    }
                }

                if (hasAdminPermission) {
                    if (campaignRepository.findCampaignByName(name) != null) {
                        throw new CampaignNameException();
                    } else {
                        Campaign campaign = new Campaign(name, purpose);
                        campaignRepository.save(campaign);
                        return ResponseEntity.ok(campaign);
                    }
                } else {
                    throw new UserPermissionException();
                }
            } else {
                throw new UserNotFoundException();
            }
        } catch (CampaignRequirementsException
                 | CampaignNameException
                 | UserPermissionException
                 | UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
        }
    }



//    public Campaign createCampaign(Long userId, String name, String purpose){
//        if (name == null || purpose == null)
//            throw new IllegalArgumentException("Name or purpose cannot be null");
//
//        Optional<User> userADMIN = userRepository.findById(userId);
//
//        if(userADMIN.isPresent()) {
//            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
//            for (Role adminRole : userADMIN.get().getRoles()) {
//                if (adminRole.getPermissions().contains(adminPermissionToCheck)){
//                    if(campaignRepository.findCampaignByName(name)!=null ){
//                         throw new IllegalArgumentException("Not a unique name");
//                    }
//                    else {
//                        Campaign campaign = new Campaign(name, purpose);
//                        campaignRepository.save(campaign);
//                        return campaign;
//                    }
//                }
//                else throw new IllegalArgumentException("No permission to create a new campaign. ");
//        }
//    }
//        throw new IllegalArgumentException("User not found.");
//    }

    public ResponseEntity<?> updateCampaign(Long userId, Long campaignId, String name, String purpose) {
        try {
            if (name == null || purpose == null) {
                throw new CampaignRequirementsException();
            }

            Optional<User> userADMIN = userRepository.findById(userId);

            if (userADMIN.isPresent()) {
                PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
                boolean hasAdminPermission = false;

                for (Role adminRole : userADMIN.get().getRoles()) {
                    if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                        hasAdminPermission = true;
                        break;
                    }
                }

                if (hasAdminPermission) {
                    Optional<Campaign> campaignOptional = campaignRepository.findById(campaignId);

                    if (campaignOptional.isPresent()) {
                        Campaign campaign = campaignOptional.get();

                        if (!campaign.getName().equals(name)) {
                            // Check for uniqueness of the new name
                            if (campaignRepository.findCampaignByName(name) != null) {
                                throw new CampaignNameException();
                            }
                        }

                        campaign.setName(name);
                        campaign.setPurpose(purpose);
                        campaignRepository.save(campaign);

                        return ResponseEntity.ok(campaign);
                    } else {
                        throw new CampaignNotFoundException();
                    }
                } else {
                    throw new UserPermissionException();
                }
            } else {
                throw new UserNotFoundException();
            }
        } catch (CampaignRequirementsException
                 | CampaignNameException
                 | CampaignNotFoundException
                 | UserPermissionException
                 | UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
        }
    }


//    public Campaign updateCampaign(Long userId, Long campaignId, String name, String purpose) {
//        // Campaign campaign1 = findById(id).orElseThrow(() -> new IllegalStateException(
//        //        "User with id: " + id + " does not exist"
//        // ));
//
//        if (name == null || purpose == null)
//            throw new IllegalArgumentException("Name or purpose cannot be null");
//
//        if(campaignRepository.findCampaignByName(name) != null){
//            throw new IllegalArgumentException("Not a unique name");
//        }
//        Optional<User> userADMIN = userRepository.findById(userId);
//
//        if (userADMIN.isPresent()) {
//            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
//            for (Role adminRole : userADMIN.get().getRoles()) {
//                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
//                    Optional<Campaign> campaign = campaignRepository.findById(campaignId);
//                    if(campaign.isPresent()){
//                        campaign.get().setName(name);
//                        campaign.get().setPurpose(purpose);
//                        campaignRepository.save(campaign.get());
//                        return campaign.get();
//                    }
//                    else throw new IllegalArgumentException("Campaign not found.");
//
//                }
//                else throw new IllegalArgumentException("No permission to modify a  campaign. ");
//
//            }
//        }
//        throw new IllegalArgumentException("User not found1.");
//    }


    public ResponseEntity<?> deleteCampaignById(Long userId, Long campaignId) {
        try {
            if (userId == null) {
                throw new UserIdException();
            }

            if (campaignId == null) {
                throw new CampaignIdException();
            }

            Optional<User> userADMIN = userRepository.findById(userId);
            if (userADMIN.isPresent()) {
                PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
                for (Role adminRole : userADMIN.get().getRoles()) {
                    if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                        Optional<Campaign> campaign = campaignRepository.findById(campaignId);
                        if(campaign.isPresent()){
                            campaignRepository.deleteById(campaignId);
                            return ResponseEntity.ok(campaign);
                        }
                        else
                            throw new CampaignNotFoundException();
                    }
                    else
                        throw new UserPermissionException();
                }
            }
            throw new UserNotFoundException();
        } catch (UserIdException | CampaignIdException | CampaignNotFoundException | UserPermissionException | UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
        }
    }


//    public Campaign deleteCampaignById(Long userId, Long campaignId){
//        if(userId == null || campaignId == null){
//            throw new IllegalArgumentException("User ID or campaign ID cannot be null.");
//        }
//
//        Optional<User> userADMIN = userRepository.findById(userId);
//        if (userADMIN.isPresent()) {
//            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
//            for (Role adminRole : userADMIN.get().getRoles()) {
//                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
//                    Optional<Campaign> campaign = campaignRepository.findById(campaignId);
//                    if(campaign.isPresent()){
//                        campaignRepository.deleteById(campaignId);
//                        return campaign.get();
//                    }
//                    else throw new IllegalArgumentException("Campaign not found.");
//                }
//                else throw new IllegalArgumentException("No permission to delete a campaign. ");
//
//            }
//        }
//        throw new IllegalArgumentException("User not found.");
//    }


}
