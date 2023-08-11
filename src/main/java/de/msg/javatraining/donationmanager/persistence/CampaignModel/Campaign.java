package de.msg.javatraining.donationmanager.persistence.CampaignModel;


<<<<<<< HEAD
import jakarta.persistence.*;

=======
import de.msg.javatraining.donationmanager.persistence.DonationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

>>>>>>> 09e7e11 (Donation CRUD - working. Small changes to Capmaign, Role & User. UNIDIRECTIONAL BINDING.)

@Entity
@Table(	name = "campaign",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
@Data
@NoArgsConstructor

@Builder
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String purpose;


    public Campaign(String name, String purpose) {
        this.name = name;
        this.purpose = purpose;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}
