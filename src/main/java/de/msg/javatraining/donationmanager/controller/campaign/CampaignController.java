package de.msg.javatraining.donationmanager.controller.campaign;

import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private DonationService donationService;


    @GetMapping()
    public List<Campaign> getALlCampaigns(){
        return campaignService.getAllCampaigns();
    }
    @PostMapping("/{userId}") ///campaign/userId
    public ResponseEntity<?> createCapmaign(@PathVariable Long userId,@RequestBody Campaign campaign){

        ResponseEntity<?> camp = campaignService.createCampaign(userId,campaign.getName(),campaign.getPurpose());
        if(camp.getStatusCode() == HttpStatus.OK){
            return ResponseEntity.ok("Campaign created successfully!");
        }else{
            return ResponseEntity.ok("Campaign has not been created!");
        }



    }

    @PutMapping("/{campId}/{userId}")
    public ResponseEntity<?> updateCampaign(@PathVariable("campId") Long campId,@PathVariable("userId") Long userId, @RequestBody Campaign newCampaign){



        ResponseEntity<?> camp =campaignService.updateCampaign(userId,campId, newCampaign.getName(), newCampaign.getPurpose());

            if(camp.getStatusCode() == HttpStatus.OK){
                return ResponseEntity.ok("Campaign updated successfully!");
            }else{
                return ResponseEntity.ok("Campaign has not been updated!");
            }
    }

    @DeleteMapping("/{campId}/{userId}")
    public ResponseEntity<?> deleteCampaignById(@PathVariable("campId") Long campId,@PathVariable("userId") Long userId){
        if(!donationService.findDonationsByCampaignId(campId))
        {
            ResponseEntity<?> camp = campaignService.deleteCampaignById(userId,campId);
            if(camp.getStatusCode() == HttpStatus.OK){
                return ResponseEntity.ok("Campaign has been deleted!");
            }else{
                return ResponseEntity.ok("Campaign can't be deleted!");
            }

        }
        else
            return ResponseEntity.ok("Deletion failed: Campaign has paid Donations!");
    }
}
