package de.msg.javatraining.donationmanager.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(	name = "donator",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "firstName"),
                @UniqueConstraint(columnNames = "lastName"),
                @UniqueConstraint(columnNames = "additionalName"),
                @UniqueConstraint(columnNames = "maidenName")
        })
public class Donator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Donator(Long id) {
        this.id = id;
    }

    public Donator(){}


}
