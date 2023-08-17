package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DonatorRepository extends JpaRepository<Donator, Long> {
    Optional<Donator> findById(Long id);
    @Override
    List<Donator> findAll();

    @Query("delete from Donator d where d.id=:id")
    @Modifying
    Optional<Donator> deleteDonatorById(Long id);
}
