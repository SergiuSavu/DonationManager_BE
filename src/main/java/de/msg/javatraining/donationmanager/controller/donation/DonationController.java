package de.msg.javatraining.donationmanager.controller.donation;

import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationNotFoundException;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping()
    public List<Donation> getAllDonations() {
        return donationService.getAllDonations();
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<?> getDonation(@PathVariable("donationId") Long donationId) throws DonationNotFoundException {
        return donationService.getDonationById(donationId);
    }

    @PostMapping("/{donatorId}/{campaignId}/{userId}")
    public ResponseEntity<?> createDonation(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId, @PathVariable("campaignId") Long campaignId, @RequestBody Donation donation) {
        ResponseEntity<?> don = donationService.createDonation(userId, donatorId, campaignId, donation);
        if (don.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("Donation created successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Donation has not been created!");
        }
    }

    @PutMapping("/{donationId}/{userId}")
    public ResponseEntity<?> updateDonation(@PathVariable("userId") Long userId, @PathVariable("donationId") Long donationId, @RequestBody Donation newDonation) {
        ResponseEntity<?> don = donationService.updateDonation(userId, donationId, newDonation);
        if (don.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("Donation updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Donation has not been updated!");
        }
    }

    @DeleteMapping("/{donationId}/{userId}")
    public ResponseEntity<?> deleteDonationById(@PathVariable("userId") Long userId, @PathVariable("donationId") Long donationId) throws DonationNotFoundException {
        Optional<Donation> donation = (Optional<Donation>) donationService.getDonationById(donationId).getBody();
        //ResponseEntity<?> donation = donationService.getDonationById(donationId);
        if (donation.isPresent()) {
            if (!donation.get().isApproved()) {
                donationService.deleteDonationById(userId, donationId);
                return ResponseEntity.ok("Donation has been successfully deleted!");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can't delete a donation which has been approved!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Donation with given id does not exist!");
        }
    }
}
