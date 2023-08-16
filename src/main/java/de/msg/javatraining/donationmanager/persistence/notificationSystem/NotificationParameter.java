package de.msg.javatraining.donationmanager.persistence.notificationSystem;

import jakarta.persistence.*;

@Entity
@Table(name = "parameter")
public class NotificationParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    public NotificationParameter() {
    }

    public NotificationParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
