package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.DonationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findById(Long id);
    @Override
    List<Donation> findAll();
}
