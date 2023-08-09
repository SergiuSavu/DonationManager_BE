package de.msg.javatraining.donationmanager.controller.campaign;

import de.msg.javatraining.donationmanager.persistence.CampaignModel.Campaign;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaign")
public class campaignController {

    @Autowired
    private CampaignService campaignService;


    @GetMapping()
    public List<Campaign> getALlCampaigns(){
        return campaignService.getAllCampaigns();
    }
    @PostMapping()
    public void creataCapaign(@RequestBody Campaign campaign){
        campaignService.createCampaign(campaign);
    }

    @PutMapping("/{id}")
    public void updateCampaign(@PathVariable("id") Long id, @RequestBody Campaign newCampaign){
        campaignService.updateCampaign(id,newCampaign);
    }

    @DeleteMapping("/{id}")
    public void deleteCampaignBuId(@PathVariable("id") Long id){
        campaignService.deleteCampaignById(id);
    }
}
