package de.msg.javatraining.donationmanager.controller.campaign;

import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
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


    @GetMapping()
    public List<Campaign> getALlCampaigns(){
        return campaignService.getAllCampaigns();
    }
    @PostMapping()
    public ResponseEntity<Void> createCapmaign(@PathVariable Long userId, @PathVariable String name, @PathVariable String purpose){
        Campaign c = campaignService.createCampaign(userId,name,purpose);
        if (c!=null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if permission was not granted
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCampaign(@PathVariable Long userId, @PathVariable Long campaignId, @PathVariable String name, @PathVariable String purpose){
        Campaign c = campaignService.updateCampaign(userId, campaignId,name,purpose);
        if (c!=null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if permission was not granted
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteCampaignById(@PathVariable("id") Long userId, @PathVariable Long campaignId){
        Campaign c = campaignService.deleteCampaignById(userId,campaignId);
        if (c!=null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if permission was not granted
        }
    }
}
