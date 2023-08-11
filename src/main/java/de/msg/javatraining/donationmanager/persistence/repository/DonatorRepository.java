package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonatorRepository extends JpaRepository<Donator, Long> {
    Optional<Donator> findById(Long id);
    @Override
    List<Donator> findAll();
}
