package de.msg.javatraining.donationmanager.service.campaignService;

import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {
    @Autowired
    private CampaignRepository campaignRepository;


    public List<Campaign> getAllCampaigns(){
        return campaignRepository.findAll() ;
    }



    public void createCampaign(Campaign campaign){
        campaignRepository.save(campaign);
    }

    public void updateCampaign(Long id, Campaign newCampaign){
        Campaign campaign1 = findById(id).orElseThrow(() -> new IllegalStateException(
                "User with id: " + id + " does not exist"
        ));

        if (newCampaign.getName() != null)
            campaign1.setName(newCampaign.getName());

        if(newCampaign.getPurpose()!=null)
            campaign1.setPurpose(newCampaign.getPurpose());

        campaignRepository.save(campaign1);

    }

    public Optional<Campaign> findById(Long id){
       return  campaignRepository.findById(id);
    }

    public void deleteCampaignById(Long id){
        campaignRepository.deleteById(id);

    }


}
