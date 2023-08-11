package de.msg.javatraining.donationmanager.service.donatorService;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.persistence.repository.DonatorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonatorService {
    @Autowired
    private DonatorRepository donatorRepository;

    public List<Donator> getAllDonators() {
        return donatorRepository.findAll();
    }

    public Optional<Donator> getDonatorById(Long id) {
        return Optional.ofNullable(donatorRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Donator with id: " + id + " does not exist.")));
    }


    public Donator createDonator(Donator donator) {
        return donatorRepository.save(donator);
    }

    public Donator deleteDonatorById(Long id) throws EntityNotFoundException {
        Donator donator = donatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donator with ID: " + id + " not found."));

        donatorRepository.deleteById(id);
        return donator;
    }


    public void updateDonator(Long id, Donator updatedDonator) {
        Donator donator = donatorRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                   "Donator with id: " + id + " does not exist!"
                ));

        if (updatedDonator.getAdditionalName() != null) {
            donator.setAdditionalName(updatedDonator.getAdditionalName());
        }
        if (updatedDonator.getFirstName() != null) {
            donator.setFirstName(updatedDonator.getFirstName());
        }
        if (updatedDonator.getLastName() != null) {
            donator.setLastName(updatedDonator.getLastName());
        }
        if (updatedDonator.getMaidenName() != null) {
            donator.setMaidenName(updatedDonator.getMaidenName());
        }

        donatorRepository.save(donator);
    }
}
