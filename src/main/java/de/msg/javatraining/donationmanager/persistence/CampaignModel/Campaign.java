package de.msg.javatraining.donationmanager.persistence.CampaignModel;


import de.msg.javatraining.donationmanager.persistence.model.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(	name = "campaign",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String purpose;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_campaign",
            joinColumns = @JoinColumn(name = "campaign_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    public Campaign(){}

    public Campaign(String name, String purpose, Set<User> users) {
        this.name = name;
        this.purpose = purpose;
        this.users = users;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
