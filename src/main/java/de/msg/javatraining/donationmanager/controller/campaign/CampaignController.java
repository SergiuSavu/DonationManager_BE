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

        Campaign camp = campaignService.createCampaign(userId,campaign.getName(),campaign.getPurpose());
        if(camp!=null){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }



    }

    @PutMapping("/{campId}/{userId}")
    public ResponseEntity<?> updateCampaign(@PathVariable("campId") Long campId,@PathVariable("userId") Long userId, @RequestBody Campaign newCampaign){



        Campaign camp =campaignService.updateCampaign(userId,campId, newCampaign.getName(), newCampaign.getPurpose());

            if(camp!=null){
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
    }

    @DeleteMapping("/{campId}/{userId}")
    public ResponseEntity<?> deleteCampaignById(@PathVariable("campId") Long campId,@PathVariable("userId") Long userId){
        if(!donationService.findDonationsByCampaignId(campId))
        {
            Campaign camp = campaignService.deleteCampaignById(userId,campId);
            if(camp!=null){
                return new ResponseEntity<>("Campaign deleted successfully",HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Campaign cant be deleted",HttpStatus.FORBIDDEN);
            }

        }
        else
            return new ResponseEntity<>("Deletion failed: Campaign has paid Donations",HttpStatus.FORBIDDEN);


    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteCampaignByCampaign(@RequestBody Campaign campaign,@PathVariable("userId") Long userId){
        if(!donationService.findDonationsByCampaignId(campaign.getId()))
        {
            Campaign camp = campaignService.deleteCampaignById(userId,campaign.getId());
            if(camp!=null){
                return new ResponseEntity<>("Campaign deleted successfully",HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Campaign cant be deleted",HttpStatus.FORBIDDEN);
            }

        }
        else
            return new ResponseEntity<>("Deletion failed: Campaign has paid Donations",HttpStatus.FORBIDDEN);


    }
}
