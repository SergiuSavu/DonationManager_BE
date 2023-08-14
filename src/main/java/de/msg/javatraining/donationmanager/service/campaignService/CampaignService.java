package de.msg.javatraining.donationmanager.service.campaignService;


import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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



    public Campaign createCampaign(Long userId, String name, String purpose){
        if (name == null || purpose == null)
            throw new IllegalArgumentException("Name or purpose cannot be null");

        Optional<User> userADMIN = userRepository.findById(userId);

        if(userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)){
                    if(campaignRepository.findCampaignByName(name).getName() == name){
                         throw new IllegalArgumentException("Not a unique name");
                    }
                    else {
                        Campaign campaign = new Campaign(name, purpose);
                        campaignRepository.save(campaign);
                        return campaign;
                    }
                }
                else throw new IllegalArgumentException("No permission to create a new campaign. ");
        }
    }
        throw new IllegalArgumentException("User not found.");
    }

    public Campaign updateCampaign(Long userId, Long campaignId, String name, String purpose) {
        // Campaign campaign1 = findById(id).orElseThrow(() -> new IllegalStateException(
        //        "User with id: " + id + " does not exist"
        // ));

        if (name == null || purpose == null)
            throw new IllegalArgumentException("Name or purpose cannot be null");

        if(campaignRepository.findCampaignByName(name).getName() == name){
            throw new IllegalArgumentException("Not a unique name");
        }
        Optional<User> userADMIN = userRepository.findById(userId);
        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    Optional<Campaign> campaign = campaignRepository.findById(campaignId);
                    if(campaign.isPresent()){
                        campaign.get().setName(name);
                        campaign.get().setPurpose(purpose);
                        campaignRepository.save(campaign.get());
                        return campaign.get();
                    }
                    else throw new IllegalArgumentException("Campaign not found.");
                }

            }
        }
        throw new IllegalArgumentException("User not found.");
    }

    public Campaign deleteCampaignById(Long userId, Long campaignId){
        if(userId == null || campaignId == null){
            throw new IllegalArgumentException("User ID or campaign ID cannot be null.");
        }

        Optional<User> userADMIN = userRepository.findById(userId);
        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    Optional<Campaign> campaign = campaignRepository.findById(campaignId);
                    if(campaign.isPresent()){
                        campaignRepository.deleteById(campaignId);
                        return campaign.get();
                    }
                    else throw new IllegalArgumentException("Campaign not found.");
                }
            }
        }
        throw new IllegalArgumentException("User not found.");
    }
}
