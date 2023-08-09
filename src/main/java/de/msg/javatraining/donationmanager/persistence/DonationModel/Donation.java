package de.msg.javatraining.donationmanager.persistence.DonationModel;

import de.msg.javatraining.donationmanager.persistence.CampaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.model.Donator;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign")
    private Campaign campaign; //campaignId

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donator")
    private Donator donator; // who donated, donatorId

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "createdBy")
    private User createdBy; // user which created the donation, createdById

    private Date approveDate;
    private String notes;
    private Date createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approvedBy")
    private User approvedBy; // who checked and approved the donation
}
