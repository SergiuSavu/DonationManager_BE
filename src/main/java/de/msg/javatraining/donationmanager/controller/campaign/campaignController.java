package de.msg.javatraining.donationmanager.controller.campaign;

import de.msg.javatraining.donationmanager.persistence.CampaignModel.Campaign;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
    public void createCapmaign(@RequestBody Campaign campaign){

        try{

            campaignService.createCampaign(campaign);

        }catch (Exception e){
            System.out.println("invalid username   try again");
        }



    }

    @PutMapping("/{id}")
    public void updateCampaign(@PathVariable("id") Long id, @RequestBody Campaign newCampaign){

        try{

            campaignService.updateCampaign(id,newCampaign);

        }catch (Exception e){
            System.out.println("invalid username   try again");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteCampaignById(@PathVariable("id") Long id){
        campaignService.deleteCampaignById(id);
    }
}
