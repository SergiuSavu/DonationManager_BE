package de.msg.javatraining.donationmanager.persistence.DonationModel;

import de.msg.javatraining.donationmanager.persistence.CampaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.DonatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Entity
@Table(name = "donation")
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key of table

    private float amount;
    private String currency; // maybe switch to another data type? Java has currency class

    @ManyToOne(fetch = FetchType.LAZY)
    private Campaign campaign; //campaignId

    @ManyToOne(fetch = FetchType.LAZY)
    private Donator donator; // who donated, donatorId

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // user which created the donation, createdById

    private Date approveDate;
    private String notes;
    private Date createdDate;
    private int approvedBy; // who checked and approved the donation

}
