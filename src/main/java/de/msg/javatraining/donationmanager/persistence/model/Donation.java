package de.msg.javatraining.donationmanager.persistence.model;

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
    private int idCampaign;
    private int idDonator; // who donated
    private int idCreatedBy; // user which created the donation
    private Date approveDate;
    private String notes;
    private Date createdDate;
    private int approvedBy; // who checked and approved the donation

}
