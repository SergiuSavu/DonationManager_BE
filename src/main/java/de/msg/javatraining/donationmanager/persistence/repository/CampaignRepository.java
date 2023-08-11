package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.CampaignModel.Campaign;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findAll();
    Campaign findCampaignByName(String name);

}
