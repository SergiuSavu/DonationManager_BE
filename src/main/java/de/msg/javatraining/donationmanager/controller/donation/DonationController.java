package de.msg.javatraining.donationmanager.controller.donation;

import de.msg.javatraining.donationmanager.exceptions.donation.*;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
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
    public ResponseEntity<?> getDonation(@PathVariable("donationId") Long donationId) {
        try {
            Donation donation = donationService.getDonationById(donationId);
            return ResponseEntity.ok(donation);
        } catch (DonationNotFoundException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
        // return donationService.getDonationById(donationId);
    }

    @PostMapping("/{donatorId}/{campaignId}/{userId}")
    public ResponseEntity<?> createDonation(@PathVariable("userId") Long userId,
                                            @PathVariable("donatorId") Long donatorId,
                                            @PathVariable("campaignId") Long campaignId,
                                            @RequestBody Donation donation) {
        try {
            Donation don = donationService.createDonation(userId, donatorId, campaignId, donation);
            if (don != null) {
                return ResponseEntity.ok("Donation created successfully!");
            }
            return ResponseEntity.ok("Donation has not been created!");
        } catch (UserPermissionException | DonationRequirementsException | DonationException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }

    }

    @PutMapping("/{donationId}/{userId}")
    public ResponseEntity<?> updateDonation(@PathVariable("userId") Long userId,
                                            @PathVariable("donationId") Long donationId,
                                            @RequestBody Donation newDonation) throws
            DonationRequirementsException, DonationIdException, UserPermissionException, DonationNotFoundException, DonationApprovedException {

        try {
            Donation don = donationService.updateDonation(userId, donationId, newDonation);
            if (don != null) {
                return ResponseEntity.ok("Donation updated successfully!");
            }
            return ResponseEntity.ok("Donation has not been updated!");
        } catch (DonationRequirementsException
                 | DonationIdException
                 | UserPermissionException
                 | DonationNotFoundException
                 | DonationApprovedException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }

    }

    @DeleteMapping("/{donationId}/{userId}")
    public ResponseEntity<?> deleteDonationById(@PathVariable("userId") Long userId,
                                                @PathVariable("donationId") Long donationId) {

        try {
            Donation donation = donationService.getDonationById(donationId);
            if (donation != null) {
                if (!donation.isApproved()) {
                    donationService.deleteDonationById(userId, donationId);
                    return ResponseEntity.ok("Donation has been deleted!");
                }
                return ResponseEntity.ok("Can't delete a donation which has been approved!");
            }
            return ResponseEntity.ok("Donation with given id does not exist!");
        } catch (DonationIdException
                 | DonationNotFoundException
                 | DonationApprovedException
                 | UserPermissionException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }

    }

}
