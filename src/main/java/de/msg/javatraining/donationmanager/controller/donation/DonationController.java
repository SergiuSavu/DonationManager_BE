package de.msg.javatraining.donationmanager.controller.donation;

import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
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
    public Optional<Donation> getDonation(@PathVariable("donationId") Long donationId) {
        return donationService.getDonationById(donationId);
    }

    @PostMapping("/{donatorId}/{campaignId}/{userId}")
    public ResponseEntity<?> createDonation(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId, @PathVariable("campaignId") Long campaignId, @RequestBody Donation donation) {
        Donation don = donationService.createDonation(userId, donatorId, campaignId, donation);
        if (don != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{donationId}/{userId}")
    public ResponseEntity<?> updateDonation(@PathVariable("userId") Long userId, @PathVariable("donationId") Long donationId, @RequestBody Donation newDonation) {
        Donation don = donationService.updateDonation(userId, donationId, newDonation);
        if (don != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{donationId}/{userId}")
    public ResponseEntity<?> deleteDonationById(@PathVariable("userId") Long userId, @PathVariable("donationId") Long donationId) {
        Optional<Donation> donation = donationService.getDonationById(donationId);
        if (donation.isPresent()) {
            if (!donation.get().isApproved()) {
                donationService.deleteDonationById(userId, donationId);
                return new ResponseEntity<>("Donation has been successfully deleted!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Can't delete a donation which has been approved!", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("Donation with given id does not exist!", HttpStatus.FORBIDDEN);
        }
    }
}
