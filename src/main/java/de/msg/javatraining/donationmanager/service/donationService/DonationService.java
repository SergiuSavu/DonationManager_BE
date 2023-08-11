package de.msg.javatraining.donationmanager.service.donationService;

import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Optional<Donation> getDonation(Long id) {
        return donationRepository.findById(id);
    }

    public void createDonation(Donation donation) {
        donationRepository.save(donation);
    }

    public void deleteDonationById(Long id) {
        donationRepository.deleteById(id);
    }

    public void updateDonation(Long id, Donation updatedDonation) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                   "Donation with id: " + id + " does not exist!"
                ));

        if (updatedDonation.getAmount() <= 0) {
            donation.setAmount(updatedDonation.getAmount());
        }
        if (updatedDonation.getCurrency() != null) {
            donation.setCurrency(updatedDonation.getCurrency());
        }
        if (updatedDonation.getCampaign() != null) {
            donation.setCampaign(updatedDonation.getCampaign());
        }
        if (updatedDonation.getDonator() != null) {
            donation.setDonator(updatedDonation.getDonator());
        }
        if (updatedDonation.getApproveDate() != null) {
            donation.setApproveDate(updatedDonation.getApproveDate());
        }
        if (updatedDonation.getNotes() != null) {
            donation.setNotes(updatedDonation.getNotes());
        }
        if (updatedDonation.getCreatedDate() != null) {
            donation.setCreatedDate(updatedDonation.getCreatedDate());
        }
        donationRepository.save(donation);
    }
}
