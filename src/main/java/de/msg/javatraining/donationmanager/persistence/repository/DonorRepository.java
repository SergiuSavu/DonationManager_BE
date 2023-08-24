package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    Optional<Donor> findById(Long id);
    @Override
    List<Donor> findAll();

    @Query("delete from Donor d where d.id=:id")
    @Modifying
    Optional<Donor> deleteDonatorById(Long id);
}
